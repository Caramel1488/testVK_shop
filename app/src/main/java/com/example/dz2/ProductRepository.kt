package com.example.dz2


import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.dz2.networking.Product
import com.example.dz2.networking.ProductService
import kotlinx.coroutines.flow.Flow


class ProductRepository(private val service: ProductService) {

    fun getSearchResultStream(query: String, category: String): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = ITEMS_PER_PAGE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                ProductPagingSource(
                    service,
                    query,
                    Pair(category.isNotEmpty().and(category.isNotBlank()), category)
                )
            }
        ).flow
    }

    suspend fun getAllCategories(): List<String> {
        return service.getAllCategories()
    }

}