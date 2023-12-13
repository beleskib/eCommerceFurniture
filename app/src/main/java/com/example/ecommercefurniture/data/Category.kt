package com.example.ecommercefurniture.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Category(
    val names: String?,
    val products: Int? = null,
    val rank: String,
    val image: List<String> = emptyList()
) : Parcelable {
    // Default constructor for Firestore deserialization
    constructor() : this("", null, "", emptyList())

    companion object {
        val Chair = Category("Chair", 1, "", emptyList())
        val Cupboard = Category("Cupboard", 1, "CupboardFragment", emptyList())
        val Table = Category("Table", 3, "", emptyList())
        val Accessory = Category("Accessory", 1, "", emptyList())
        val Furniture = Category("Furniture", 1, "", emptyList())
    }
}
