package com.example.ecommercefurniture.fragments.shopping

import android.os.Bundle
import android.util.Log
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
        onCategoryClick()



        lifecycleScope.launchWhenStarted {
            viewModel.searchProducts.collectLatest {

                when (it) {
                    is Resource.Loading -> {
                    //    binding.searchProgressBar.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {

                        searchProductsAdapter.differ.submitList(it.data)
                        binding.searchProgressBar.visibility = View.GONE

                    }

                    is Resource.Error -> {
                        binding.searchProgressBar.visibility = View.GONE
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.searchCategory.collectLatest {

                when (it) {
                    is Resource.Loading -> {
                        binding.searchProgressBar.visibility = View.VISIBLE
                    }

                    is Resource.Success -> {
                        searchCategoryAdapter.differ.submitList(it.data)

                    }

                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun onCategoryClick() {
        searchCategoryAdapter.onItemClick = { category ->
            var position = 0
            when (category.names) {
                resources.getString(R.string.g_chair) -> position = 1
                resources.getString(R.string.g_cupboard) -> position = 2
                resources.getString(R.string.g_table) -> position = 3
                resources.getString(R.string.g_accessory) -> position = 4
                resources.getString(R.string.g_furniture) -> position = 5
            }

            navigateToCategory(position)
        }
    }

    private fun navigateToCategory(position: Int) {
        Log.d("Navigation", "Navigating to position: $position")
        val fragmentTransaction = parentFragmentManager.beginTransaction()

        when (position) {
            1 -> fragmentTransaction.replace(R.id.fragmentContainer, ChairFragment())
            2 -> fragmentTransaction.replace(R.id.fragmentContainer, CupboardFragment())
            3 -> fragmentTransaction.replace(R.id.fragmentContainer, TableFragment())
            4 -> fragmentTransaction.replace(R.id.fragmentContainer, AccesoryFragment())
            5 -> fragmentTransaction.replace(R.id.fragmentContainer, FurnitureFragment())
        }

        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
        Log.d("Navigation", "FragmentTransaction committed successfully")
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
            layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = searchCategoryAdapter

        }
    }
}

