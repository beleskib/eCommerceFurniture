package com.example.ecommercefurniture.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommercefurniture.data.CartProduct
import com.example.ecommercefurniture.firebase.FirebaseCommon
import com.example.ecommercefurniture.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
):ViewModel() {
    private val _cartProducts = MutableStateFlow<Resource<List<CartProduct>>>(Resource.Unspecified())

    val cartProducts = _cartProducts.asStateFlow()
    val productsPrice = cartProducts.map {
        when(it) {
            is Resource.Success -> {
                calculatePrice(it.data!!)
            }
            else -> null
        }
    }
    private val _deleteDialog = MutableSharedFlow<CartProduct>()
    val deleteDialog = _deleteDialog.asSharedFlow()
    private var cartProductDocuments = emptyList<DocumentSnapshot>()

    fun deleteCartProduct(cartProduct: CartProduct) {
        val index = cartProducts.value.data?.indexOf(cartProduct)
        if (index != null && index != -1 ) {
            val documentId = cartProductDocuments[index].id
            firestore.collection("user").document(firebaseAuth.uid!!)
                .collection("cart").document(documentId).delete()
        }
    }


    private fun calculatePrice(data: List<CartProduct>): Float? {
        return data.sumByDouble { cartProduct ->
            val product = cartProduct.product
            val productPrice = product?.price ?: 0f
            val offerPercentage = product?.offerPercentage ?: 0f

            if (productPrice == null) return@sumByDouble 0.0

            val priceAfterOffer = if (offerPercentage > 0) {
                productPrice * (1 - offerPercentage / 100)
            } else {
                productPrice
            }

            (priceAfterOffer * cartProduct.quantity).toDouble()
        }.toFloat()
    }

    init {
        getCartProducts()
    }

    private fun getCartProducts() {
        if (viewModelScope.isActive) {
            viewModelScope.launch {
                _cartProducts.emit(Resource.Loading())
                firestore.collection("user").document(firebaseAuth.uid!!).collection("cart")
                    .addSnapshotListener { value, error ->
                        if (error != null || value == null) {
                            viewModelScope.launch {
                                _cartProducts.emit(Resource.Error(error?.message.toString()))
                            }
                        } else {
                            cartProductDocuments = value.documents
                            val cartProduct = value.toObjects(CartProduct::class.java)
                            viewModelScope.launch {
                                _cartProducts.emit(Resource.Success(cartProduct))
                            }
                        }
                    }
            }
        }
    }

    fun changeQuantity(
        cartProduct: CartProduct,
        quantityChanging: FirebaseCommon.QuantityChanging
    ) {

        val index = cartProducts.value.data?.indexOf(cartProduct)


        if(index != null && index != -1) {
            val documentId = cartProductDocuments[index].id
            when(quantityChanging) {
                FirebaseCommon.QuantityChanging.INCREASE -> {
                    viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
                    increaseQuantity(documentId)
                }
                FirebaseCommon.QuantityChanging.DECREASE -> {
                    if (cartProduct.quantity == 1) {
                        viewModelScope.launch { _deleteDialog.emit(cartProduct) }
                        return
                    }
                    viewModelScope.launch { _cartProducts.emit(Resource.Loading()) }
                    decreaseQuantity(documentId)
                }
            }
        }
    }

    private fun decreaseQuantity(documentId: String) {
        firebaseCommon.decreaseQuantiy(documentId) {
            result, exception ->
            if (exception != null)
                viewModelScope.launch {
                    _cartProducts.emit(Resource.Error(exception.message.toString()))
                }
        }
    }

    private fun increaseQuantity(documentId: String) {
        firebaseCommon.increaseQuantity(documentId) {
                result, exception ->
            if (exception != null)
                viewModelScope.launch {
                    _cartProducts.emit(Resource.Error(exception.message.toString()))
                }
        }
    }
}