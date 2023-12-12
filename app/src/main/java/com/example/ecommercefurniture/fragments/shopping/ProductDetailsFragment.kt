package com.example.ecommercefurniture.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommercefurniture.R
import com.example.ecommercefurniture.adapters.ColorsAdapter
import com.example.ecommercefurniture.adapters.SizesAdapter
import com.example.ecommercefurniture.adapters.ViewPager2Images
import com.example.ecommercefurniture.data.CartProduct
import com.example.ecommercefurniture.databinding.FragmentProductDetailsBinding
import com.example.ecommercefurniture.util.HorizontalItemDecoration
import com.example.ecommercefurniture.util.Resource
import com.example.ecommercefurniture.util.hideBottomNavigationView
import com.example.ecommercefurniture.viewmodel.DetailsViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class ProductDetailsFragment: Fragment() {
    private val args by navArgs<ProductDetailsFragmentArgs>()

    private lateinit var binding: FragmentProductDetailsBinding
    private val viewPagerAdapter by lazy { ViewPager2Images() }
    private val sizesAdapter by lazy { SizesAdapter() }
    private val colorsAdapter by lazy { ColorsAdapter() }
    private var selectedColor: Int?= null
    private var selectedSizes: String? = null
    private val viewModel by viewModels<DetailsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        hideBottomNavigationView()
        binding = FragmentProductDetailsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = args.product

        setupSizesRV()
        setupColorsRV()
        setupViewPager()

        binding.imggClose.setOnClickListener {
            findNavController().navigateUp()
        }
        sizesAdapter.onItemClick = {
            selectedSizes = it
        }
        colorsAdapter.onItemClick = {
            selectedColor = it
        }
        binding.btnAddtoCart.setOnClickListener {
            viewModel.addUpdateProductInCart(CartProduct(product, 1, selectedColor, selectedSizes))
        }

        lifecycleScope.launchWhenStarted {
            viewModel.addToCart.collectLatest {
                when(it) {
                    is Resource.Loading -> {
                        binding.btnAddtoCart.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.btnAddtoCart.revertAnimation()
                        binding.btnAddtoCart.setBackgroundColor(resources.getColor(R.color.black))
//                        findNavController().navigate(R.id.action_productDetailsFragment_to_cartFragment)
                    }
                    is Resource.Error -> {
                        binding.btnAddtoCart.stopAnimation()
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    }
                    else -> Unit
                }
            }
        }

        binding.apply {
            tvProductName.text = product.name
            tvProductDescription.text = product.description
            if (product.offerPercentage != null) {
                val discountPercentage = product.offerPercentage
                val discountedPrice = product.price - (product.price * discountPercentage / 100)
                tvProductPrice.text = "$ ${String.format("%.2f", discountedPrice)}"
            } else {
                tvProductPrice.text = product.price.toString() + "$"
            }

            if(product.colors.isNullOrEmpty())
                tvProductColors.visibility = View.INVISIBLE
            if(product.sizes.isNullOrEmpty())
                tvProductSizes.visibility = View.INVISIBLE
        }
        viewPagerAdapter.differ.submitList(product.images)
        product.colors?.let {
            colorsAdapter.differ.submitList(it)
        }
        product.sizes?.let {
            sizesAdapter.differ.submitList(it)
        }
    }

    private fun setupViewPager() {
        binding.apply {
            viewPagerProductImages.adapter = viewPagerAdapter
        }
    }

    private fun setupColorsRV() {
        binding.rvColors.apply {
            adapter = colorsAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(HorizontalItemDecoration())
        }
    }

    private fun setupSizesRV() {
        binding.rvSizes.apply {
            adapter = sizesAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(HorizontalItemDecoration())
        }
    }
}

