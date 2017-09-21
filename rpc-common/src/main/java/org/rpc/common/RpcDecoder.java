package org.rpc.common;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * Created by xinghang on 17/9/21.
 * RPC 解码器
 */
public class RpcDecoder extends ByteToMessageDecoder {
  private Class<?> genericClass;

  //构造函数传入要序列化的class
  public RpcDecoder(Class<?> genericClass) {
    this.genericClass = genericClass;
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object
    > out) throws Exception {
    if (in.readableBytes() < 4) {
      return;
    }
    in.markReaderIndex();
    int datalength = in.readInt();
    if (datalength < 0) {
      ctx.close();
    }
    if (in.readableBytes() < datalength) {
      in.resetReaderIndex();
    }
    //将ByteBuf转换为byte[]
    byte[] data = new byte[datalength];
    in.readBytes(data);
    //对象序列化为数组
    Object obj = SerializableUtil.deserialize(data, genericClass);
    out.add(obj);
  }
}
