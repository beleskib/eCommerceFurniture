package com.example.ecommercefurniture.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.ecommercefurniture.data.CartProduct
import com.example.ecommercefurniture.databinding.BillingProductsRvItemBinding

class BillingProductsAdapter: Adapter<BillingProductsAdapter.BillingViewHolder>() {

    inner class BillingViewHolder(val binding: BillingProductsRvItemBinding) :
            ViewHolder(binding.root) {

                fun bind(billingProduct: CartProduct) {
                    binding.apply {
                        Glide.with(itemView).load(billingProduct.product.images[0]).into(imageCartProduct)
                        tvProductCartName.text = billingProduct.product.name
                        tvBillingProductQuantity.text = billingProduct.quantity.toString()

                        if (billingProduct.product.offerPercentage != null) {
                            val discountPercentage = billingProduct.product.offerPercentage
                            val discountedPrice = billingProduct.product.price - (billingProduct.product.price * discountPercentage / 100)
                            tvProductCartPrice.text = "$ ${String.format("%.2f", discountedPrice)}"
                        } else {
                            tvProductCartPrice.text = billingProduct.product.price.toString()
                        }
                        imageCartProductColor.setImageDrawable(ColorDrawable(billingProduct.selectedColor?: Color.TRANSPARENT))
                        tvCartProductSize.text = billingProduct.selectedSize?:"".also {
                            imageCartProductSize.setImageDrawable(ColorDrawable(Color.TRANSPARENT))
                        }
                    }
                }
            }
    private val diffUtil = object : DiffUtil.ItemCallback<CartProduct>() {
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.product == newItem.product
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffUtil)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BillingViewHolder {
        return BillingViewHolder(
            BillingProductsRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BillingViewHolder, position: Int) {
        val billingProduct = differ.currentList[position]

        holder.bind(billingProduct)
    }
}