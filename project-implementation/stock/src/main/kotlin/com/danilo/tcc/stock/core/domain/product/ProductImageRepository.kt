package com.danilo.tcc.stock.core.domain.product

interface ProductImageRepository {
    suspend fun uploadImage(
        productId: ProductId,
        file: ByteArray,
        fileName: String,
        contentType: String?,
    ): String
}
