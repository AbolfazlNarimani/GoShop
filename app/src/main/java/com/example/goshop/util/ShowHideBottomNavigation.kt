package com.example.goshop.util

import androidx.fragment.app.Fragment
import com.example.goshop.activities.ShoppingActivity
import com.example.goshop.R
import com.google.android.material.bottomnavigation.BottomNavigationView

fun Fragment.hideBottomNavigationView(){
    val bottomNavigationView =
        (activity as ShoppingActivity).findViewById<BottomNavigationView>(
            com.example.goshop.R.id.bottomNavigation
        )
    bottomNavigationView.visibility = android.view.View.GONE
}

fun Fragment.showBottomNavigationView(){
    val bottomNavigationView =
        (activity as ShoppingActivity).findViewById<BottomNavigationView>(
            com.example.goshop.R.id.bottomNavigation
        )
    bottomNavigationView.visibility = android.view.View.VISIBLE
}