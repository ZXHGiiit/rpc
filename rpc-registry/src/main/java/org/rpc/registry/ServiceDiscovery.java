package org.rpc.registry;


import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by xinghang on 17/9/21.
 * 本类用于client发现server节点的变化，实现负载均衡
 */
public class ServiceDiscovery {
  private static final Logger LOGGER = LoggerFactory.getLogger(ServiceDiscovery.class);

  private CountDownLatch latch = new CountDownLatch(1);

  private volatile List<String> dataList = new ArrayList<>();

  private String registryAddress;

  /**
   * ZK链接
   */
  public ServiceDiscovery(String registryAddress) {
    this.registryAddress = registryAddress;
    ZooKeeper zk = connectServer();
    if (zk != null) {
      watchNode(zk);
    }
  }

  /**
   * 发现新节点
   */
  public String discover() {
    String data = null;
    int size = dataList.size();
    if (size > 0) {
      if (size == 1) {
        data = dataList.get(0);
        LOGGER.debug("ServiceDiscovery.discover.using only data: {}", data);
      } else {
        data = dataList.get(ThreadLocalRandom.current().nextInt(size));
        LOGGER.debug("ServiceDiscovery.discover.using random data:{}", data);
      }
    }
    return data;
  }

  /**
   * 链接
   */
  public ZooKeeper connectServer() {
    ZooKeeper zk = null;
    try {
      zk = new ZooKeeper(registryAddress, Constant.ZK_SESSION_TIMEOUT,
        new Watcher() {
          @Override
          public void process(WatchedEvent watchedEvent) {
            if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
              latch.countDown();
            }
          }
        });
      latch.await();
    } catch (Exception e) {
      LOGGER.error("ServiceDiscovery.connectServer.ERROR", e);
    }
    return zk;
  }

  /**
   * 监听
   */
  private void watchNode(final ZooKeeper zk) {
    try {
      List<String> nodeList = zk.getChildren(Constant.ZK_REGISTRY_PATH, new Watcher() {
        @Override
        public void process(WatchedEvent watchedEvent) {
          //节点改变
          if (watchedEvent.getType() == Event.EventType.NodeChildrenChanged) {
            watchNode(zk);
          }
        }
      });
      List<String> dataList = new ArrayList<>();
      for (String node : nodeList) {
        byte[] bytes = zk.getData(Constant.ZK_REGISTRY_PATH + "/"
          + node, false, null);
        dataList.add(new String(bytes));
      }
      LOGGER.info("ServiceDiscovery node date: {}", dataList);
      this.dataList = dataList;
    } catch (Exception e) {
      LOGGER.error("ServiceDiscovery", e);
    }
  }

}
