package com.mobdeve.s15.nadela.oliva.quizon.myapplication.holders

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.databinding.InventoryItemBinding
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.models.StockItemModel


class StockItemViewHolder(private val binding: InventoryItemBinding): RecyclerView.ViewHolder(binding.root){
    @SuppressLint("SetTextI18n")
    fun bind(stockItem: StockItemModel){
        binding.tvItemName.text = stockItem.itemCategory
        binding.tvStockValue.text = stockItem.available.toString() + "/" + stockItem.totalStock.toString()
    }

}