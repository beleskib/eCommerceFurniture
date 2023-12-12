package com.example.ecommercefurniture.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommercefurniture.data.CartProduct
import com.example.ecommercefurniture.data.Product
import com.example.ecommercefurniture.databinding.CartProductItemBinding
import com.example.ecommercefurniture.databinding.SpecialRvItemBinding
import com.example.ecommercefurniture.helper.getProductPrice

class CartAdapter: RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    inner class CartViewHolder(val binding: CartProductItemBinding):
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(cartProduct: CartProduct) {
            binding.apply {
                Glide.with(itemView)
                    .load(cartProduct.product.images[0]).into(imgCartProduct)
                tvProductCartName.text = cartProduct.product.name
                tvCartProductQuantity.text = cartProduct.quantity.toString()

                if (cartProduct.product.offerPercentage != null) {
                    val discountPercentage = cartProduct.product.offerPercentage
                    val discountedPrice = cartProduct.product.price - (cartProduct.product.price * discountPercentage / 100)
                    tvProductCartPrice.text = "$ ${String.format("%.2f", discountedPrice)}"
                } else {
                    tvProductCartPrice.text = cartProduct.product.price.toString()
                }
                imgProductCartColor.setImageDrawable(ColorDrawable(cartProduct.selectedColor?: Color.TRANSPARENT))
                tvCartProductsize.text = cartProduct.selectedSize?:"".also {
                    imgProductCartSize.setImageDrawable(ColorDrawable(Color.TRANSPARENT))
                }

            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<CartProduct>() {
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.product == newItem.product
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this,diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        return CartViewHolder(
            CartProductItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartProduct = differ.currentList[position]
        holder.bind(cartProduct)

        holder.itemView.setOnClickListener {
            onProductClick?.invoke(cartProduct)
        }
        holder.binding.imgPlus.setOnClickListener {
            onPlusClick?.invoke(cartProduct)
        }
        holder.binding.imgMinus.setOnClickListener {
            onMinusClick?.invoke(cartProduct)
        }
    }
    var onProductClick:((CartProduct) -> Unit)? = null
    var onPlusClick:((CartProduct) -> Unit)? = null
    var onMinusClick:((CartProduct) -> Unit)? = null

}