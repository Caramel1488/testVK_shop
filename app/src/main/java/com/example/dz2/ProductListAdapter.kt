package com.example.dz2

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dz2.databinding.ItemListProductLayoutBinding
import com.example.dz2.networking.Product

class ProductListAdapter(
    private val onClick: (position: Int) -> Unit
) : PagingDataAdapter<Product, ProductListAdapter.ProductHolder>(COMPARATOR) {

    fun getList(): List<Product>{
        return this.snapshot().items
    }

    override fun onBindViewHolder(holder: ProductHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductHolder {
        val binding =
            ItemListProductLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductHolder(binding, onClick)
    }

    class ProductHolder(
        private val binding: ItemListProductLayoutBinding,
        private val onClick: (position: Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                onClick(adapterPosition)
            }
        }

        fun bind(product: Product) {
            val imagePath = product.images[0]
            Glide.with(itemView)
                .load(imagePath)
                .into(binding.imageView)
            with(binding){
                productTitleTextView.text = product.title
                val newPrice = (product.price * (1 - product.discountPercentage / 100)).toInt()
                val oldPriceText = itemView.context.getString(R.string.old_price, product.price.toInt())
                productNewPriceTextView.text = newPrice.toString()
                productPriceTextView.text = Html.fromHtml(oldPriceText)
                productDiscTextView.text = "-${product.discountPercentage} %"
            }
        }

    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<Product>() {
            override fun areItemsTheSame(oldProduct: Product, newProduct: Product): Boolean {
                return oldProduct.id == newProduct.id
            }

            override fun areContentsTheSame(oldProduct: Product, newProduct: Product): Boolean {
                return oldProduct == newProduct
            }
        }
    }


}