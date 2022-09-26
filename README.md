## 联系我
- E-Mail：dtsola@163.com
- QQ：550182738


## 背景信息

### 遇到的问题

有微服务敏捷开发实践经验的开发测试，经常会遇到如下几种问题：

- 开发人员开发接口，因为依赖其他接口返回，所以无法独立的联调测试，需要一套包含依赖的其他所有应用的环境。
- 开发人员将开发的接口部署到环境后，联调测试不符合预期，通过日志等方式排查，发现是依赖的其他应用代码有变更，导致原有的逻辑发生了变更。
- 多位开发人员并行开发多个应用，在联调测试时，只能允许一个应用联调测试，其他应用需要在该应用联调测试完成后才能逐个进行。
- 应用联调测试报错，Debug时其他开发测试正在联调测试的接口也报错。

### 解决方案

让流量在Feature环境内流转非常重要，是微服务敏捷开发的基础。

微服务带来了敏捷开发的便利，但是微服务架构本身也给开发环境带来了一定的复杂性，多个应用间形成完整的流量闭环逻辑，才能避免应用间相互影响。[Discovery](https://github.com/Nepxion/Discovery/)将应用直接接入到微服务治理，绑定环境并配置标签路由后，即可实现应用的精准流量控制，既能享受到微服务架构带来的敏捷开发的便利，又不会给日常开发环境的搭建带来很大的成本。

- 方案一：

  每个迭代或Feature都享有一套独立的完整环境。

  这套独立的环境包含了整个微服务应用集所有的应用，包含注册中心和接入层。

  从该方案的计算方法我们可以发现，当应用增加和环境增加时，成本成倍的增加。

  - 优点：实现简单。
  - 缺点：成本比较大。

- 方案二：

  基于[Discovery](https://github.com/Nepxion/Discovery/)标签路由功能使用开发环境隔离。

  表面上看起来有很多套环境，每个环境都有一套完整的微服务应用。但是这些环境内的有些应用节点不只属于某一个环境，是被多个环境共享，大大降低了成本。只需要维护一套完整的基线环境，基线环境包含了所有微服务应用，也包含了服务注册中心、域名、SLB、网关等其他设施。在增加Feature环境时，只需要单独部署这个Feature所涉及到改动的应用即可，而不需要在每个Feature环境都部署整套的微服务应用及其配套设施。

  维护N套Feature环境的成本计算方法为：N+M。与方案一乘法计算方法相比，相当于零成本增加Feature环境。这样我们就可以放心地扩容出多套Feature环境，每位研发测试都可以轻松拥有属于自己的独立环境。

  具体实现方案如下图所示。

  ![](https://pan.bilnn.cn/api/v3/file/sourcejump/bGE7zQsY/AUdB07hIeWBTyZRU5CbDd88rIN6-0DSO2vLQVBwWjBc*)

## 说明
- 本文以开发环境为本地网络与 [docker-desktop](https://www.docker.com/products/docker-desktop/) +k8s 为例
## 前提条件
- 已部署[Nacos](https://nacos.io/zh-cn/docs/what-is-nacos.html)或能访问到Nacos
	- 本文Nacos地址：192.168.31.64:8848
- 本地可以使用docker（v20.10.17）和k8s（v1.25.0） kubectl的命令 -- 版本使用尽量用最新版
- 安装工具[ktConnect](https://alibaba.github.io/kt-connect/#/zh-cn/guide/downloads)
- jdk 1.8+（ jdk8u345-b01）
- IntelliJ IDEA 2021.1.2 x64
	- 需要安装插件docker以及Kubernetes（可选）
![](https://pan.bilnn.com/api/v3/file/sourcejump/KPZJpWIQ/LZrdLRXxa-3auMeP8r4zugtLBVxIZAoynuKGy-PxDWw*)
- [下载java代码](https://github.com/dtsola/springboot-discovery-test)
## 步骤1：部署基线环境
### 打包镜像
- 首先整个项目package生成jar包
- 使用IDEA对项目中的Dockerfile文件进行打包 为1.0.0版本，比如：springboot-discovery-test-service-a:1.0.0
![](https://pan.bilnn.cn/api/v3/file/sourcejump/QBpJ48Ub/lSLPvG74pyYWdGe1xwPUKMm735jyoHmfi_g_tN6JXRQ*)
### 推送镜像到镜像中心
- 本文不推送镜像中心，直接放在本地
![](https://pan.bilnn.cn/api/v3/file/sourcejump/YqE5gxIv/Ku8EMSj8ZuM7fRN2eooiCGlQoKZW49i9EMJQGbkU4po*)

### 创建dev命名空间
- kubectl create namespace dev
### k8s方式部署应用
- 使用deploy-k8s.yaml进行部署

- kubectl apply -f deploy-k8s.yaml

- 发现已正常部署

  ![](https://pan.bilnn.cn/api/v3/file/sourcejump/XqEbgzFd/DMHhdITIxwIYa6CrJlmuojHaMKPGIEaDm2jfFtdcE-4*)

  ![](https://pan.bilnn.com/api/v3/file/sourcejump/NKWJn4SW/6gfWu6wFiuX6gSxWtDyzHnveigHWQ0Rn2KIN8o4jzus*)

## 步骤2：连接到k8s的网络

### k8s中的网络IP

- 发现肯定是ping不通的

![](https://pan.bilnn.com/api/v3/file/sourcejump/BoMJZbC2/63BuNGvlhKS4hpxBTVZ2NINXO8IppwL2aIXabnKw1n0*)

### 打通网络

- 使用[ktConnect](https://alibaba.github.io/kt-connect/#/zh-cn/guide/downloads)

- ktctl connect -n dev #本地连接到k8s的dev命名空间

### 再次ping地址
- 发现已经通了
![](https://pan.bilnn.cn/api/v3/file/sourcejump/3965Q7tD/p_iuo1O-dXF2hdbChUT-LGLtNIHwRc6o28qQzLbVv9M*)

## 步骤3：IDEA开发特性功能
### 规划

| 服务名                              | 版本        | 区域  | 环境   | 可用区 |
| ----------------------------------- | ----------- | ----- | ------ | ------ |
| springboot-discovery-test-gateway   | 无          | dev   | 全局   | 全局   |
| springboot-discovery-test-service-a | 1.0         | dev   | common | zone1  |
| springboot-discovery-test-service-b | 1.0         | dev   | common | zone1  |
| springboot-discovery-test-service-c | 1.0         | dev   | common | zone1  |
|                                     |             |       |        |        |
| springboot-discovery-test-service-a | feature-1.1 | local | local  | local  |
| springboot-discovery-test-service-b | feature-1.1 | local | local  | local  |
|                                     |             |       |        |        |

全链路调用路径：gateway ->springboot-discovery-test-service-a->springboot-discovery-test-service-b->springboot-discovery-test-service-c

默认环境为基准环境 common，区域为dev

### 配置中心流量调度规则

#### 服务A/B/C分别添加配置

- dataId：

  - springboot-discovery-test-service-a
  - springboot-discovery-test-service-b
  - springboot-discovery-test-service-c

- groupId：springboot-discovery-test-group

- 类型 xml

- 说明：用于本地开发环境调试

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <rule>
      <strategy-failover>
          <!-- 区域调试转移，跨区调试路由到指定区域的实例 -->
          <region-transfer>dev</region-transfer> 
      </strategy-failover>
  </rule>
  ```

#### 网关添加配置

- dataId：springboot-discovery-test-gateway
- groupId：springboot-discovery-test-group
- 类型 xml
- 说明：防止前端或外部调用调用到 开发环境

```xml
<?xml version="1.0" encoding="UTF-8"?>
<rule>
    <strategy>
        <!-- 网关 调用默认 区域 全都走 dev -->
        <region>dev</region>
    </strategy>
</rule>
```

### A/B/C本地开发配置

#### 分别添加内容

  application-dev.properties

  ```pr
spring.cloud.discovery.metadata.version=feature-1.1
spring.cloud.discovery.metadata.region=local
spring.cloud.discovery.metadata.env=local
spring.cloud.discovery.metadata.zone=local
# 本地IP地址
spring.cloud.nacos.discovery.ip=192.168.31.64
# 启动和关闭区域调试转移
spring.application.strategy.region.transfer.enabled=true
  ```

### 开发内容

- 略

## 步骤4：联调测试

### 默认情况下基准环境网关调用

- GET请求调用地址：http://localhost:31000/springboot-discovery-test-service-a/invoke/gateway
- 返回值：gateway -> [ID=springboot-discovery-test-service-a][T=service][P=Nacos][H=10.1.1.101:13001][V=1.0][R=dev][E=common][Z=zone1][G=springboot-discovery-test-group] -> [ID=springboot-discovery-test-service-b][T=service][P=Nacos][H=10.1.1.99:14001][V=1.0][R=dev][E=common][Z=zone1][G=springboot-discovery-test-group] -> [ID=springboot-discovery-test-service-c][T=service][P=Nacos][H=10.1.1.102:16001][V=1.0][R=dev][E=common][Z=zone1][G=springboot-discovery-test-group]
- 全部走 dev 区域以及基准版本

### 场景1：本地1个服务，并调用基准环境

- 本地 启动A服务

- 调用路径：网关->A服务（本地环境）->B服务（基准环境）->C服务（基准环境）

#### 调试-从网关开始调用

- GET请求调用地址：http://localhost:31000/springboot-discovery-test-service-a/invoke/gateway

- 添加请求头：n-d-region：{"springboot-discovery-test-service-a":"local"}

- 返回值：gateway -> [ID=springboot-discovery-test-service-a][T=service][P=Nacos][H=192.168.31.64:13001][V=feature-1.1][R=local][E=local][Z=local][G=springboot-discovery-test-group] -> [ID=springboot-discovery-test-service-b][T=service][P=Nacos][H=10.1.1.99:14001][V=1.0][R=dev][E=common][Z=zone1][G=springboot-discovery-test-group] -> [ID=springboot-discovery-test-service-c][T=service][P=Nacos][H=10.1.1.102:16001][V=1.0][R=dev][E=common][Z=zone1][G=springboot-discovery-test-group]

- 发现：A服务调用的是local，B和C都是基准环境

  

#### 调试-从本地A服务开始调用

- GET请求调用地址：http://localhost:13001/invoke/gateway

- 返回值：gateway -> [ID=springboot-discovery-test-service-a][T=service][P=Nacos][H=192.168.31.64:13001][V=feature-1.1][R=local][E=local][Z=local][G=springboot-discovery-test-group] -> [ID=springboot-discovery-test-service-b][T=service][P=Nacos][H=10.1.1.99:14001][V=1.0][R=dev][E=common][Z=zone1][G=springboot-discovery-test-group] -> [ID=springboot-discovery-test-service-c][T=service][P=Nacos][H=10.1.1.102:16001][V=1.0][R=dev][E=common][Z=zone1][G=springboot-discovery-test-group]

- 发现：A服务调用的是local，B和C都是基准环境

  

### 场景2：本地2个服务，并调用基准环境

- 本地启动A和B服务

- 调用路径：网关->A服务（本地环境）->B服务（本地环境）->C服务（基准环境）

#### 调试-从网关开始调用

- GET请求调用地址：http://localhost:31000/springboot-discovery-test-service-a/invoke/gateway

- 添加请求头：n-d-region：{"springboot-discovery-test-service-a":"local", "springboot-discovery-test-service-b":"local"}

- 返回值：gateway -> [ID=springboot-discovery-test-service-a][T=service][P=Nacos][H=192.168.31.64:13001][V=feature-1.1][R=local][E=local][Z=local][G=springboot-discovery-test-group] -> [ID=springboot-discovery-test-service-b][T=service][P=Nacos][H=192.168.31.64:14001][V=feature-1.1][R=local][E=local][Z=local][G=springboot-discovery-test-group] -> [ID=springboot-discovery-test-service-c][T=service][P=Nacos][H=10.1.1.69:16001][V=1.0][R=dev][E=common][Z=zone1][G=springboot-discovery-test-group]

- 发现：A和B服务调用的是local，C是基准环境

  

#### 调试-从本地A服务开始调用

- GET请求调用地址：http://localhost:13001/invoke/gateway
- 添加请求头：n-d-region：{“springboot-discovery-test-service-b":"local"}
- 返回值：gateway -> [ID=springboot-discovery-test-service-a][T=service][P=Nacos][H=192.168.31.64:13001][V=feature-1.1][R=local][E=local][Z=local][G=springboot-discovery-test-group] -> [ID=springboot-discovery-test-service-b][T=service][P=Nacos][H=192.168.31.64:14001][V=feature-1.1][R=local][E=local][Z=local][G=springboot-discovery-test-group] -> [ID=springboot-discovery-test-service-c][T=service][P=Nacos][H=10.1.1.69:16001][V=1.0][R=dev][E=common][Z=zone1][G=springboot-discovery-test-group]
- 发现：A和B服务调用的是local，C是基准环境

### 场景3：本地3个服务，全部调用本地环境

- 本地启动A、B、C服务

- 调用路径：网关->A服务（本地环境）->B服务（本地环境）->C服务（本地环境）

#### 调试-从网关开始调用

- GET请求调用地址：http://localhost:31000/springboot-discovery-test-service-a/invoke/gateway
- 添加请求头：n-d-region：{"springboot-discovery-test-service-a":"local", "springboot-discovery-test-service-b":"local",, "springboot-discovery-test-service-c":"local"}
- 返回值：gateway -> [ID=springboot-discovery-test-service-a][T=service][P=Nacos][H=192.168.31.64:13001][V=feature-1.1][R=local][E=local][Z=local][G=springboot-discovery-test-group] -> [ID=springboot-discovery-test-service-b][T=service][P=Nacos][H=192.168.31.64:14001][V=feature-1.1][R=local][E=local][Z=local][G=springboot-discovery-test-group] -> [ID=springboot-discovery-test-service-c][T=service][P=Nacos][H=10.1.1.69:16001][V=1.0][R=dev][E=common][Z=zone1][G=springboot-discovery-test-group]
- 发现：A、B、C服务调用的都是local

#### 调试-从本地A服务开始调用

- GET请求调用地址：http://localhost:13001/invoke/gateway
- 添加请求头：n-d-region：{“springboot-discovery-test-service-b":"local", "springboot-discovery-test-service-c":"local"}
- 返回值：gateway -> [ID=springboot-discovery-test-service-a][T=service][P=Nacos][H=192.168.31.64:13001][V=feature-1.1][R=local][E=local][Z=local][G=springboot-discovery-test-group] -> [ID=springboot-discovery-test-service-b][T=service][P=Nacos][H=192.168.31.64:14001][V=feature-1.1][R=local][E=local][Z=local][G=springboot-discovery-test-group] -> [ID=springboot-discovery-test-service-c][T=service][P=Nacos][H=10.1.1.69:16001][V=1.0][R=dev][E=common][Z=zone1][G=springboot-discovery-test-group]
- 发现：A、B、C服务调用的都是local

## 步骤5：部署feature-1.1版本到dev区域

- 调用路径：网关->A服务（feature-1.1版本）->B服务（基准环境，1.0版本）->C服务（feature-1.1版本）

### 打包feature-1.1版本镜像并推送镜像中心

- A和C服务的镜像版本为feature-1.1版本，比如：springboot-discovery-test-service-a:feature-1.1
- 本文不推送镜像中心，直接放在本地

### 配置中心流量调度规则

#### 网关添加配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<rule>
    <strategy>
        <!-- 网关 调用默认 区域 全都走 dev -->
        <region>dev</region>
        <!-- 网关 调用默认 版本 全都走 dev -->
        <version>1.0</version>
    </strategy>
</rule>
```

### 部署版本

- 使用deploy-k8s-feature-1.1.yaml进行部署
- kubectl apply -f deploy-k8s-feature-1.1.yaml

### 调试验证

#### 调试-从网关开始调用

- GET请求调用地址：http://localhost:31000/springboot-discovery-test-service-a/invoke/gateway
- 添加请求头：n-d-version：{"springboot-discovery-test-service-a":"feature-1.1", "springboot-discovery-test-service-c":"feature-1.1"}
- 返回值：gateway -> [ID=springboot-discovery-test-service-a][T=service][P=Nacos][H=10.1.1.110:13001][V=feature-1.1][R=dev][E=common][Z=zone1][G=springboot-discovery-test-group] -> [ID=springboot-discovery-test-service-b][T=service][P=Nacos][H=10.1.1.112:14001][V=1.0][R=dev][E=common][Z=zone1][G=springboot-discovery-test-group] -> [ID=springboot-discovery-test-service-c][T=service][P=Nacos][H=10.1.1.109:16001][V=feature-1.1][R=dev][E=common][Z=zone1][G=springboot-discovery-test-group]
- 发现：A和C服务调用的是feature-1.1，C是基准环境1.0版本，验证成功

