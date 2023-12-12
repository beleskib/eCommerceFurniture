package com.example.ecommercefurniture.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Product(
    val id: String,
    val name: String,
    val category: String,
    val price : Float,
    val offerPercentage: Float? = null,
    val description: String? = null,
    val colors: List<Int>? = null,
    val sizes: List<String>? = null,
    val images: List<String>,
    val searchKeywords: List<String> = emptyList()
): Parcelable {
    constructor(): this("0","","",0f,images = emptyList())

}
