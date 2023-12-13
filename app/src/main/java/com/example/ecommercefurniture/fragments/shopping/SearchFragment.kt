package com.example.ecommercefurniture.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommercefurniture.Activities.ShoppingActivity
import com.example.ecommercefurniture.R
import com.example.ecommercefurniture.adapters.SearchCategoryAdapter
import com.example.ecommercefurniture.adapters.SearchProductAdapter
import com.example.ecommercefurniture.data.Category
import com.example.ecommercefurniture.data.Product
import com.example.ecommercefurniture.databinding.FragmentSearchBinding
import com.example.ecommercefurniture.fragments.categories.AccesoryFragment
import com.example.ecommercefurniture.fragments.categories.ChairFragment
import com.example.ecommercefurniture.fragments.categories.CupboardFragment
import com.example.ecommercefurniture.fragments.categories.FurnitureFragment
import com.example.ecommercefurniture.fragments.categories.TableFragment
import com.example.ecommercefurniture.util.Resource
import com.example.ecommercefurniture.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.util.Locale

private val TAG = "SearchFragment"

@AndroidEntryPoint
class SearchFragment: Fragment(R.layout.fragment_search) {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchCategoryAdapter: SearchCategoryAdapter
    private lateinit var searchProductsAdapter: SearchProductAdapter
    private lateinit var viewModel: SearchViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = (activity as ShoppingActivity).viewModela
        viewModel.getCategories()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater)
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSearchCategoryRV()
        setupSearchProductsRV()

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    filterProducts(it)
                }
                return true
            }
        })
        lifecycleScope.launchWhenStarted {
            viewModel.searchProducts.collectLatest { result ->
                when (result) {
                    is Resource.Success -> {
                        searchProductsAdapter.differ.submitList(result.data)
                        binding.searchProgressBar.visibility = View.GONE
                    }

                    is Resource.Error -> {
                        binding.searchProgressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    }

                    is Resource.Loading -> {
                        binding.searchProgressBar.visibility = View.VISIBLE
                    }

                    else -> {Unit}
                }
            }
        }
        searchProductsAdapter.onClick = { product ->
            navigateToProductOptions(product)
        }


        lifecycleScope.launchWhenStarted {
            viewModel.searchCategory.collectLatest {
                when (it) {
                    is Resource.Loading -> { }
                    is Resource.Success -> { searchCategoryAdapter.differ.submitList(it.data) }
                    is Resource.Error -> { Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show() }
                    else -> Unit
                }
            }
        }
        searchCategoryAdapter.onItemClick = { category ->
            navigateToCategoryFragment(category)
        }
    }

    private fun navigateToCategoryFragment(category: Category) {
        val fragmentTransaction = parentFragmentManager.beginTransaction()
        val categoryName = category.names

        when (categoryName) {
            "Chair" -> fragmentTransaction.replace(R.id.fragmentContainer, ChairFragment())
            "Cupboard" -> fragmentTransaction.replace(R.id.fragmentContainer, CupboardFragment())
            "Table" -> fragmentTransaction.replace(R.id.fragmentContainer, TableFragment())
            "Accessory" -> fragmentTransaction.replace(R.id.fragmentContainer, AccesoryFragment())
            "Furniture" -> fragmentTransaction.replace(R.id.fragmentContainer, FurnitureFragment())
            else -> {
                    //Nada
                }
        }
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }

    private fun filterProducts(query: String) {
        val searchText = query.toLowerCase(Locale.getDefault())
        val filteredList = viewModel.searchProducts.value.data?.filter {
            it.name.toLowerCase(Locale.getDefault()).contains(searchText)
        }

        filteredList?.let {
            searchProductsAdapter.differ.submitList(it)
        }
    }
    private fun setupSearchProductsRV() {
        searchProductsAdapter = SearchProductAdapter()
        binding.rvSearchProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            adapter = searchProductsAdapter
        }
    }
    private fun setupSearchCategoryRV() {
        searchCategoryAdapter = SearchCategoryAdapter(requireView())
        binding.rvCategories.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = searchCategoryAdapter

        }
    }
    private fun navigateToProductOptions(product: Product) {
        val action = SearchFragmentDirections.actionSearchFragmentToProductDetailsFragment(product)
        findNavController().navigate(action)
    }
}

