package com.example.dz2.ui.fragment

import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.dz2.R
import com.example.dz2.databinding.DetailProductFragmentLayoutBinding
import com.example.dz2.data.model.Product
import com.example.dz2.ui.adapter.DetailImagesListAdapter
import com.example.dz2.utils.autoCleared


class DetailProductFragment: Fragment(R.layout.detail_product_fragment_layout) {

    private val binding: DetailProductFragmentLayoutBinding by viewBinding(DetailProductFragmentLayoutBinding::bind)
    private val args: com.example.dz2.ui.DetailProductFragmentArgs by navArgs()
    private var detailImagesListAdapter: DetailImagesListAdapter by autoCleared()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        bindData(args.detailProduct)

    }

    private fun initAdapter() {
        detailImagesListAdapter = DetailImagesListAdapter()

        with(binding.detailImagesList){
            adapter = detailImagesListAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            addItemDecoration(DividerItemDecoration(context, LinearLayout.VERTICAL))
            setHasFixedSize(true)
        }
        val snapHelper: SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.detailImagesList)
    }

    private fun bindData(detailProduct: Product){
        with(binding){
            productTitleTextView.text = detailProduct.title
            productDescTextView.text = detailProduct.description
            val newPrice = (detailProduct.price * (1 - detailProduct.discountPercentage / 100)).toInt()
            val oldPriceText = getString(R.string.old_price, detailProduct.price.toInt())
            productNewPriceTextView.text = newPrice.toString()
            productPriceTextView.text = Html.fromHtml(oldPriceText)
            productDiscTextView.text = "-${detailProduct.discountPercentage.toInt()} %"
            productRatingTextView.text = "${detailProduct.rating}/5 in ${detailProduct.category}"
            detailImagesListAdapter.submitList(detailProduct.images)
        }
    }

}