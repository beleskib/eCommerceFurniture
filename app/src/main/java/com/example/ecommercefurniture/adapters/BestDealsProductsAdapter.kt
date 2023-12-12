package com.example.ecommercefurniture.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommercefurniture.data.Product
import com.example.ecommercefurniture.databinding.BestDealsRvItemBinding

class BestDealsProductsAdapter:
    RecyclerView.Adapter<BestDealsProductsAdapter.BestDealsViewHolder>() {

        inner class BestDealsViewHolder(private val binding:BestDealsRvItemBinding):
                RecyclerView.ViewHolder(binding.root){
                    fun bind(product: Product) {
                        binding.apply {
                            Glide.with(itemView)
                                .load(product.images[0]).into(imgBestDealsRvItem)
                            tvBestDealsProductsName.text = product.name
                            tvBestDealsProductsOldPrice.text = product.price.toString() + "$"
                            val price = product.price
                            val percentage = product.offerPercentage
                            val discountedPrice = price - (price * percentage!!/ 100)
                            tvBestDealsProductNewPrice.text = "$ ${String.format("%.2f", discountedPrice)}"

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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestDealsViewHolder {
        return BestDealsViewHolder(
            BestDealsRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BestDealsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)
        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
        }

    }
    var onClick:((Product) -> Unit)? = null
}