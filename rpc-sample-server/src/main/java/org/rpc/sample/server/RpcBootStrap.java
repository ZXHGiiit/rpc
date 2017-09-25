package org.rpc.sample.server;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by xinghang on 17/9/25.
 * 用户系统服务端的启动入口
 * 其意义是启动springcontext。从而构造框架中的RpcServer
 * 将用户系统中所有标注了RpcService注解的业务类发布到RpcServer中
 */
public class RpcBootStrap {
  public static void main(String[] args) {
    new ClassPathXmlApplicationContext("spring.xml");
  }
}
