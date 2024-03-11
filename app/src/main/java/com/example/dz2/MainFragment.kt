package com.example.dz2

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.dz2.databinding.MainFragmentLayoutBinding
import com.example.dz2.networking.Product
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch


class MainFragment : Fragment(R.layout.main_fragment_layout) {

    private val binding: MainFragmentLayoutBinding by viewBinding(MainFragmentLayoutBinding::bind)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProvider(this, Injection.provideViewModelFactory(owner = this))[GifViewModel::class.java]


        binding.bindState(
            uiState = viewModel.state,
            pagingData = viewModel.pagingDataFlow,
            uiActions = viewModel.accept
        )
    }

    private fun MainFragmentLayoutBinding.bindState(
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<Product>>,
        uiActions: (UiAction) -> Unit
    ) {
        val gifAdapter = GifListAdapter()
        list.adapter = gifAdapter.withLoadStateHeaderAndFooter(
            header = ProductLoadStateAdapter { gifAdapter.retry() },
            footer = ProductLoadStateAdapter { gifAdapter.retry() }
        )
        list.layoutManager = GridLayoutManager(requireContext(),5)

        bindSearch(
            uiState = uiState,
            onQueryChanged = uiActions
        )
        bindList(
            gifAdapter = gifAdapter,
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
                updateGifListFromInput(onQueryChanged)
                true
            } else {
                false
            }
        }
        searchProduct.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateGifListFromInput(onQueryChanged)
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

    private fun MainFragmentLayoutBinding.updateGifListFromInput(onQueryChanged: (UiAction.Search) -> Unit) {
        searchProduct.text.trim().let {
            if (it.isNotEmpty()) {
                list.scrollToPosition(0)
                onQueryChanged(UiAction.Search(query = it.toString()))
            }
        }
    }

    private fun MainFragmentLayoutBinding.bindList(
        gifAdapter: ProductListAdapter,
        uiState: StateFlow<UiState>,
        pagingData: Flow<PagingData<Product>>,
        onScrollChanged: (UiAction.Scroll) -> Unit
    ) {

        retryButton.setOnClickListener { gifAdapter.retry() }
        list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy != 0) onScrollChanged(UiAction.Scroll(currentQuery = uiState.value.query))
            }
        })

        val notLoading = gifAdapter.loadStateFlow
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
                gifAdapter.submitData(it)
            }
        }
        lifecycleScope.launch {
            shouldScrollToTop.collect { shouldScroll ->
                if (shouldScroll) list.scrollToPosition(0)
            }
        }
        lifecycleScope.launch {
            gifAdapter.loadStateFlow.collect { loadState ->
                val isListEmpty =
                    loadState.refresh is LoadState.NotLoading && gifAdapter.itemCount == 0
                emptyList.isVisible = isListEmpty
                list.isVisible = !isListEmpty
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                retryButton.isVisible = loadState.source.refresh is LoadState.Error

                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
                errorState?.let {
                    Toast.makeText(
                        requireContext(),
                        "\uD83D\uDE28 Whooops ${it.error}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}