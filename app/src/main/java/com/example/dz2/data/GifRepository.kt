package com.example.dz2.data


import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.dz2.ui.ProductPagingSource
import com.example.dz2.ui.ITEMS_PER_PAGE
import com.example.dz2.networking.Product
import com.example.dz2.networking.ProductService
import kotlinx.coroutines.flow.Flow


class GifRepository(private val service: ProductService) {

    fun getSearchResultStream(query: String): Flow<PagingData<Product>>{
        return Pager(
            config = PagingConfig(
                pageSize = ITEMS_PER_PAGE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { ProductPagingSource(service, query) }
        ).flow
    }

    suspend fun getAllCategories(): List<String> {
        return service.getAllCategories()
    }
}