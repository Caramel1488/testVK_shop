package com.example.dz2.data


import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.dz2.ui.paging.ProductPagingSource
import com.example.dz2.ui.paging.ITEMS_PER_PAGE
import com.example.dz2.data.model.Product
import com.example.dz2.data.networking.ProductService
import kotlinx.coroutines.flow.Flow


class ProductRepository(private val service: ProductService) {

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