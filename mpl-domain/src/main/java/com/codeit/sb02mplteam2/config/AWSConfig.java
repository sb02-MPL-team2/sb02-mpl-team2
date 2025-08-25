package com.codeit.sb02mplteam2.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

@Configuration
public class AWSConfig {

  @Value("${mpl.aws.accessKeyId}")
  private String ACCESS_KEY;

  @Value("${mpl.aws.secretKey}")
  private String SECRET_KEY;

  @Bean
  public StaticCredentialsProvider awsCredentialsProvider() {
    AwsBasicCredentials credentials = AwsBasicCredentials.create(ACCESS_KEY, SECRET_KEY);
    return StaticCredentialsProvider.create(credentials);
  }
}
