# 云兔智能系统——独立部署Demo
这是一个基于SpringBoot + MybatisPlus构架的云兔独立部署系统

## 功能特性
- 云兔应用独立部署公共框架
- RESTful API接口
- 简单的AI响应生成
- Spring Boot自动配置
- 健康检查和监控

## 技术栈
- Java 21
- Spring Boot 3.2.2
- Mybatis Plus 3.5.12
- Spring MVC
- Spring Actuator
- Maven (项目管理)

## 快速开始
### 数据库准备
到云兔工作台（）下载数据库安装包，包括表结构初始化和数据导入两部分，并部署到自己的数据库，推荐阿里云RDS-Mysql数据库
到应用的配置文件application.yml配置好数据库连接信息

### 应用配置
到应用的配置文件application.yml配置好应用相关的配置，包括：
```yaml
lovrabet:
  appCode: app-c2dd52a2     #替换为你自己的云兔应用appCode
  apiDomain: http://127.0.0.1:8080  #替换为你自己的API请求地址
  rendererVersion: 1.0.98   #前端渲染资源版本号，建议使用最新的版本
  appPlatformVersion: 1.5.1 #前端平台资源版本号，建议使用最新的版本
```

### 编译项目
```bash
mvn clean compile
```

### 运行服务器
```bash
mvn spring-boot:run
```

或者
```bash
mvn clean package
java -jar target/lovrabet-demo-1.0.0.jar
```
### 健康检查
```
GET /heartbeat
```

### 开发环境访问地址
http://127.0.0.1:8080/

### https访问地址
请先调整配置文件中的以下配置项：
```yaml
ssl: #默认关闭，按需开启
  key-store: classpath:keystore.p12
  key-store-password: yuntoo
  key-store-type: PKCS12
  key-alias: myapp

apiDomain: https://127.0.0.1:8080
```
然后再访问以下地址：
https://127.0.0.1:8080/

## 配置文件
主要配置在 `src/main/resources`目录下：
主配置文件：application.yml
默认开发环境配置文件：application-dev.yml
生产环境配置文件：application-prod.yml

## 联系我们
【邮箱】：service@lovrabet.com
或者通过微信群、飞书群和钉钉群进行反馈和交流