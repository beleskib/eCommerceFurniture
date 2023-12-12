package com.example.ecommercefurniture.firebase

import com.example.ecommercefurniture.data.CartProduct
import com.example.ecommercefurniture.data.User
import com.example.ecommercefurniture.util.Constants.USER_COLLECTION
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.Exception

class FirebaseCommon(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val usersCollectionRef: CollectionReference = Firebase.firestore.collection(USER_COLLECTION)

) {
    private val cartCollection =
        auth.uid?.let { firestore.collection("user").document(it).collection("cart") }

    fun addProductToCart(cartProduct: CartProduct, onResult: (CartProduct?, Exception?) -> Unit) {
        cartCollection?.document()?.set(cartProduct)?.addOnSuccessListener {
            onResult(cartProduct, null)
        }?.addOnFailureListener {
            onResult(null, it)
        }
    }

    fun increaseQuantity(documentId: String, onResult: (String?, Exception?) -> Unit) {
        firestore.runTransaction { transition ->
            val documentRef = cartCollection!!.document(documentId)
            val document = transition.get(documentRef)
            val productObject = document.toObject(CartProduct::class.java)
            productObject?.let { cartProduct ->
                val newQuantity = cartProduct.quantity + 1
                val newProductOBject = cartProduct.copy(quantity = newQuantity)
                transition.set(documentRef, newProductOBject)
            }
        }.addOnSuccessListener {
            onResult(documentId, null)
        }.addOnFailureListener {
            onResult(null, it)
        }
    }

    fun saveUserInformation(
        userUid: String,
        user: User
    ) = usersCollectionRef.document(userUid).set(user)
    fun checkUserByEmail(email: String, onResult: (String?, Boolean?) -> Unit) {
        usersCollectionRef.whereEqualTo("email", email).get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val user = it.result.toObjects(User::class.java)
                    if (user.isEmpty())
                        onResult(null, false)
                    else
                        onResult(null, true)
                } else
                    onResult(it.exception.toString(), null)
            }
    }
    fun signInWithGoogle(credential: AuthCredential) =
        FirebaseAuth.getInstance().signInWithCredential(credential)

    fun decreaseQuantiy(documentId: String, onResult: (String?, Exception?) -> Unit) {

        firestore.runTransaction { transition ->
            val documentRef = cartCollection!!.document(documentId)
            val document = transition.get(documentRef)
            val productObject = document.toObject(CartProduct::class.java)
            productObject?.let { cartProduct ->
                val newQuantity = cartProduct.quantity - 1
                val newProductOBject = cartProduct.copy(quantity = newQuantity)
                transition.set(documentRef, newProductOBject)
            }
        }.addOnSuccessListener {
            onResult(documentId, null)
        }.addOnFailureListener {
            onResult(null, it)
        }
    }

    enum class QuantityChanging{
        INCREASE, DECREASE
    }
}
