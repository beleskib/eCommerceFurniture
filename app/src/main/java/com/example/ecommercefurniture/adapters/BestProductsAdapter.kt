package com.example.ecommercefurniture.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommercefurniture.data.Product
import com.example.ecommercefurniture.databinding.ProductRvItemBinding
import com.example.ecommercefurniture.helper.getProductPrice

class BestProductsAdapter:
RecyclerView.Adapter<BestProductsAdapter.BestProductsViewHolder>() {

    inner class BestProductsViewHolder(private val binding: ProductRvItemBinding):
            RecyclerView.ViewHolder(binding.root){
                fun bind(product: Product) {
                    binding.apply {
                        if (product.offerPercentage != null) {
                            val discountPercentage = product.offerPercentage
                            val discountedPrice = product.price - (product.price * discountPercentage / 100)
                            tvBestProductsNewPrice.text = "$ ${String.format("%.2f", discountedPrice)}"
                            tvBestProductPrice.paintFlags = tvBestProductPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                        } else {
                            tvBestProductsNewPrice.visibility = View.INVISIBLE
                        }
                        /*val priceAfterOffer = product.offerPercentage?.getProductPrice(product.price) ?: product.price
                        tvBestProductsNewPrice.text = "$ ${String.format("%.2f", priceAfterOffer)}"
                        tvBestProductPrice.paintFlags = tvBestProductPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                        if(product.offerPercentage == null)
                            tvBestProductsNewPrice.visibility = View.INVISIBLE*/

                        Glide.with(itemView)
                            .load(product.images[0]).into(imgBestProductRV)
                        tvBestProductName.text = product.name
                        tvBestProductPrice.text = "${product.price}$"



                    }
                }
            }
    private val diffCallback = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestProductsViewHolder {
        return BestProductsViewHolder(
            ProductRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BestProductsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)

        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
        }
    }
    var onClick:((Product) -> Unit)? = null
}