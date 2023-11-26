package com.mobdeve.s15.nadela.oliva.quizon.myapplication.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.databinding.StudentTransactionItemLayoutBinding
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.fragments.EditTransactionBottomSheetDialogFragment
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.holders.ListOfBorrowedTransactionsViewHolder
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.models.TransactionModel

class ListOfBorrowedTransactionsAdapter(private var data: MutableList<TransactionModel>): RecyclerView.Adapter<ListOfBorrowedTransactionsViewHolder>() {

    interface ScrollToPositionCallback {
        fun onScrollToPosition(position: Int)
    }

    private var callback: ScrollToPositionCallback? = null

    fun setScrollToPositionCallback(callback: ScrollToPositionCallback?) {
        this.callback = callback
    }

    fun smoothScrollToPosition(position: Int) {
        if (callback != null) {
            callback!!.onScrollToPosition(position)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListOfBorrowedTransactionsViewHolder {
        val itemViewBinding: StudentTransactionItemLayoutBinding = StudentTransactionItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ListOfBorrowedTransactionsViewHolder(itemViewBinding, parent.context)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ListOfBorrowedTransactionsViewHolder, position: Int) {
        holder.bindData(data[position])

        holder.itemView.setOnClickListener{
            showBottomSheetDialog(holder.itemView.context, position, data[position])
        }
    }

    private fun showBottomSheetDialog(context: Context, position: Int, transaction: TransactionModel) {
        // Use BottomSheetFragment with view binding
        val bottomSheetFragment = EditTransactionBottomSheetDialogFragment(transaction)
        bottomSheetFragment.setBottomSheetListener(object : EditTransactionBottomSheetDialogFragment.BottomSheetListener {
            override fun onDataSent(transaction: TransactionModel) {
                updateTransactionItem(position, transaction)
                this@ListOfBorrowedTransactionsAdapter.smoothScrollToPosition(position)
            }

            override fun onDeleteData() {
                removeTransactionItem(position)
            }
        })
        val fragmentManager = (context as FragmentActivity).supportFragmentManager
        bottomSheetFragment.show(fragmentManager, bottomSheetFragment.tag)
    }

    //  Handles updating a media item in the array list + updates the UI accordingly.
    private fun updateTransactionItem(position: Int, transaction: TransactionModel) {
        data[position] = transaction
        notifyItemChanged(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newData: MutableList<TransactionModel>) {
        data = newData
        Log.d("DataSetAfter", data.last().toString())
        notifyDataSetChanged()
    }
    fun addData(transaction: TransactionModel) {
        this.data.add(transaction)
        notifyItemInserted(this.data.size - 1)
    }

    fun removeTransactionItem(position: Int) {
        data.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, data.size)
    }




}