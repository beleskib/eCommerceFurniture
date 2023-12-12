package com.example.ecommercefurniture.firebase

import com.example.ecommercefurniture.util.Constants.CATEGORIES_COLLECTION
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirebaseDb {
    private val categoriesCollection = Firebase.firestore.collection(CATEGORIES_COLLECTION)


    fun getCategories() = categoriesCollection.orderBy("rank").get()
}