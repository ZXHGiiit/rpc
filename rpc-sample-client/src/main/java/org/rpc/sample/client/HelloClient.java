package org.rpc.sample.client;

import org.rpc.client.RpcProxy;
import org.rpc.sample.api.HelloService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by xinghang on 17/9/25.
 */
public class HelloClient {
  public static void main(String[] args) throws Exception {
    ApplicationContext context = new ClassPathXmlApplicationContext("spring.xml");
    RpcProxy rpcProxy = context.getBean(RpcProxy.class);

    HelloService helloService = rpcProxy.create(HelloService.class);
    String result = helloService.hello("World");
    System.out.println(result);

    HelloService helloService2 = rpcProxy.create(HelloService.class);
    String result2 = helloService2.hello("世界");
    System.out.println(result2);

    System.exit(0);
  }
}
