package com.example.ecommercefurniture.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val firstName: String,
    val lastName: String,
    val email: String,
    val imagePath: String = ""
) : Parcelable {
    // Add a no-argument constructor
    constructor() : this("", "", "")
}
