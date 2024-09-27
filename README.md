# 百度向量数据库 Mochow JAVA SDK

针对百度智能云向量数据库，我们推出了一套 JAVA SDK（下称Mochow SDK），方便用户通过代码调用百度向量数据库。

## 如何安装
**使用Maven安装**
在Maven的pom.xml文件中添加mochow-sdk-java的依赖：
```xml
<dependency>
    <groupId>com.baidu</groupId>
    <artifactId>mochow-sdk-java</artifactId>
    <version>{version}</version>
</dependency>
```
JAVA SDK可以在Java1.7，Java1.8环境下运行。

## 快速使用

在使用Mochow SDK 之前，用户需要在百度智能云上创建向量数据库，以获得 API Key。API Key 是用户在调用Mochow SDK 时所需要的凭证。具体获取流程参见平台的[向量数据库使用说明文档](https://cloud.baidu.com/)。

获取到 API Key 后，用户还需要传递它们来初始化Mochow SDK。 可以通过如下方式初始化Mochow SDK：

```java
import com.baidu.mochow.auth.Credentials;
import com.baidu.mochow.client.ClientConfiguration;
import com.baidu.mochow.client.MochowClient;

public class Main {
    public static void main(String[] args) {
        String account = "root";
        String apiKey = "your_api_key";
        String endpoint = "your_service_endpoint"; // example: 127.0.0.1:5287
        
        // 创建Mochow服务的Client
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setCredentials(new Credentials(account, apiKey));
        clientConfiguration.setEndpoint(endpoint);
        MochowClient mochowClient =  new MochowClient(clientConfiguration);
        
        // 创建database
        mochowClient.createDatabase("your_database_name");
    }
}
```

## 功能

目前Mochow SDK 支持用户使用如下功能:

+ Databse 操作
+ Table 操作
+ Alias 操作
+ Index 操作
+ Row 操作

## License

Apache-2.0