package org.rpc.server;

import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

import org.rpc.common.RpcRequest;
import org.rpc.common.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by xinghang on 17/9/22.
 * 具体业务的调用
 * 通过构造是传入的"业务接口以及实现"handlerMap,来调用客户端说请求的业务方法
 * 并将业务方法返回值封装成response对象写入下一个handler（即编码handler--RpcEnEncoder）
 */
public class RpcHandler extends SimpleChannelInboundHandler<RpcRequest> {
  private static final Logger LOGGER = LoggerFactory.getLogger(RpcHandler.class);

  private final Map<String, Object> handlerMap;

  public RpcHandler(Map<String, Object> handlerMap) {
    this.handlerMap = handlerMap;
  }

  /**
   * 接收消息，处理消息，返回结果
   */
  @Override
  protected void channelRead0(ChannelHandlerContext ctx, RpcRequest msg) throws Exception {
    RpcResponse response = new RpcResponse();
    response.setRequestId(msg.getRequestId());
    try {
      Object result = handle(msg);
      response.setResult(result);
    } catch (Throwable throwable) {
      response.setError(throwable);
    }
    //写入 RPC响应，并自动关闭连接
    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
  }

  /**
   * 更具request来处理具体业务调用
   * 调用通过CGLib反射
   *
   * @return 返回执行结果
   */
  private Object handle(RpcRequest request) throws Throwable {
    String className = request.getClassName();
    Object serviceBean = handlerMap.get(className);
    if (serviceBean == null) {
      throw new RuntimeException(String.format("can not find service bean by key: %s",
        serviceBean));
    }

    //获取反射调用所需的参数
    Class<?> serviceClass = serviceBean.getClass();
    String methodName = request.getMethodName();
    Class<?>[] parameterTypes = request.getParameterTypes();
    Object[] parameters = request.getParameters();

    //使用CGLib进行反射调用
    FastClass fastClass = FastClass.create(serviceClass);
    FastMethod method = fastClass.getMethod(methodName, parameterTypes);//通过方法名和参数类型获取方法
    return method.invoke(serviceBean, parameters);
  }
}
