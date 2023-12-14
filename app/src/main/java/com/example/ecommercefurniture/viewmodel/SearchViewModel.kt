package com.example.ecommercefurniture.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ecommercefurniture.data.Category
import com.example.ecommercefurniture.data.Product
import com.example.ecommercefurniture.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SearchViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,

): ViewModel() {

    private val _searchCategory = MutableStateFlow<Resource<List<Category>>>(Resource.Unspecified())
    private val _searchProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val searchCategory : StateFlow<Resource<List<Category>>> = _searchCategory
    val searchProducts : StateFlow<Resource<List<Product>>> = _searchProducts

    private val pagingSearchInfo = PagingSearchInfo()

    init {
        getCategories()
        getProducts()
    }

     fun getProducts() {
            if (!pagingSearchInfo.isPagingEnd) {
            viewModelScope.launch {
            _searchProducts.emit(Resource.Loading())
            firestore.collection("Products").limit(pagingSearchInfo.bestProductsSearchPage * 10).get()
                .addOnSuccessListener { result ->
                    val searchProductsList = result.toObjects(Product::class.java)
                    pagingSearchInfo.isPagingEnd = searchProductsList == pagingSearchInfo.oldBestProducts
                    pagingSearchInfo.oldBestProducts = searchProductsList
                    viewModelScope.launch {
                        _searchProducts.emit(Resource.Success(searchProductsList))
                    }
                    pagingSearchInfo.bestProductsSearchPage++
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _searchProducts.emit(Resource.Error(it.message.toString()))
                    }
                }
            }
        }
    }


    fun getCategories() {
            viewModelScope.launch {
            _searchCategory.emit(Resource.Loading())
            firestore.collection("Categories").get()
                .addOnSuccessListener { result ->
                    val searchCategoryList = result.toObjects(Category::class.java)
                    viewModelScope.launch {
                        _searchCategory.emit(Resource.Success(searchCategoryList))
                    }
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _searchCategory.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }
}
internal data class PagingSearchInfo(
    var bestProductsSearchPage: Long = 1,
    var oldBestProducts: List<Product> = emptyList(),
    var isPagingEnd: Boolean = false
)