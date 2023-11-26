package com.mobdeve.s15.nadela.oliva.quizon.myapplication.holders

import android.content.Context
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.R
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.databinding.ItemTransactionBinding
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.models.ItemModel

class ItemViewHolder(private val binding: ItemTransactionBinding, private val context: Context) : RecyclerView.ViewHolder(binding.root) {
    var dropdown = binding.actvDropdownItem

    fun bind(item: ItemModel) {
        binding.tvItemCode.text = item.id
        binding.tvItemCategory.text = item.itemCategory


    }


}
