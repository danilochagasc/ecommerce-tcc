package com.danilo.tcc.stock.adapter.aws.s3

import com.danilo.tcc.stock.core.domain.product.ProductId
import com.danilo.tcc.stock.core.domain.product.ProductImageRepository
import kotlinx.coroutines.future.await
import org.springframework.stereotype.Repository
import software.amazon.awssdk.core.async.AsyncRequestBody
import software.amazon.awssdk.core.sync.RequestBody.fromBytes
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.model.GetUrlRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest

@Repository
class ProductImageS3Repository(
    private val s3AsyncClient: S3AsyncClient,
    private val props: S3Properties,
) : ProductImageRepository {
    companion object {
        val acceptedFormats = listOf(".png", ".jpg", ".jpeg")
    }

    override suspend fun uploadImage(
        productId: ProductId,
        file: ByteArray,
        fileName: String,
        contentType: String?,
    ): String {
        val fileExtension =
            fileName
                .substringAfterLast('.', "")
                .let { if (it.isNotEmpty()) ".$it" else "" }

        if (fileExtension.lowercase() !in acceptedFormats) {
            throw IllegalArgumentException("Unsupported file format. Accepted formats are: ${acceptedFormats.joinToString(", ")}")
        }

        val key = "products/$productId/image$fileExtension"

        val putRequest =
            PutObjectRequest
                .builder()
                .bucket(props.bucket)
                .key(key)
                .contentType(contentType ?: "application/octet-stream")
                .build()

        s3AsyncClient.putObject(putRequest, AsyncRequestBody.fromBytes(file)).await()

        return if (props.endpointOverride.enabled) {
            "${props.endpointOverride.url}/${props.bucket}/$key"
        } else {
            s3AsyncClient
                .utilities()
                .getUrl { b: GetUrlRequest.Builder ->
                    b.bucket(props.bucket)
                    b.key(key)
                }.toString()
        }
    }
}
