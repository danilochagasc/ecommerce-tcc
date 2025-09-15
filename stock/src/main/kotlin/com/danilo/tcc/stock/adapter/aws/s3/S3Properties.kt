package com.danilo.tcc.stock.adapter.aws.s3

import org.springframework.boot.context.properties.ConfigurationProperties
import kotlin.time.Duration

@ConfigurationProperties(prefix = "aws.s3")
data class S3Properties(
    val bucket: String,
    val region: String,
    val endpoint: String,
    val accessKeyId: String,
    val secretAccessKey: String,
    val endpointOverride: S3EndpointOverride,
    val nettyNioProperties: NettyNioProperties,
)

data class S3EndpointOverride(
    val enabled: Boolean,
    val url: String,
    val region: String,
)

data class NettyNioProperties(
    val writeTimeout: Long,
    val maxConcurrency: Int,
    val maxPendingConnectionAcquires: Int,
    val connectionTimeout: Long,
    val connectionAcquisitionTimeout: Duration,
)
