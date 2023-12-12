package com.example.ecommercefurniture.data

import android.os.Parcelable
import com.example.ecommercefurniture.fragments.categories.AccesoryFragment
import com.example.ecommercefurniture.fragments.categories.ChairFragment
import com.example.ecommercefurniture.fragments.categories.CupboardFragment
import com.example.ecommercefurniture.fragments.categories.FurnitureFragment
import com.example.ecommercefurniture.fragments.categories.MainCategoryFragment
import com.example.ecommercefurniture.fragments.categories.TableFragment
import kotlinx.parcelize.Parcelize


/*
@Parcelize
sealed class Category(
    val name:String,
    val products:Int?,
    val rank:Int,
    val image:String

    ) : Parcelable {

    object Chair: Category("Chair", 1,1, "")
    object Cupboard: Category("Cupboard",1 ,2, "")
    object Table: Category("Table",3, 3, "")
    object Accessory: Category("Accessory",1, 4, "")
    object Furniture: Category("Furniture",1, 5, "")

    }*/
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
