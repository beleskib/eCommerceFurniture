package com.example.ecommercefurniture.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.ecommercefurniture.R
import com.example.ecommercefurniture.adapters.HomeViewpagerAdapter
import com.example.ecommercefurniture.databinding.FragmentHomeBinding
import com.example.ecommercefurniture.fragments.categories.AccesoryFragment
import com.example.ecommercefurniture.fragments.categories.ChairFragment
import com.example.ecommercefurniture.fragments.categories.CupboardFragment
import com.example.ecommercefurniture.fragments.categories.FurnitureFragment
import com.example.ecommercefurniture.fragments.categories.MainCategoryFragment
import com.example.ecommercefurniture.fragments.categories.TableFragment
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment: Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoriesFragments = arrayListOf<Fragment>(
            MainCategoryFragment(),
            ChairFragment(),
            CupboardFragment(),
            TableFragment(),
            AccesoryFragment(),
            FurnitureFragment()
        )

        val viewPager2Adapter = HomeViewpagerAdapter(categoriesFragments, childFragmentManager, lifecycle)
        binding.viewPagerHome.adapter = viewPager2Adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPagerHome) { tab, position ->
            when(position) {
                0 -> tab.text = "Main"
                1 -> tab.text = "Chair"
                2 -> tab.text = "Cupboard"
                3 -> tab.text = "Table"
                4 -> tab.text = "Accessory"
                5 -> tab.text = "Furniture"
            }
        }.attach()
    }
}