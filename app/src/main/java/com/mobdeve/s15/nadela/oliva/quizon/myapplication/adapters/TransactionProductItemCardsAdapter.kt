package com.mobdeve.s15.nadela.oliva.quizon.myapplication.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.databinding.ComponentProductItemLayoutBinding
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.holders.TransactionProductItemCardsViewHolder

class TransactionProductItemCardsAdapter () : RecyclerView.Adapter<TransactionProductItemCardsViewHolder>() {

    private lateinit var data: MutableList<MutableMap.MutableEntry<String, Boolean>>
    private lateinit var onItemClickListener: OnItemClickListener
    private var selectable: Boolean = false

    interface OnItemClickListener {
        fun onItemClick(position: Int, selected: Boolean)
    }

    constructor(requestItems: MutableList<MutableMap.MutableEntry<String, Boolean>>) : this() {
        this.data = requestItems

    }

    constructor(items: MutableList<MutableMap.MutableEntry<String, Boolean>>, onItemClickListener: Any) : this() {
        this.data = items
        this.onItemClickListener = onItemClickListener as OnItemClickListener
        this.selectable = true
    }

    constructor(selectable: Boolean, items: MutableList<MutableMap.MutableEntry<String, Boolean>>, onItemClickListener: Any) : this() {
        this.data = items
        this.onItemClickListener = onItemClickListener as OnItemClickListener
        this.selectable = selectable
    }



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransactionProductItemCardsViewHolder {

        val itemViewBinding: ComponentProductItemLayoutBinding = ComponentProductItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return TransactionProductItemCardsViewHolder(itemViewBinding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: TransactionProductItemCardsViewHolder, position: Int) {
        val item = data[position].key
        val isSelected = data[position].value

        //if not editMode, do not make a border
        //else make a border
        holder.bindData(item, isSelected, selectable)

        if (selectable){
            holder.itemView.isSelected = isSelected
            holder.itemView.setOnClickListener{
                onItemClickListener.onItemClick(position, !isSelected)
                notifyItemChanged(position)
            }
        }


    }


    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: MutableList<MutableMap.MutableEntry<String, Boolean>>) {
        this.data = newData
        Log.d("DataSetAfter", data.last().toString())
        notifyDataSetChanged()
    }

}