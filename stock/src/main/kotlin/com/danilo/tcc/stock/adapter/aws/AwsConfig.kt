package com.danilo.tcc.stock.adapter.aws

import com.danilo.tcc.stock.adapter.aws.s3.S3Properties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Configuration
import java.net.URI

@Configuration
class AwsConfig(
    private val props: S3Properties,
) {
    @Bean
    fun s3Client(): S3Client =
        S3Client
            .builder()
            .region(Region.of(props.region))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        props.accessKeyId,
                        props.secretAccessKey,
                    ),
                ),
            ).endpointOverride(URI.create(props.endpoint))
            .build()

    @Bean fun s3AsyncClient() =
        if (props.endpointOverride.enabled) {
            S3AsyncClient
                .builder()
                .endpointOverride(URI.create(props.endpointOverride.url))
                .region(Region.of(props.endpointOverride.region))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .credentialsProvider(
                    StaticCredentialsProvider.create(
                        AwsSessionCredentials.create(
                            "accessKey",
                            "secretKey",
                            "sessionToken",
                        ),
                    ),
                ).build()
        } else {
        }
}
