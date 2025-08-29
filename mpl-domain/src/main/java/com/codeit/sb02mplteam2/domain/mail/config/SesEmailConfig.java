package com.codeit.sb02mplteam2.domain.mail.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;

@Configuration
@RequiredArgsConstructor
@Profile("!prod")
public class SesEmailConfig {
  private final AwsCredentialsProvider awsCredentialsProvider;

  @Bean
  public SesClient sesClient() {
    return SesClient.builder()
        .region(Region.AP_NORTHEAST_2)
        .credentialsProvider(awsCredentialsProvider)
        .build();
  }
}
