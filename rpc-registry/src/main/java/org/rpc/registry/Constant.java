package org.rpc.registry;

/**
 * Created by xinghang on 17/9/21.
 * 常量
 */
public class Constant {
  public static final int ZK_SESSION_TIMEOUT = 5000;//zk超时

  public static final String ZK_REGISTRY_PATH = "/registry";//ZK注册节点

  public static final String ZK_DATA_PATH = ZK_REGISTRY_PATH + "/data";//节点
}
