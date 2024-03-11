package com.example.dz2.networking

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("price")
    val price: Float,
    @SerializedName("discountPercentage")
    val discountPercentage: Float,
    val priceWithDiscount: Float = price * (1 - discountPercentage / 100),
    @SerializedName("rating")
    val rating: Float,
    @SerializedName("stock")
    val stock: Int,
    @SerializedName("brand")
    val brand: String,
    @SerializedName("category")
    val category: String,
    @SerializedName("thumbnail")
    val thumbnail: String,
    @SerializedName("images")
    val images: List<String>,
)