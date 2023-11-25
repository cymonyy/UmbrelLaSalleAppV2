package com.mobdeve.s15.nadela.oliva.quizon.myapplication.holders

import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.R
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.databinding.ComponentProductItemLayoutBinding

class TransactionProductItemCardsViewHolder (itemView: ComponentProductItemLayoutBinding): RecyclerView.ViewHolder(itemView.root) {
    private var card = itemView.cvItemCard
    private var icon = itemView.ivItemIcon
    private var label = itemView.tvItemLabel

    fun bindData(item: String, isSelected: Boolean, selectable: Boolean){
        label.text = item

        when(item){
            "Umbrella" -> icon.setImageResource(R.drawable.icon_item_umbrella)
            "Boots" -> icon.setImageResource(R.drawable.icon_item_boots)
            "Rain Coat" -> icon.setImageResource(R.drawable.icon_item_raincoat)
        }


        card.strokeWidth = if (isSelected) itemView.rootView.resources.getDimensionPixelSize(R.dimen.selected_stroke_width).toFloat().toInt() else 0
    }

}