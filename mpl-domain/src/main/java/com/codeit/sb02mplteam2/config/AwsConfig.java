package com.codeit.sb02mplteam2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.ses.SesClient;

@Configuration
public class AwsConfig {

  /**
   * Spring Cloud AWS가 application.yml의 cloud.aws.* 설정을 바탕으로
   * AwsCredentialsProvider Bean을 자동으로 생성하고 여기에 주입해줍니다.
   */
  private final AwsCredentialsProvider awsCredentialsProvider;

  public AwsConfig(AwsCredentialsProvider awsCredentialsProvider) {
    this.awsCredentialsProvider = awsCredentialsProvider;
  }

  /**
   * S3 클라이언트 Bean을 생성합니다.
   * @return S3Client
   */
  @Bean
  public S3Client s3Client() {
    return S3Client.builder()
        .region(Region.AP_NORTHEAST_2)
        .credentialsProvider(awsCredentialsProvider) // 주입받은 자격 증명을 명시적으로 사용
        .build();
  }

  /**
   * SES 클라이언트 Bean을 생성합니다.
   * @return SesClient
   */
  @Bean
  public SesClient sesClient() {
    return SesClient.builder()
        .region(Region.AP_NORTHEAST_2)
        .credentialsProvider(awsCredentialsProvider) // 주입받은 자격 증명을 명시적으로 사용
        .build();
  }
}