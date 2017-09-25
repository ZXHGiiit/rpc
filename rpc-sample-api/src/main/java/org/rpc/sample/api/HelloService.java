package org.rpc.sample.api;

/**
 * Created by xinghang on 17/9/25.
 */
public interface HelloService {
  String hello(String name);

  String hello(Person person);
}
