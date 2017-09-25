package org.rpc.sample.server;

import org.rpc.sample.api.HelloService;
import org.rpc.sample.api.Person;
import org.rpc.server.RpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xinghang on 17/9/25.
 */

@RpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {
  private static final Logger LOGGER = LoggerFactory.getLogger(HelloServiceImpl.class);

  @Override
  public String hello(String name) {
    LOGGER.info("TestServiceImpl.hello.begin==========>");
    String result = "Hello! " + name;
    LOGGER.info("调用服务端接口实现，业务处理结果为：" + result);
    return result;
  }

  @Override
  public String hello(Person person) {
    LOGGER.info("TestServiceImpl.hello.degin==========>");
    String result = "Hello! " + person.getFirstName() + person.getLastName();
    LOGGER.info("调用服务端接口实现，业务处理结果为: " + result);
    return result;
  }
}
