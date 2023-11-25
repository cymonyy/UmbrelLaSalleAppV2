package com.mobdeve.s15.nadela.oliva.quizon.myapplication.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.holders.TransactionViewHolder
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.R
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.databinding.TransactionItemBinding
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.models.TransactionModel

class TransactionsAdapter (private var transactions: MutableList<TransactionModel>) :
    RecyclerView.Adapter<TransactionViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val binding = TransactionItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TransactionViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]

        when (transaction.status) {
            "Approved" -> {
                holder.bind(transaction)
                holder.itemView.findViewById<CardView>(R.id.cvTransaction).setCardBackgroundColor(
                    Color.parseColor("#54C220"))
            }
            "Returned" -> {
                holder.bind(transaction)
                holder.itemView.findViewById<CardView>(R.id.cvTransaction).setCardBackgroundColor(
                    Color.parseColor("#979797"))
            }
            else -> {
                holder.itemView.visibility = View.GONE
                return
            }
        }

        holder.itemView.findViewById<TextView>(R.id.tvTransactionStatusValue).setTypeface(null, Typeface.BOLD)
        holder.itemView.findViewById<TextView>(R.id.tvTransactionNumberValue).setTypeface(null, Typeface.BOLD)
        holder.itemView.findViewById<TextView>(R.id.tvStudentNumberValue).setTypeface(null, Typeface.BOLD)
        holder.itemView.findViewById<TextView>(R.id.tvEDRValue).setTypeface(null, Typeface.BOLD)

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: MutableList<TransactionModel>){
        transactions = newData
        notifyDataSetChanged()
    }

}