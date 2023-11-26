package com.mobdeve.s15.nadela.oliva.quizon.myapplication.holders

import android.annotation.SuppressLint
import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.adapters.TransactionProductItemCardsAdapter
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.databinding.TransactionItemBinding
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.models.TransactionModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


class TransactionViewHolder (private val binding: TransactionItemBinding):
    RecyclerView.ViewHolder(binding.root) {

    private lateinit var itemsAdapter: TransactionProductItemCardsAdapter

    fun bind(transaction: TransactionModel){
        binding.tvTransactionStatusValue.text = transaction.status
        binding.tvTransactionNumberValue.text = transaction.id
        binding.tvStudentNumberValue.text = transaction.borrower

        binding.tvEDRValue.text = formatDateInWords(transaction.expectedReturnDate)
//        binding.tvUmbrellaValue = transaction.requestedItems
        binding.tvDaysLeftValue.text = daysUntil(transaction.expectedReturnDate)

        binding.cvAdminTransaction.isClickable = transaction.status=="Approved"


        binding.rvAdminTransactionItems.layoutManager = LinearLayoutManager(this.itemView.context, LinearLayoutManager.HORIZONTAL, false)
        itemsAdapter = TransactionProductItemCardsAdapter(createRequestedItemsMapping(transaction.requestedItems.keys))
        binding.rvAdminTransactionItems.adapter = itemsAdapter

    }

    private fun createRequestedItemsMapping(request: MutableSet<String>): MutableList<MutableMap.MutableEntry<String, Boolean>>{
        val items: MutableList<MutableMap.MutableEntry<String, Boolean>> = mutableListOf()
        for (x in request){
            val map = mutableMapOf(x to true)
            items.add(map.entries.first())
        }

        return items
    }

    private fun daysUntil(expectedReturnDate: String):String{
        val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val currentDate = LocalDate.now()

        val targetDate = LocalDate.parse(expectedReturnDate, dateFormatter)
        val period = java.time.Period.between(currentDate, targetDate)

        val days = period.days

        return if(days <= 0) {
            "Item/s must be returned now"
        } else {
            "$days ${if (days == 1) "day" else "days"} left to return"
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun formatDateInWords(dateString: String): String? {
        val date: Date? = try {
            val sdf = SimpleDateFormat("dd-MM-yyyy")
            sdf.parse(dateString)
        } catch (e: ParseException) {
            return "Error parsing the date: " + e.message
        }

        // Handle nullable date
        return if (date != null) {
            val formatter = DateTimeFormatter.ofPattern("dd-MMMM-yyyy", Locale.ENGLISH)
            val localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault())
            localDateTime.format(formatter)
        } else {
            "Invalid date format"
        }
    }




}