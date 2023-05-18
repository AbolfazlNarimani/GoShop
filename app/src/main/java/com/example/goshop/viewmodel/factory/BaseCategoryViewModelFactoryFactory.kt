package com.example.goshop.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.goshop.data.Category
import com.example.goshop.viewmodel.CategoryViewModel
import com.google.firebase.firestore.FirebaseFirestore

class BaseCategoryViewModelFactoryFactory(
    private val firestore: FirebaseFirestore,
    private val category: Category
): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CategoryViewModel(firestore,category) as T
    }
}