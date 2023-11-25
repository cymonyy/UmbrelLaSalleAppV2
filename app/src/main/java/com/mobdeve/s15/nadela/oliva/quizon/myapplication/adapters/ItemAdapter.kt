package com.mobdeve.s15.nadela.oliva.quizon.myapplication.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
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

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.bind(currentItem)
        holder.setOnItemSelectedListener (object : ItemViewHolder.OnItemSelectedListener {
            override fun onItemsSelected(itemID: String, newStatus: String) {
                this.onItemsSelected(itemID, newStatus)
            }
        })
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
