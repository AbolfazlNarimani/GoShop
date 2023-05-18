package com.example.goshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.goshop.data.Product
import com.example.goshop.fragments.categories.SearchFragment
import com.example.goshop.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
): ViewModel() {

    private val _searchResult = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val specialProducts: StateFlow<Resource<List<Product>>> = _searchResult


    init {

    }


    fun fetchSpecialProducts(searchPram: String) {
        viewModelScope.launch {
            _searchResult.emit(Resource.Loading())
        }
        firestore.collection("Products")
            .whereEqualTo("name", searchPram.trim()).get().addOnSuccessListener { result ->
                val specialProductsList = result.toObjects(Product::class.java)
                viewModelScope.launch {
                    _searchResult.emit(Resource.Success(specialProductsList))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _searchResult.emit(Resource.Error(it.message.toString()))
                }
            }
    }

}

