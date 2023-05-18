package com.example.goshop.fragments.categories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.goshop.R
import com.example.goshop.adapters.BestProductsAdapter
import com.example.goshop.data.Category
import com.example.goshop.data.Product
import com.example.goshop.databinding.FragmentSearchBinding
import com.example.goshop.util.Constants
import com.example.goshop.util.Resource
import com.example.goshop.viewmodel.CategoryViewModel
import com.example.goshop.viewmodel.PagingInfo
import com.example.goshop.viewmodel.SearchViewModel
import com.example.goshop.viewmodel.factory.BaseCategoryViewModelFactoryFactory
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.BindsInstance
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {

    private lateinit var binding: FragmentSearchBinding
    private val bestProductsAdapter: BestProductsAdapter by lazy { BestProductsAdapter() }
    private val viewModel by viewModels<SearchViewModel>()
    val firestore = Firebase.firestore

    private val pagingInfo = PagingInfo()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.etSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                getResult()
                true
            }
            false
        }

        binding.imageClose.setOnClickListener { findNavController().navigateUp() }

        binding.buttonSearch.setOnClickListener {
            getResult()

        }

        getResult()
        setupBestProducts()

        bestProductsAdapter.onClick = {
            val b = Bundle().apply { putParcelable("product", it) }
            findNavController().navigate(R.id.action_searchFragment3_to_productDetailsFragment, b)
        }

        binding.nestedScrollSearch.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
            if (v.getChildAt(0).bottom <= v.height + scrollY) {
                getResult()
            }
        })

    }

    private fun getResult() {
        if (!pagingInfo.isPagingEnd) {
            val searchPram = binding.etSearch.text.toString().trim()
            firestore.collection(Constants.PRODUCTS).whereEqualTo(Constants.NAME, searchPram)
                .limit(pagingInfo.bestProductsPage * 10).get()
                .addOnSuccessListener { result ->
                    val bestDealsProducts = result.toObjects(Product::class.java)
                    bestProductsAdapter.differ.submitList(bestDealsProducts)
                    pagingInfo.bestProductsPage++
                    val searchProducts = result.toObjects(Product::class.java)
                    pagingInfo.isPagingEnd = searchProducts == pagingInfo.oldBestProducts
                    pagingInfo.oldBestProducts = searchProducts
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), it.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
        }
    }

    private fun setupBestProducts() {
        binding.rvBestProducts.apply {
            layoutManager =
                GridLayoutManager(requireContext(), 2, GridLayoutManager.VERTICAL, false)
            adapter = bestProductsAdapter
        }
    }

}

internal data class PagingInfo(
    var bestProductsPage: Long = 1,
    var oldBestProducts: List<Product> = emptyList(),
    var isPagingEnd: Boolean = false
)