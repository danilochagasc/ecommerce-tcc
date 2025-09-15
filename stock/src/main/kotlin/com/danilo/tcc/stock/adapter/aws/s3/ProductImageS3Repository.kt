package com.danilo.tcc.stock.adapter.aws.s3

import com.danilo.tcc.stock.core.domain.product.ProductId
import com.danilo.tcc.stock.core.domain.product.ProductImageRepository
import org.springframework.stereotype.Repository
import software.amazon.awssdk.core.sync.RequestBody.fromBytes
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetUrlRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest

@Repository
class ProductImageS3Repository(
    private val s3Client: S3Client,
    private val props: S3Properties,
) : ProductImageRepository {
    private val acceptedFormats = listOf(".png", ".jpg", ".jpeg")

    override fun uploadImage(
        productId: ProductId,
        file: ByteArray,
        fileName: String,
        contentType: String?,
    ): String {
        val fileExtension = fileName.substringAfterLast('.', "").let { if (it.isNotEmpty()) ".$it" else "" }

        if (fileExtension.lowercase() !in acceptedFormats) {
            throw IllegalArgumentException("Unsupported file format. Accepted formats are: ${acceptedFormats.joinToString(", ")}")
        }

        val key = "products/$productId/image$fileExtension"

        println(props.bucket)

        val putRequest =
            PutObjectRequest
                .builder()
                .bucket(props.bucket)
                .key(key)
                .contentType(contentType ?: "application/octet-stream")
                .build()

        s3Client.putObject(putRequest, fromBytes(file))

        val urlRequest =
            GetUrlRequest
                .builder()
                .bucket(props.bucket)
                .key(key)
                .build()

        return s3Client.utilities().getUrl(urlRequest).toString()
    }
}
