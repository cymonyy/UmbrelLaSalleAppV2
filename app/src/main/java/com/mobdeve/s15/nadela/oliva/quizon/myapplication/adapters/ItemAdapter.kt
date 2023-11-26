package com.mobdeve.s15.nadela.oliva.quizon.myapplication.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.R
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.databinding.ItemTransactionBinding
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.holders.ItemViewHolder
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.models.ItemModel

class ItemAdapter(itemList: MutableList<ItemModel>) : RecyclerView.Adapter<ItemViewHolder>() {

    private var itemList: MutableList<ItemModel>
    private lateinit var binding: ItemTransactionBinding
    private var onItemSelectedListener: ItemAdapter.OnItemSelectedListener? = null

    init {
        this.itemList = itemList
    }

    interface OnItemSelectedListener {
        fun onItemsSelected(itemID: String, newStatus: String)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ItemTransactionBinding.inflate(inflater, parent, false)
        return ItemViewHolder(binding, parent.context)


    }

    override fun getItemCount(): Int {
        return itemList.size
    }


    override fun onBindViewHolder(holder: ItemViewHolder, int: Int) {
        val currentItem = itemList[int]
        holder.bind(currentItem)

        val status = listOf("Intact", "Damaged")
        val adapter = ArrayAdapter(holder.itemView.context, R.layout.component_dropdown_status, status)
        holder.dropdown.setAdapter(adapter)

        holder.dropdown.setOnItemClickListener { parent, view, position, id ->
            val selectedStatus = parent.getItemAtPosition(position).toString()
            this.onItemSelectedListener?.onItemsSelected(currentItem.id, selectedStatus)
        }



//        //binding.actvDropdownItem.setAdapter(adapter)
//
//        val dropdown = binding.actvDropdownItem
//
//        dropdown.onItemClickListener =
//            AdapterView.OnItemClickListener { parent, view, position, id ->
//                val selectedStatus = parent.getItemAtPosition(position).toString()
//
//                this.onItemSelectedListener?.onItemsSelected(item.id, selectedStatus)
//
//            }
//
//        holder.setOnItemSelectedListener (object : ItemViewHolder.OnItemSelectedListener {
//            override fun onItemsSelected(itemID: String, newStatus: String) {
//                this.onItemsSelected(itemID, newStatus)
//                notifyItemChanged(holder.bindingAdapterPosition)
//            }
//        })
    }

    fun setOnItemSelectedListener(listener: ItemAdapter.OnItemSelectedListener){
        this.onItemSelectedListener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateItems(itemList: MutableList<ItemModel>){
        this.itemList = itemList
        notifyDataSetChanged()
    }

}