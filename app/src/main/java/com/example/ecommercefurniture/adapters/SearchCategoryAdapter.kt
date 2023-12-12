package com.example.ecommercefurniture.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.ecommercefurniture.data.Category
import com.example.ecommercefurniture.databinding.SearchCategoriesRvItemBinding

class SearchCategoryAdapter(private val parentView: View) :
    RecyclerView.Adapter<SearchCategoryAdapter.SearchCategoryViewHolder>() {

    inner class SearchCategoryViewHolder(private val binding: SearchCategoriesRvItemBinding):
    RecyclerView.ViewHolder(binding.root){
        fun bind(category: Category) {
            binding.apply {
                Log.d("CategoryAdapter", "Category name: ${category.names}")
                Glide.with(itemView)
                    .load(category.image.firstOrNull()).into(imgSearchProductRV)
                tvSearchedName.text = category.names
                itemView.setOnClickListener {
                    onItemClick?.invoke(category)
                }
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
           return oldItem.rank == oldItem.rank
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
           return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diffCallback)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchCategoryAdapter.SearchCategoryViewHolder {
        return SearchCategoryViewHolder(
            SearchCategoriesRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun onBindViewHolder(
        holder: SearchCategoryAdapter.SearchCategoryViewHolder,
        position: Int
    ) {
        val category = differ.currentList[position]
        holder.bind(category)
        holder.itemView.setOnClickListener {
            onClick?.invoke(category)

        }
    }


    override fun getItemCount(): Int {
        return differ.currentList.size
    }
    var onClick:((Category) -> Unit)? = null
    var onItemClick :((Category)->Unit)?=null
}