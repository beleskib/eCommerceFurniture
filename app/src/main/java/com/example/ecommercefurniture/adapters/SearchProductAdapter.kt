package com.example.ecommercefurniture.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommercefurniture.data.Product
import com.example.ecommercefurniture.databinding.SearchRvItemBinding

class SearchProductAdapter:
    RecyclerView.Adapter<SearchProductAdapter.SearchProductsViewHolder>() {



    inner class SearchProductsViewHolder(private val binding: SearchRvItemBinding):
        RecyclerView.ViewHolder(binding.root){
        fun bind(product: Product) {
            binding.apply {
               tvSearchProductName.text = product.name
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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchProductsViewHolder {
        return SearchProductsViewHolder(
            SearchRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: SearchProductsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)

        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
        }
    }
    var onClick:((Product) -> Unit)? = null
}