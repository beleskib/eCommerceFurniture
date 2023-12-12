package com.example.ecommercefurniture.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Address(
    val addressTitle: String,
    val fullName: String,
    val street: String,
    val phone: String,
    val city: String,
    val country: String
): Parcelable {
    constructor(): this("", "", "", "","","")
}