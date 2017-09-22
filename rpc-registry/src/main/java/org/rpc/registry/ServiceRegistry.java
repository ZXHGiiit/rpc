package org.rpc.registry;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * Created by xinghang on 17/9/21.
 * 服务注册，ZK在该架构中扮演了"服务注册表"的角色，用于注册所有服务器的地址与端口，并对客户端提供服务发现的功能
 */
public class ServiceRegistry {
  private static final Logger LOGGER = LoggerFactory.getLogger(ServiceRegistry.class);

  private CountDownLatch latch = new CountDownLatch(1);

  private String registryAddress;

  public ServiceRegistry(String registryAddress) {
    //Zookeeper的地址
    this.registryAddress = registryAddress;
  }

  /**
   * 创建Zookeeper链接
   */
  public void register(String data) {
    if (data != null) {
      ZooKeeper zk = connectServer();
      if (zk != null) {

      }
    }
  }

  /**
   * 创建Zookeeper链接，监听
   */
  private ZooKeeper connectServer() {
    ZooKeeper zk = null;
    try {
      zk = new ZooKeeper(registryAddress, Constant.ZK_SESSION_TIMEOUT, new Watcher() {
        @Override
        public void process(WatchedEvent watchedEvent) {
          if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
            latch.countDown();//计数器-1
          }
        }
      });
      latch.await();//阻塞当前线程，直到计数器为0
    } catch (Exception e) {
      LOGGER.error("", e);
    }
    return zk;
  }

  /**
   * 创建节点
   */
  private void createNode(ZooKeeper zk, String data) {
    try {
      byte[] bytes = data.getBytes();
      if (zk.exists(Constant.ZK_REGISTRY_PATH, null) == null) {
        zk.create(Constant.ZK_REGISTRY_PATH, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode
          .PERSISTENT_SEQUENTIAL);
      }
    } catch (Exception e) {
      LOGGER.error("", e);
    }
  }

}
