package com.itheima.mall;

import com.aliyun.oss.*;
import com.aliyun.oss.common.comm.Protocol;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.CreateBucketRequest;
import com.aliyun.oss.model.DataRedundancyType;
import com.aliyun.oss.model.StorageClass;
import com.itheima.mall.properties.AliyunOssProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@SpringBootTest
class MallApplicationTests {
    @Autowired
    private AliyunOssProperties properties;

    @Test
    void contextLoads() {
        // yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
        OSS ossClient = null;
        try {
            String endpoint = properties.getEndpoint();
// 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
            String accessKeyId = properties.getAccessKeyId();
            String accessKeySecret = properties.getAccessKeySecret();

            // 创建ClientConfiguration。ClientConfiguration是OSSClient的配置类，可配置代理、连接超时、最大连接数等参数。
            ClientBuilderConfiguration conf = new ClientBuilderConfiguration();

        // 设置OSSClient允许打开的最大HTTP连接数，默认为1024个。
                    conf.setMaxConnections(200);
        // 设置Socket层传输数据的超时时间，默认为50000毫秒。
                    conf.setSocketTimeout(10000);
        // 设置建立连接的超时时间，默认为50000毫秒。
                    conf.setConnectionTimeout(10000);
        // 设置从连接池中获取连接的超时时间（单位：毫秒），默认不超时。
                    conf.setConnectionRequestTimeout(1000);
        // 设置连接空闲超时时间。超时则关闭连接，默认为60000毫秒。
                    conf.setIdleConnectionTime(10000);
        // 设置失败请求重试次数，默认为3次。
                    conf.setMaxErrorRetry(5);
        // 设置是否支持将自定义域名作为Endpoint，默认支持。
                    conf.setSupportCname(true);
        // 设置是否开启二级域名的访问方式，默认不开启。
                    conf.setSLDEnabled(true);
        // 设置连接OSS所使用的协议（HTTP或HTTPS），默认为HTTP。
                    conf.setProtocol(Protocol.HTTP);
        // 设置用户代理，指HTTP的User-Agent头，默认为aliyun-sdk-java。
                    conf.setUserAgent("aliyun-sdk-java");
        // 设置代理服务器端口。
                    conf.setProxyHost("<yourProxyHost>");
        // 设置代理服务器验证的用户名。
                    conf.setProxyUsername("<yourProxyUserName>");
        // 设置代理服务器验证的密码。
                    conf.setProxyPassword("<yourProxyPassword>");
        // 设置是否开启HTTP重定向，默认开启。
                    conf.setRedirectEnable(true);
        // 设置是否开启SSL证书校验，默认开启。
                    conf.setVerifySSLEnable(true);


// 创建OSSClient实例。
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

            // 填写Bucket名称，例如examplebucket。
            String bucketName = properties.getBucketName();

            this.createBucket(ossClient, bucketName);

            // 填写Object完整路径，例如exampledir/exampleobject.txt。Object完整路径中不能包含Bucket名称。
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            String objectName=properties.getImgUrl()+"/"+format.format(new Date())+"/"+ UUID.randomUUID().toString()+".png";

            ossClient.putObject(bucketName, objectName, new FileInputStream(new File("C:/Users/liujj/Pictures/Saved Pictures/微信图片_20210829121759.png")));

        }catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message:" + oe.getErrorMessage());
            System.out.println("Error Code:" + oe.getErrorCode());
            System.out.println("Request ID:" + oe.getRequestId());
            System.out.println("Host ID:" + oe.getHostId());
        } catch (ClientException | FileNotFoundException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message:" + ce.getMessage());
        } finally {
            // 关闭OSSClient。
            ossClient.shutdown();
        }




    }
    private void createBucket(OSS ossClient,String bucketName){
        // 判断存储空间examplebucket是否存在。如果返回值为true，则存储空间存在，如果返回值为false，则存储空间不存在。
        boolean exists = ossClient.doesBucketExist(properties.getBucketName());

        if(!exists){
            //创建CreateBucketRequest对象。
            CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);

            // 如果创建存储空间的同时需要指定存储类型和数据容灾类型, 请参考如下代码。
            // 此处以设置存储空间的存储类型为标准存储为例介绍。
            createBucketRequest.setStorageClass(StorageClass.Standard);
            // 数据容灾类型默认为本地冗余存储，即DataRedundancyType.LRS。如果需要设置数据容灾类型为同城冗余存储，请设置为DataRedundancyType.ZRS。
            createBucketRequest.setDataRedundancyType(DataRedundancyType.ZRS);
            // 设置存储空间的权限为公共读，默认为私有。
            createBucketRequest.setCannedACL(CannedAccessControlList.PublicRead);

            // 创建存储空间。
            ossClient.createBucket(createBucketRequest);


        }
    }


    @Test
    public void  test1(){

//        java的基本数据类型的存储范围
//        整数： 1 byte   2 short   4 int    8 long
//        浮点型小数：  单精度 flot 4         双精度 double 8字节

        System.out.println(true^true);
    }

}
