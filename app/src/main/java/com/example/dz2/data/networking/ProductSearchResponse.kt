package com.example.dz2.data.networking

import com.example.dz2.data.model.Product
import com.google.gson.annotations.SerializedName

data class ProductSearchResponse(
    @SerializedName("products") val data: List<Product>,
    @SerializedName("total")
    val total: Int,
    @SerializedName("skip")//с какого грузим
    val skip: Int,
    @SerializedName("limit")//сколько грузим
    val limit: Int,
    val nextPage: Int?
)