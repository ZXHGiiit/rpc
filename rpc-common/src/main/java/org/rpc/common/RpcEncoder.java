package org.rpc.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by xinghang on 17/9/21.
 * RPC 编码器
 */
public class RpcEncoder extends MessageToByteEncoder {
  private Class<?> genericClass;

  //构造函数传入要序列化的class
  public RpcEncoder(Class<?> genericClass) {
    this.genericClass = genericClass;
  }

  @Override
  protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
    //序列化
    if (genericClass.isInstance(msg)) {
      byte[] data = SerializableUtil.serialize(msg);
      out.writeInt(data.length);
      out.readBytes(data);
    }
  }
}
