package com.itheima.mall.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "aliyun.oss")
public class AliyunOssProperties {

  public String  endpoint;
  public String  accessKeyId;
  public String  accessKeySecret;
  public String  bucketName ;
  private String imgUrl;

}
