package org.rpc.sample.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rpc.client.RpcProxy;
import org.rpc.sample.api.HelloService;
import org.rpc.sample.api.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by xinghang on 17/9/25.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring.xml")
public class RpcTest {
  //private static final Logger LOGGER = LoggerFactory.getLogger(RpcTest.class);
  private static final Log LOGGER = LogFactory.getLog(RpcTest.class);
  @Autowired
  private RpcProxy rpcProxy;

  @Test
  public void helloTest1() {
    LOGGER.debug("RpcTest.helloTest1.begin==========>");

    //调用代理的create方法，代理HelloService接口
    HelloService helloService = rpcProxy.create(HelloService.class);

    //调用代理的方法，执行invoke
    String result = helloService.hello("RPC");
    LOGGER.info("服务端返回结果为============>");
    LOGGER.info("RpcTest.helloTest1.result: " + result);
  }

  @Test
  public void helloTest2() {
    LOGGER.info("RpcTest.helloTest2.begin==========>");
    HelloService helloService = rpcProxy.create(HelloService.class);
    String result = helloService.hello(new Person("Zhou", "Xinghang"));
    LOGGER.info("服务端返回结果为============>");
    LOGGER.info("RpcTest.helloTest2.result: " + result);
  }
}
