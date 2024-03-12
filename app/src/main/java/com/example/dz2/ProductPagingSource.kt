package com.example.dz2

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.dz2.networking.Product
import com.example.dz2.networking.ProductService
import retrofit2.HttpException
import java.io.IOException

private const val GIF_STARTING_PAGE_INDEX = 1
const val ITEMS_PER_PAGE = 10

class ProductPagingSource(
    private val service: ProductService,
    private val query: String,
    private val category: Pair<Boolean, String>
) : PagingSource<Int, Product>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        val position = params.key ?: GIF_STARTING_PAGE_INDEX

        return try {
            val response = if (category.first) {
                service.searchByCategory(category.second)
            } else {
                service.searchProducts(query, position * ITEMS_PER_PAGE, ITEMS_PER_PAGE)
            }
            Log.d("Response", response.toString())
            val products = response.data
            val nextKey = if (products.isEmpty()) {
                null
            } else {
                position + (params.loadSize / ITEMS_PER_PAGE)
            }
            LoadResult.Page(
                data = products,
                prevKey = if (position == GIF_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )
        } catch (e: IOException) {
            Log.e("ResponseIOE", e.toString())
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            Log.e("ResponseHTTPE", e.toString())
            return LoadResult.Error(e)
        } catch (e: Throwable) {
            Log.e("ResponseE", e.toString())
            return LoadResult.Error(e)
        }

    }

    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}