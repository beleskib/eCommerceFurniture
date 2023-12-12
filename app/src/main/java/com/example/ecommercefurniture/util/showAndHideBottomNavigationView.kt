package com.example.ecommercefurniture.util

import androidx.fragment.app.Fragment
import com.example.ecommercefurniture.Activities.ShoppingActivity
import com.example.ecommercefurniture.R
import com.google.android.material.bottomnavigation.BottomNavigationView

fun Fragment.hideBottomNavigationView(){
    val bottomNavigationView = (activity as ShoppingActivity).findViewById<BottomNavigationView>(
        R.id.bottomNavigation)

    bottomNavigationView.visibility = android.view.View.GONE
}

fun Fragment.showBottomNavigationView(){
    val bottomNavigationView = (activity as ShoppingActivity).findViewById<BottomNavigationView>(
        R.id.bottomNavigation)

    bottomNavigationView.visibility = android.view.View.VISIBLE
}