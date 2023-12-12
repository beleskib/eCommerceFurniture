package com.example.ecommercefurniture.fragments.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommercefurniture.R
import com.example.ecommercefurniture.adapters.BestDealsProductsAdapter
import com.example.ecommercefurniture.adapters.BestProductsAdapter
import com.example.ecommercefurniture.adapters.SpecialProductsAdapter
import com.example.ecommercefurniture.databinding.FragmentMainCategoryBinding
import com.example.ecommercefurniture.util.Resource
import com.example.ecommercefurniture.util.showBottomNavigationView
import com.example.ecommercefurniture.viewmodel.MainCategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

private val TAG = "MainCategoryFragment"
@AndroidEntryPoint
class MainCategoryFragment: Fragment(R.layout.fragment_main_category) {
    private lateinit var binding: FragmentMainCategoryBinding
    private lateinit var specialProductsAdapter: SpecialProductsAdapter
    private lateinit var bestDealsProductsAdapter: BestDealsProductsAdapter
    private lateinit var bestProductsAdapter: BestProductsAdapter
    private val viewModel by viewModels<MainCategoryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpecialProductsRV()

        setupBestDealsRV()

        setupBestProductsRV()

        specialProductsAdapter.onClick = {
            val b = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, b)
        }
        bestDealsProductsAdapter.onClick = {
            val b = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, b)
        }
        bestProductsAdapter.onClick = {
            val b = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, b)
        }
        lifecycleScope.launchWhenStarted {
            viewModel.specialProducts.collectLatest {
                when(it) {
                    is Resource.Loading -> {
                        showLoading()
                    }
                    is Resource.Success -> {
                        specialProductsAdapter.differ.submitList(it.data)
                        hideLoading()
                    }
                    is Resource.Error -> {
                        hideLoading()
                        Log.e(TAG, it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.bestDealsProducts.collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        showLoading()
                    }
                    is Resource.Success -> {
                        bestDealsProductsAdapter.differ.submitList(it.data)
                        hideLoading()
                    }
                    is Resource.Error -> {
                        hideLoading()
                        Log.e(TAG, it.message.toString())
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
        viewModel.bestProducts.collectLatest {
            when (it) {
                is Resource.Loading -> {
                    binding.BestProductsProgressBar.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    bestProductsAdapter.differ.submitList(it.data)
                    binding.BestProductsProgressBar.visibility = View.GONE

                }
                is Resource.Error -> {
                    binding.BestProductsProgressBar.visibility = View.GONE
                    Log.e(TAG, it.message.toString())
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                else -> Unit
                }
            }
        }
        binding.nestedScrollMainCategory
            .setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{ view,_,scrollY,_,_ ->
            if (view.getChildAt(0).bottom <= view.height + scrollY) {
                viewModel.fetchBestProducts()

            }
        })
    }
    private fun setupBestProductsRV() {
        bestProductsAdapter = BestProductsAdapter()
        binding.rvBestProducts.apply {
            layoutManager = GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = bestProductsAdapter
        }
    }
    private fun setupBestDealsRV() {
        bestDealsProductsAdapter = BestDealsProductsAdapter()
        binding.rvBestDealsProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = bestDealsProductsAdapter
        }
    }
    private fun setupSpecialProductsRV() {
        specialProductsAdapter = SpecialProductsAdapter()
        binding.rvSpecialProducts.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = specialProductsAdapter
        }
    }


    private fun hideLoading() {
        binding.mainCategoryProgressBar.visibility =View.GONE

    }

    private fun showLoading() {
        binding.mainCategoryProgressBar.visibility =View.VISIBLE

    }

    override fun onResume() {
        super.onResume()

        showBottomNavigationView()
    }

}