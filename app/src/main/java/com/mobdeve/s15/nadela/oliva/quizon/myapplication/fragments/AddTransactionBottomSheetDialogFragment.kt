package com.mobdeve.s15.nadela.oliva.quizon.myapplication.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.DocumentSnapshot
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.R
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.adapters.TransactionProductItemCardsAdapter
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.databases.StockItemHelper2
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.databases.TransactionsHelper
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.databinding.StudentAddTransactionBinding
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.models.TransactionModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class AddTransactionBottomSheetDialogFragment (private val userID: String): BottomSheetDialogFragment() {

    private lateinit var viewBinding: StudentAddTransactionBinding
    private lateinit var itemsAdapter: TransactionProductItemCardsAdapter
    private lateinit var dropDown: AutoCompleteTextView
    private lateinit var datePickerButton: CardView
    private lateinit var dateSelected: TextView
    private lateinit var submitButton: CardView

    //mapping of available items Pair(item:String, selected:Boolean = false)
    private var availableItems: MutableList<MutableMap.MutableEntry<String, Boolean>> = mutableListOf()
    private var requestedItems: MutableMap<String, String> = mutableMapOf()
    private var mListener: BottomSheetListener? = null
    private var year: Int = 0
    private var month: Int = 0
    private var day: Int = 0


    interface BottomSheetListener {
        fun onDataSent(transaction: TransactionModel)
    }

    override fun onResume() {
        super.onResume()
        setupStationDropDownMenu()
    }

    fun setBottomSheetListener(listener: BottomSheetListener) {
        mListener = listener
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = StudentAddTransactionBinding.inflate(inflater, container, false)

        setupStationDropDownMenu()
        return viewBinding.root
    }

    private fun loadDataFromFirestore(station: String) {
        lifecycleScope.launch(Dispatchers.Main) {
            try {
                availableItems = mutableListOf()
                val documents = withContext(Dispatchers.IO) {
                    StockItemHelper2.getAvailableItems(station)
                }

                if (documents.isEmpty()) throw Exception("No transactions found")
                processData(documents)

                itemsAdapter.updateData(availableItems)
            } catch (e: Exception) {
                // Handle exceptions
                Log.e("EXCEPTION", e.message.toString())
            }
        }
    }

    private fun processData(documents: MutableList<DocumentSnapshot>) {
        // Handle the data on the main thread
        for(document in  documents){
            Log.d("document", document.id)
            val map = mutableMapOf(document.get("itemCategory").toString() to false)
            availableItems.add(map.entries.first())
            Log.d("document", availableItems.last().key)
        }
    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAvailableItems()
        loadDataFromFirestore(viewBinding.actvDropdownItem.text.toString())

        datePickerButton = viewBinding.cvDatePickerButton
        dateSelected = viewBinding.tvDate

        datePickerButton.setOnClickListener {
            showExpectedDatePicker()
        }

        submitButton = viewBinding.cvSubmitButton

        submitButton.setOnClickListener{
            try {

                if (dateSelected.text.isEmpty()) throw Exception("ERROR: Please Input an Expected Return Date.")

                var i = 0
                availableItems.forEach { item -> if (!item.value) i++ }
                if (i == availableItems.size) throw Exception("ERROR: Please Select at Least One Item.")

                submitTransaction(viewBinding.actvDropdownItem.text.toString())
                dismiss()
            }catch (e: Exception){
                Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
            }
        }

    }

    @OptIn(DelicateCoroutinesApi::class)
    @Throws(Exception::class)
    private fun submitTransaction(station: String) {

        val timeZone = TimeZone.getTimeZone("Asia/Singapore")
        val calendar = Calendar.getInstance(timeZone)

        Log.d("STATIONCREATE", station)
        Log.d("ITEMSCREATE", availableItems.toList().toString())

        GlobalScope.launch(Dispatchers.Main) {

            requestedItems = mutableMapOf()
            for (item in availableItems.toList()){
                if (item.value){
                    Log.d("ITEMSCREATE", item.key)
                    val documents = withContext(Dispatchers.IO) {
                        StockItemHelper2.getAvailableItem(station, item.key)
                    }
                    if (documents.isEmpty()) throw Exception("ERROR: No available ${item.key} at the moment. Please try again later.")
                    requestedItems[item.key] = documents.first().id
                    Log.d("ITEMSCREATED", requestedItems[item.key].toString())
                }
            }

            Log.d("REQUEST_ITEMS", requestedItems.toString())


            val transaction = TransactionModel(
                userID,
                station,
                "Requested",
                "${calendar.get(Calendar.DAY_OF_MONTH)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.YEAR)}",
                dateSelected.text.toString(),
                "",
                requestedItems,
                "",
                ""
            )

            mListener?.onDataSent(TransactionsHelper.addStudentTransaction(transaction))

        }

        Log.d("REQUEST_ITEMS", requestedItems.keys.toString())

    }

    @SuppressLint("SetTextI18n")
    private fun showExpectedDatePicker() {

        // Set the time zone to Singapore
        val timeZone = TimeZone.getTimeZone("Asia/Singapore")
        val calendar = Calendar.getInstance(timeZone)

        if (year == 0 && month == 0 && day == 0) {
            year = calendar.get(Calendar.YEAR)
            month = calendar.get(Calendar.MONTH)
            day = calendar.get(Calendar.DAY_OF_MONTH)
        }

        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val minDate = calendar.timeInMillis

        calendar.add(Calendar.DAY_OF_MONTH, 6)
        val maxDate = calendar.timeInMillis

        val datePickerDialog = DatePickerDialog(
            requireContext(),
            DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
                // Do something with the selected date
                dateSelected.text = "$selectedDay-${selectedMonth + 1}-$selectedYear"
                // You can use the selectedDate as needed

                year = selectedYear
                month = selectedMonth
                day = selectedDay
            },
            year,
            month,
            day
        )
        // Set the minimum and maximum dates
        datePickerDialog.datePicker.minDate = minDate
        datePickerDialog.datePicker.maxDate = maxDate

        datePickerDialog.show()


//        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore"))
//        if (year == 0 && month == 0 && day == 0) {
//            year = calendar.get(Calendar.YEAR)
//            month = calendar.get(Calendar.MONTH)
//            day = calendar.get(Calendar.DAY_OF_MONTH)
//        }
//
//        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
//            // Handle the selected date
//            val selectedCalendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore"))
//            selectedCalendar.set(selectedYear, selectedMonth, selectedDay)
//            selectedDate = selectedCalendar.timeInMillis
//
//            year = selectedYear
//            month = selectedMonth
//            day = selectedDay
//
//
//            // You can convert the timestamp to a formatted date if needed
//            dateSelected.text = convertTimeToDate(selectedDate)
//            // Show or use the selected date as needed
//            // For example, you can update a TextView with the selected date
//            // textViewSelectedDate.text = selectedDateString
//
//        }, year, month, day)
//
//
//        // Set constraints for now and seven days from now
//        val now = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore")).timeInMillis
//        val sevenDaysFromNow = now + (6 * 24 * 60 * 60 * 1000) // 7 days in milliseconds
//        datePickerDialog.datePicker.minDate = now
//        datePickerDialog.datePicker.maxDate = sevenDaysFromNow
//        datePickerDialog.show()
    }

    private fun convertTimeToDate(selected: Long): String {
        val utc = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore"))
        utc.timeInMillis = selected
        return SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(utc.timeZone)
    }

    private fun setupStationDropDownMenu() {
        val stations = resources.getStringArray(R.array.stations_list)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.component_station_dropdown_item, stations)
        dropDown = viewBinding.actvDropdownItem
        dropDown.setAdapter(arrayAdapter)

        dropDown.onItemClickListener =
            AdapterView.OnItemClickListener { parent, _, position, _ ->
                // This code will be executed when an item is selected
                val selectedItem = parent.getItemAtPosition(position).toString()
                // Update the TextView to display the selected item
                Log.d("SELECTED", selectedItem)
                loadDataFromFirestore(selectedItem)
            }
    }



    private fun setupAvailableItems(){

        // Set up RecyclerView with the new adapter
        itemsAdapter = TransactionProductItemCardsAdapter(availableItems, object : TransactionProductItemCardsAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, selected: Boolean) {
                val newEntry = mutableMapOf(availableItems[position].key to selected)
                availableItems[position] = newEntry.entries.first()
            }

        })

        viewBinding.rvAvailableItems.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        viewBinding.rvAvailableItems.adapter = itemsAdapter

    }

}