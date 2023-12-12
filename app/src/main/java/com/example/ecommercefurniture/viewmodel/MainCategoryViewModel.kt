package com.example.ecommercefurniture.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommercefurniture.data.Product
import com.example.ecommercefurniture.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainCategoryViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
): ViewModel() {

    private val _specialProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    private val _bestDealsProductsProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    private val _bestProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())



    val specialProducts: StateFlow<Resource<List<Product>>> = _specialProducts
    val bestDealsProducts : StateFlow<Resource<List<Product>>> = _bestDealsProductsProducts
    val bestProducts : StateFlow<Resource<List<Product>>> = _bestProducts

    private val pagingInfo = PagingInfo()
    init {
        fetchSpecialProducts()

        fetchBestDealsProducts()

        fetchBestProducts()
    }

     fun fetchBestProducts() {
         if (!pagingInfo.isPagingEnd) {
             viewModelScope.launch {
                 _bestProducts.emit(Resource.Loading())
             firestore.collection("Products").limit(pagingInfo.bestProductsPage * 10).get()
                 .addOnSuccessListener { result ->
                     val bestProductList = result.toObjects(Product::class.java)
                     pagingInfo.isPagingEnd = bestProductList == pagingInfo.oldBestProducts
                     pagingInfo.oldBestProducts = bestProductList
                     viewModelScope.launch {
                         _bestProducts.emit(Resource.Success(bestProductList))
                     }
                     pagingInfo.bestProductsPage++
                 }.addOnFailureListener {
                     viewModelScope.launch {
                         _bestProducts.emit(Resource.Error(it.message.toString()))
                     }
                 }
             }
         }
    }

     fun fetchBestDealsProducts() {
        viewModelScope.launch {
            _bestDealsProductsProducts.emit(Resource.Loading())
            firestore.collection("Products")
                .whereEqualTo("category", "Best Deals").limit(pagingInfo.bestDealsProductsPage * 10).get().addOnSuccessListener { result ->
                    val bestDealsProductList = result.toObjects(Product::class.java)
                    pagingInfo.isPagingEnd = bestDealsProductList == pagingInfo.oldBestDealsProducts
                    pagingInfo.oldBestDealsProducts = bestDealsProductList
                    viewModelScope.launch {
                        _bestDealsProductsProducts.emit(Resource.Success(bestDealsProductList))
                    }
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _bestDealsProductsProducts.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }
    fun fetchSpecialProducts() {
        viewModelScope.launch {
            _specialProducts.emit(Resource.Loading())
            firestore.collection("Products")
                .whereEqualTo("category", "Special Products").limit(pagingInfo.SpecialProductsPage * 10).get().addOnSuccessListener { result ->
                    val specialProductList = result.toObjects(Product::class.java)
                    pagingInfo.isPagingEnd = specialProductList == pagingInfo.oldSpecialProducts
                    pagingInfo.oldSpecialProducts = specialProductList
                    viewModelScope.launch {
                        _specialProducts.emit(Resource.Success(specialProductList))
                    }
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _specialProducts.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }
}
internal data class PagingInfo(
    var bestProductsPage: Long = 1,
    var bestDealsProductsPage: Long = 1,
    var SpecialProductsPage: Long = 1,
    var oldBestProducts: List<Product> = emptyList(),
    var oldBestDealsProducts : List<Product> = emptyList(),
    var oldSpecialProducts : List<Product> = emptyList(),
    var isPagingEnd: Boolean = false
)