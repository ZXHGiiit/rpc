package org.rpc.client;

import org.rpc.common.RpcDecoder;
import org.rpc.common.RpcEncoder;
import org.rpc.common.RpcRequest;
import org.rpc.common.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Created by xinghang on 17/9/22.
 * RPC客户端
 */
public class RpcClient extends SimpleChannelInboundHandler<RpcResponse> {
  private static final Logger LOGGER = LoggerFactory.getLogger(RpcClient.class);

  private String host;
  private int port;

  private RpcResponse response;

  private final Object obj = new Object();

  /**
   * 异常处理
   */
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    LOGGER.error("RpcClient caught exception", cause);
    ctx.close();
  }

  public RpcClient(String host, int port) {
    this.host = host;
    this.port = port;
  }

  public RpcResponse send(RpcRequest request) throws Exception {
    EventLoopGroup group = new NioEventLoopGroup();
    try {
      Bootstrap bootstrap = new Bootstrap();
      bootstrap.group(group).channel(NioSocketChannel.class)
        .handler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) throws Exception {
            // 向pipeline中添加编码、解码、业务处理的handler
            ch.pipeline()
              .addLast(new RpcEncoder(RpcRequest.class))  //OUT - 1
              .addLast(new RpcDecoder(RpcResponse.class)) //IN - 1
              .addLast(RpcClient.this);                   //IN - 2
          }
        }).option(ChannelOption.SO_KEEPALIVE, true);
      //链接服务器  阻塞的方式
      ChannelFuture future = bootstrap.connect(host, port).sync();
      //将request对象写入outbundle处理，经过RpcEncoder编码
      future.channel().writeAndFlush(request).sync();

      synchronized (obj) {
        obj.wait();//收不到线程响应，在此等待. channelRead0会处理服务器响应，并唤醒线程
      }

      if (response != null) {
        //
        future.channel().closeFuture().sync();
      }
      return response;
    } finally {
      group.shutdownGracefully();
    }
  }

  /**
   * 读取服务器返回结果
   */
  @Override
  protected void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
    this.response = response;
    synchronized (obj) {
      obj.notifyAll();
    }
  }
}
