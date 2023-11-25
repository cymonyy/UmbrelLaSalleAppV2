package com.mobdeve.s15.nadela.oliva.quizon.myapplication.holders

import android.content.Context
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.R
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.databinding.ItemTransactionBinding
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.models.ItemModel

class ItemViewHolder(private val binding: ItemTransactionBinding, private val context: Context) : RecyclerView.ViewHolder(binding.root) {
    private var onItemSelectedListener: OnItemSelectedListener? = null

    interface OnItemSelectedListener {
        fun onItemsSelected(itemID: String, newStatus: String)
    }

    fun bind(item: ItemModel) {
        binding.tvItemCode.text = item.id
        binding.tvItemCategory.text = item.itemCategory

        val status = listOf("Intact", "Damaged")
        val adapter = ArrayAdapter(context, R.layout.component_dropdown_status, status)
        binding.actvDropdownItem.setAdapter(adapter)

        val dropdown = binding.actvDropdownItem

        dropdown.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val selectedStatus = parent.getItemAtPosition(position).toString()

                this.onItemSelectedListener?.onItemsSelected(item.id, selectedStatus)

            }
    }

    fun setOnItemSelectedListener(listener: OnItemSelectedListener){
        this.onItemSelectedListener = listener
    }
}

