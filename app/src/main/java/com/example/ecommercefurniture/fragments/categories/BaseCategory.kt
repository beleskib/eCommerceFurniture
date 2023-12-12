package com.example.ecommercefurniture.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommercefurniture.R
import com.example.ecommercefurniture.adapters.BestProductsAdapter
import com.example.ecommercefurniture.databinding.FragmentBaseCategoryBinding
import com.example.ecommercefurniture.util.showBottomNavigationView

open class BaseCategory() :Fragment(R.layout.fragment_base_category) {
    private lateinit var binding: FragmentBaseCategoryBinding
    protected val offerAdapter: BestProductsAdapter by lazy { BestProductsAdapter() }
    protected val bestProducts: BestProductsAdapter by lazy { BestProductsAdapter() }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBaseCategoryBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOfferRV()
        setupBestProductsRV()

        bestProducts.onClick = {
            val b = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, b)
        }
        offerAdapter.onClick = {
            val b = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_homeFragment_to_productDetailsFragment, b)
        }

        binding.RVoffer.addOnScrollListener(object  : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (!recyclerView.canScrollVertically(1) && dx != 0) {
                    onofferPagingRequest()
                }
            }
        })
        binding.nestedScrollBaseCategory
            .setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener{ view, _, scrollY, _, _ ->
                if (view.getChildAt(0).bottom <= view.height + scrollY) { onBestProductsPagingRequest()
                }
            })
    }

    fun showOfferLoading() {
        binding.offerProductsPB.visibility = View.VISIBLE
    }
    fun hideOfferLoading() {
        binding.offerProductsPB.visibility = View.GONE
    }

    fun showBestProductsLoading() {
        binding.bestProductsPB.visibility = View.VISIBLE
    }
    fun hideBestProductsLoading() {
        binding.bestProductsPB.visibility = View.GONE
    }
    open fun onofferPagingRequest() {

    }
    open fun onBestProductsPagingRequest() {

    }

    private fun setupBestProductsRV() {
        binding.rvBestProducts.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = bestProducts
        }
    }

    private fun setupOfferRV() {
        binding.RVoffer.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = offerAdapter
        }

    }

    override fun onResume() {
        super.onResume()
        showBottomNavigationView()
    }
}