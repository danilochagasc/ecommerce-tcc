package com.danilo.tcc.stock.adapter.aws

import com.danilo.tcc.stock.adapter.aws.s3.S3Properties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsSessionCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.S3Configuration
import java.net.URI
import java.time.Duration.ofSeconds

@Configuration
class AwsConfig(
    private val props: S3Properties,
) {
    @Bean fun s3AsyncClient(): S3AsyncClient {
        val nettyNioAsyncHttpClientHandler =
            NettyNioAsyncHttpClient
                .builder()
                .writeTimeout(ofSeconds(props.nettyNioProperties.writeTimeout))
                .maxConcurrency(props.nettyNioProperties.maxConcurrency)
                .maxPendingConnectionAcquires(props.nettyNioProperties.maxPendingConnectionAcquires)
                .connectionTimeout(ofSeconds(props.nettyNioProperties.connectionTimeout))
                .connectionAcquisitionTimeout(props.nettyNioProperties.connectionAcquisitionTimeout)

        val s3AsyncClient =
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
                S3AsyncClient
                    .builder()
                    .region(Region.of(props.region))
                    .httpClientBuilder(nettyNioAsyncHttpClientHandler)
                    .build()
            }

        return s3AsyncClient
    }
}
