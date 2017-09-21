package org.rpc.common;

/**
 * Created by xinghang on 17/9/21.
 * RPC请求
 * 封装发送的object的反射属性
 */
public class RpcRequest {
  private String requestId;
  private String className;
  private String methodName;
  private Class<?>[] paraneterTypes;
  private Object[] parameters;

  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public String getMethodName() {
    return methodName;
  }

  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }

  public Class<?>[] getParaneterTypes() {
    return paraneterTypes;
  }

  public void setParaneterTypes(Class<?>[] paraneterTypes) {
    this.paraneterTypes = paraneterTypes;
  }

  public Object[] getParameters() {
    return parameters;
  }

  public void setParameters(Object[] parameters) {
    this.parameters = parameters;
  }
}
