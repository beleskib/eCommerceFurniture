package com.example.ecommercefurniture.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommercefurniture.data.CartProduct
import com.example.ecommercefurniture.firebase.FirebaseCommon
import com.example.ecommercefurniture.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
) :ViewModel(){

    private val _addtoCart = MutableStateFlow<Resource<CartProduct>>(Resource.Unspecified())
    val addToCart = _addtoCart.asStateFlow()

    fun addUpdateProductInCart(cartProduct: CartProduct) {
        viewModelScope.launch { _addtoCart.emit(Resource.Loading()) }
        firestore.collection("user").document(auth.uid!!).collection("cart")
            .whereNotEqualTo("product.id", cartProduct.product.id).get()
            .addOnSuccessListener {
                it.documents.let {
                    if(it.isEmpty()) { // Add new product
                        addNewProduct(cartProduct)
                    } else {
                        val product = it.first().toObject(CartProduct::class.java)
                        if(product == cartProduct) { // Increase quantity
                            val documentId = it.first().id
                            increaseQuantity(documentId, cartProduct)
                        } else { // Add new product
                            addNewProduct(cartProduct)
                        }
                    }
                }
            }.addOnFailureListener {
                viewModelScope.launch { _addtoCart.emit(Resource.Error(it.message.toString())) }
            }
    }
    private fun addNewProduct(cartProduct: CartProduct) {
        firebaseCommon.addProductToCart(cartProduct) {
            addedProduct, e ->
            viewModelScope.launch {
                if (e == null)
                    _addtoCart.emit(Resource.Success(addedProduct!!))
                else
                    _addtoCart.emit(Resource.Error(e.message.toString()))
            }
        }
    }
    private fun increaseQuantity(documentId: String, cartProduct: CartProduct) {
        firebaseCommon.increaseQuantity(documentId) {
            _, exception ->
            viewModelScope.launch {
                if (exception == null)
                    _addtoCart.emit(Resource.Success(cartProduct))
                else
                    _addtoCart.emit(Resource.Error(exception.message.toString()))
            }
        }
    }
}