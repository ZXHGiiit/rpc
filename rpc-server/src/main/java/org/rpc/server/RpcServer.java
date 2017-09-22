package org.rpc.server;

import org.apache.commons.collections4.MapUtils;
import org.rpc.common.RpcDecoder;
import org.rpc.common.RpcEncoder;
import org.rpc.common.RpcRequest;
import org.rpc.common.RpcResponse;
import org.rpc.registry.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by xinghang on 17/9/22.
 * RPC服务器。
 * 通过spring-bean的方式注入到用户的业务系统中
 * 实现了ApplicationContextAware InitializingBean
 * Spring构造本对象时，会调用setApplicationContext()方法，通过自定义注解获取用户的业务接口
 * 还会调用afterPropertiesSet()方法，启动Netty服务器
 */
public class RpcServer implements ApplicationContextAware, InitializingBean {
  private static final Logger LOGGER = LoggerFactory.getLogger(RpcServer.class);

  private String serverAddress;

  private ServiceRegistry serviceRegistry;

  //用于存储业务接口和实现类的对象（由Spring所构造）
  private Map<String, Object> handlerMap = new HashMap<>();

  public RpcServer(String serverAddress) {
    this.serverAddress = serverAddress;
  }

  /**
   * 通过注解，获取标注了rpc服务注解的业务类，放入handlerMap中
   */
  @Override
  public void setApplicationContext(ApplicationContext context) throws BeansException {
    Map<String, Object> serviceBeanMap = context.getBeansWithAnnotation(RpcService.class);
    if (MapUtils.isNotEmpty(serviceBeanMap)) {
      for (Object serviceBean : serviceBeanMap.values()) {
        String interfaceName = serviceBean.getClass().getAnnotation(RpcService.class).value()
          .getName();
        handlerMap.put(interfaceName, serviceBean);
      }
    }
  }

  /**
   * 在此启动netty服务，绑定handle流水线
   * 1、接收对象数据，反序列化得到request对象
   * 2、根据request中的参数，让RpcHandler从handlerMap中找到对象的业务imple，调用指定方法，获取返回值
   * 3、将业务调用结果封装到response并序列化发送到客户端
   */
  @Override
  public void afterPropertiesSet() throws Exception {
    //NioEventLoopGroup是处理io操作的多线程事件环，即为Netty4的线程池
    EventLoopGroup bossGroup = new NioEventLoopGroup();
    EventLoopGroup workGroup = new NioEventLoopGroup();
    try {
      ServerBootstrap bootstrap = new ServerBootstrap();
      bootstrap.group(bossGroup, workGroup)
        .channel(NioServerSocketChannel.class)
        .childHandler(new ChannelInitializer<SocketChannel>() {
          @Override
          public void initChannel(SocketChannel channel)
            throws Exception {
            // 向pipeline中添加编码、解码、业务处理的handler
            channel.pipeline()
              .addLast(new RpcDecoder(RpcRequest.class))// 注册解码 IN-1
              .addLast(new RpcEncoder(RpcResponse.class))// 注册编码 OUT
              .addLast(new RpcHandler(handlerMap));//注册RpcHandler IN-2
          }
        }).option(ChannelOption.SO_BACKLOG, 128)
        .childOption(ChannelOption.SO_KEEPALIVE, true);

      String[] array = serverAddress.split(":");
      String host = array[0];
      int port = Integer.parseInt(array[1]);
      //启动RPC服务
      ChannelFuture future = bootstrap.bind(host, port);
      LOGGER.info("server started on port{}", port);
      //注册rpc服务
      if (serviceRegistry != null) {
        serviceRegistry.register(serverAddress);
      }
      //给channel增加一个管道关闭的监听器并同步阻塞，直到channel关闭
      future.channel().closeFuture().sync();
    } catch (Exception e) {
      LOGGER.error("RpcServer.ServerBootStrap启动失败", e);
    } finally {
      workGroup.shutdownGracefully();
      bossGroup.shutdownGracefully();
    }
  }
}
