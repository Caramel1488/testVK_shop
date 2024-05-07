package com.example.dz2.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.dz2.*
import com.example.dz2.databinding.MainFragmentLayoutBinding
import com.example.dz2.data.model.Product
import com.example.dz2.ui.adapter.LoadStateAdapter
import com.example.dz2.ui.adapter.ProductListAdapter
import com.example.dz2.utils.Injection
import com.example.dz2.utils.toast
import com.example.dz2.vm.ProductViewModel
import com.example.dz2.vm.UiAction
import com.example.dz2.vm.UiState
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class MainFragment : Fragment(R.layout.main_fragment_layout) {

    private val binding: MainFragmentLayoutBinding by viewBinding(MainFragmentLayoutBinding::bind)

    var productAdapter: ProductListAdapter? = null

    lateinit var viewModel: ProductViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this,
            Injection.provideViewModelFactory(owner = this)
        )[ProductViewModel::class.java]

        binding.bindState(
            uiState = viewModel.state,
            pagingData = viewModel.pagingDataFlow,
            uiActions = viewModel.accept
        )
        viewModel.getAllCategories()

        filterListener()

    }

    private fun MainFragmentLayoutBinding.bindState(
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<Product>>,
        uiActions: (UiAction) -> Unit
    ) {
        productAdapter = ProductListAdapter(){
            val product: Product = productAdapter!!.getList()[it]
            findNavController().navigate(
                MainFragmentDirections.actionMainFragmentToDetailProductFragment(
                    product
                )
            )
        }
        list.adapter = productAdapter?.withLoadStateHeaderAndFooter(
            header = LoadStateAdapter { productAdapter?.retry() },
            footer = LoadStateAdapter { productAdapter?.retry() }
        )

        list.layoutManager = GridLayoutManager(requireContext(), 2)

        bindSearch(
            uiState = uiState,
            onQueryChanged = uiActions
        )
        bindList(
            productListAdapter = productAdapter!!,
            uiState = uiState,
            pagingData = pagingData,
            onScrollChanged = uiActions
        )
    }

    private fun MainFragmentLayoutBinding.bindSearch(
        uiState: StateFlow<UiState>,
        onQueryChanged: (UiAction.Search) -> Unit
    ) {
        searchProduct.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateProductListFromInput(onQueryChanged)
                true
            } else {
                false
            }
        }
        searchProduct.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateProductListFromInput(onQueryChanged)
                true
            } else {
                false
            }
        }

        lifecycleScope.launch {
            uiState
                .map { it.query }
                .distinctUntilChanged()
                .collect {
                    searchProduct.setText(it)
                }
        }
    }

    private fun MainFragmentLayoutBinding.updateProductListFromInput(onQueryChanged: (UiAction.Search) -> Unit) {
        searchProduct.text.trim().let {
            if (it.isNotEmpty()) {
                list.scrollToPosition(0)
                onQueryChanged(UiAction.Search(query = it.toString()))
            }
        }
    }

    private fun MainFragmentLayoutBinding.bindList(
        productListAdapter: ProductListAdapter,
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<Product>>,
        onScrollChanged: (UiAction.Scroll) -> Unit
    ) {

        retryButton.setOnClickListener { productListAdapter.retry() }
        list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0) onScrollChanged(UiAction.Scroll(currentQuery = uiState.value.query))
            }
        })

        val notLoading = productListAdapter.loadStateFlow
            .distinctUntilChanged()
            .map { it.source.refresh is LoadState.NotLoading }

        val hasNotScrolledForCurrentSearch = uiState
            .map { it.hasNotScrolledForCurrentSearch }
            .distinctUntilChanged()

        val shouldScrollToTop = combine(
            notLoading,
            hasNotScrolledForCurrentSearch,
            Boolean::and
        )
            .distinctUntilChanged()

        lifecycleScope.launch {
            pagingData.collectLatest {
                productListAdapter.submitData(it)
            }
        }
        lifecycleScope.launch {
            shouldScrollToTop.collect { shouldScroll ->
                if (shouldScroll) list.scrollToPosition(0)
            }
        }
        lifecycleScope.launch {
            productListAdapter.loadStateFlow.collect { loadState ->
                val isListEmpty =
                    loadState.refresh is LoadState.NotLoading && productListAdapter.itemCount == 0
                emptyList.isVisible = isListEmpty
                list.isVisible = !isListEmpty
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                retryButton.isVisible = loadState.source.refresh is LoadState.Error

                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
                errorState?.let {
                    toast("\uD83D\uDE28 Whoops ${it.error}")
                }
            }
        }
    }

    private fun filterListener() {
        binding.filterImageView.setOnClickListener {
            showFilterDialog()
        }
    }

    private fun showFilterDialog() {
        AlertDialog.Builder(requireActivity())
            .setTitle(R.string.ba_day)
            .setNegativeButton(R.string.filter_no) { _, _ -> }
            .setPositiveButton(R.string.filter_yes) { _, _ -> }
            .setSingleChoiceItems(
                viewModel.categoryList.value?.toTypedArray()?.plus(arrayOf("Clear Filter"))
                    ?: emptyArray(),
                -1
            ) { _, position ->
                //viewModel.setCategoryFilter(position)
            }.create().show()
    }
}