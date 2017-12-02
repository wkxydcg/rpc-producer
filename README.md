# rpc-provider
一个基于http协议的rpc服务端 采用zookeeper作为注册中心,开箱即用

## 配置
application.properties配置
spring.application.name=${serviceName}
zookeeper.servers=${host1}:${port1},${host2}:${port2}
