package org.rpc.server;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by xinghang on 17/9/22.
 * RPC服务注解类，标注在服务实现类中
 */

@Target({ElementType.TYPE})//注解用在接口上
@Retention(RetentionPolicy.RUNTIME)//VM将在运行期也保留注释，因此可以通过反射机制读取注释的信息
@Component
public @interface RpcService {
  /**
   * 服务接口类
   */
  Class<?> value();
}
