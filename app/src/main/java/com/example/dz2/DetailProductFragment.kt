package com.example.dz2

import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.example.dz2.databinding.DetailProductFragmentLayoutBinding
import com.example.dz2.networking.Product

class DetailProductFragment: Fragment(R.layout.detail_product_fragment_layout) {

    private val binding: DetailProductFragmentLayoutBinding by viewBinding(DetailProductFragmentLayoutBinding::bind)
    private val args: DetailProductFragmentArgs by navArgs()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindData(args.detailProduct)
    }

    private fun bindData(detailProduct: Product){
        with(binding){
            productTitleTextView.text = detailProduct.title
            Glide.with(requireContext())
                .load(detailProduct.images[0])
                .into(productImageView)
            productDescTextView.text = detailProduct.description
            val newPrice = (detailProduct.price * (1 - detailProduct.discountPercentage / 100)).toInt()
            val oldPriceText = getString(R.string.old_price, detailProduct.price.toInt())
            productNewPriceTextView.text = newPrice.toString()
            productPriceTextView.text = Html.fromHtml(oldPriceText)
            productDiscTextView.text = "-${detailProduct.discountPercentage.toInt()} %"
        }
    }

}