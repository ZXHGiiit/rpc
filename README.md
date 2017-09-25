# 分布式 RPC 框架

## 前言

        该项目为本人在公司实习学习RPC框架写的简单实现，供大家参考学习。
        参考自：https://gitee.com/huangyong/rpc

## 项目介绍

        RPC（Remote Procedure Call Protocol）——远程过程调用协议，它是一种通过网络从远程计算机程序上请求服务，
    而不需要了解底层网络技术的协议。RPC协议假定某些传输协议的存在，如TCP或UDP，为通信程序之间携带信息数据。在OSI
    网络通信模型中，RPC跨越了传输层和应用层。RPC使得开发包括网络分布式多程序在内的应用程序更加容易。
        为了提高并发，使用NIO，这里使用Netty。Netty 是一个基于NIO的客户、服务器端编程框架，屏蔽了java底层的NIO
    细节。
        使用Spring实现依赖注入。
        使用Protostuff实现序列化，比jdk原生序列化更高效。
            参考：http://www.protostuff.io/
        使用Zookeeper搭建集群，实现服务的发现与注册功能。

## 组织结构

``` lua
rpc
├── rpc-common -- RPC框架公共模块，提供序列化与反序列化，编码与解码功能
├── rpc-registry --实现服务的发现与注册
├── rpc-client -- 通过动态代理封装实现代理类的请求，发送给服务端，接收服务端相应
├── rpc-server -- 接收客户端请求，处理请求，发送响应
├── rpc-sample-client --
└── rpc-sample-server --

```

## 实现细节