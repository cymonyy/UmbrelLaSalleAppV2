package com.mobdeve.s15.nadela.oliva.quizon.myapplication.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.firestore.DocumentSnapshot
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.R
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.adapters.TransactionProductItemCardsAdapter
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.databases.StockItemHelper2
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.databases.TransactionsHelper
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.databinding.StudentEditTransactionBinding
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.models.TransactionModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.TimeZone

class EditTransactionBottomSheetDialogFragment (private val transaction: TransactionModel): BottomSheetDialogFragment() {

    private lateinit var viewBinding: StudentEditTransactionBinding
    private lateinit var qrCodeImageView: ImageView
    private lateinit var itemsAdapter: TransactionProductItemCardsAdapter
    private lateinit var datePickerButton: CardView
    private lateinit var dateSelected: TextView
    private lateinit var status: TextView
    private lateinit var station: TextView
    private lateinit var requestNote: EditText
    private lateinit var saveButton: CardView
    private lateinit var removeButton: CardView
    private lateinit var saveButtonContent: LinearLayout
    private lateinit var removeButtonContent: LinearLayout

    private var availableItems: MutableList<MutableMap.MutableEntry<String, Boolean>> = mutableListOf()
    private var initialItemsRequested: MutableMap<String, Boolean> = mutableMapOf()
    private var mListener: BottomSheetListener? = null
    private var year: Int = 0
    private var month: Int = 0
    private var day: Int = 0


    interface BottomSheetListener {
        fun onDataSent(transaction: TransactionModel)
    }
    //
//    override fun onResume() {
//        super.onResume()
//        setupStationDropDownMenu()
//    }
//
    fun setBottomSheetListener(listener: BottomSheetListener) {
        mListener = listener
    }



    //
//
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = StudentEditTransactionBinding.inflate(inflater, container, false)

        qrCodeImageView = viewBinding.ivQRImage
        datePickerButton = viewBinding.cvDatePickerButton
        dateSelected = viewBinding.tvEditDate
        status = viewBinding.tvTransactionStatus
        station = viewBinding.tvTransactionStation
        requestNote = viewBinding.etRequestNote
        saveButton = viewBinding.cvSaveButton
        removeButton = viewBinding.cvRemoveButton
        saveButtonContent = viewBinding.llSaveButtonContent
        removeButtonContent = viewBinding.llRemoveButtonContent

        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //view configurations
        setupRequestedItems()
        loadDataFromFirestore(transaction.station)

        showQRCode()
        dateSelected.text = transaction.expectedReturnDate
        status.text = transaction.status
        station.text = transaction.station
        requestNote.setText(transaction.requestNote)


        when(transaction.status) {
            "Requested" -> {
                datePickerButton.setOnClickListener {
                    showExpectedDatePicker()
                }
                requestNote.isEnabled = true
                saveButtonContent.setBackgroundResource(R.drawable.student_save_gradient_background)
                removeButtonContent.setBackgroundResource(R.drawable.student_remove_gradient_background)

                saveButton.setOnClickListener {  saveChanges()}
                removeButton.setOnClickListener {  }

            }
            "Denied" -> {
                removeButtonContent.setBackgroundResource(R.drawable.student_remove_gradient_background)
                removeButton.setOnClickListener {  }
            }
            "Returned" -> {
                removeButtonContent.setBackgroundResource(R.drawable.student_remove_gradient_background)
                removeButton.setOnClickListener {  }
            }
            "Approved" -> {

            }
        }




//        submitButton.setOnClickListener {
//            try {
//
//                if (dateSelected.text.isEmpty()) throw Exception("ERROR: Please Input an Expected Return Date.")
//                if (itemsAdapter.getSelectedItems().isEmpty()) throw Exception("ERROR: Please Select at Least One Item.")
//
//                updateTransaction(itemsAdapter.getSelectedItems(), itemsAdapter.getUnselectedItems())
//                dismiss()
//            }catch (e: Exception){
//                Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
//            }
//
//
//        }

    }

    private fun saveChanges(){
        try {

            if (dateSelected.text.isEmpty()) throw Exception("ERROR: Please Input an Expected Return Date.")

            var i = 0
            availableItems.forEach { item -> if (!item.value) i++ }
            if (i == availableItems.size) throw Exception("ERROR: Please Select at Least One Item.")

            //submitTransaction(viewBinding.actvDropdownItem.text.toString())
            updateTransaction()
            dismiss()
        }catch (e: Exception){
            Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
        }
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
                createInitialRequestsMapping()

                itemsAdapter.updateData(availableItems)
            } catch (e: Exception) {
                // Handle exceptions
                Log.e("EXCEPTION", e.message.toString())
            }
        }
    }

    private fun createInitialRequestsMapping() {
        initialItemsRequested = mutableMapOf()
        for(item in transaction.requestedItems.keys){
            initialItemsRequested[item] = true
        }
    }

    private fun processData(documents: MutableList<DocumentSnapshot>) {
        // Handle the data on the main thread
        for(document in  documents){
            Log.d("document", document.id)
            val key = document.get("itemCategory").toString()
            val map = mutableMapOf(key to transaction.requestedItems.keys.contains(key))
            availableItems.add(map.entries.first())
            Log.d("document", availableItems.last().key)
        }

    }

    @Throws(Exception::class)
    @OptIn(DelicateCoroutinesApi::class)
    private fun updateTransaction() {
//
        val timeZone = TimeZone.getTimeZone("Asia/Singapore")
        val calendar = Calendar.getInstance(timeZone)
//
        GlobalScope.launch(Dispatchers.Main) {

            //get the keys of all initial requested items that have been unselected
            val unselectedItemKeys = initialItemsRequested.filterNot { it.value }.keys //true --> selected, false --> not selected

            //separate unselected items from the transaction.requestItems
            val unselectedItems = transaction.requestedItems.filter { unselectedItemKeys.contains(it.key) }.toMutableMap()
            val finalInitialRequests = transaction.requestedItems.filterNot { unselectedItemKeys.contains(it.key) }.toMutableMap()

            Log.d("UNITEMS", unselectedItems.entries.toString())
            Log.d("UNITEMS", finalInitialRequests.entries.toString())

            //get the id of all newly selected items
            val newlyAddedKeys =
                availableItems.filter { it.value }
                    .filterNot { finalInitialRequests.containsKey(it.key) }

            Log.d("UNITEMS", newlyAddedKeys.toString())

            val newlyAdded : MutableMap<String, String> = mutableMapOf()
            for (x in newlyAddedKeys){
                Log.d("ITEMSCREATE", x.key)
                val documents = withContext(Dispatchers.IO) {
                    StockItemHelper2.getAvailableItem(transaction.station, x.key)
                }
                if (documents.isEmpty()) throw Exception("ERROR: No available ${x.key} at the moment. Please try again later.")
                newlyAdded[x.key] = documents.first().id
            }

            finalInitialRequests.putAll(newlyAdded)

            Log.d("UNITEMS", newlyAdded.toString())
            Log.d("UNITEMS", finalInitialRequests.toString())


            //update the transaction using unselected, selected, and newly added
            mListener?.onDataSent(
                TransactionsHelper.updateStudentTransaction(
                transaction.id,
                dateSelected.text.toString(),
                requestNote.text.toString(),
                unselectedItems,
                finalInitialRequests
            ))


//            val requestItemIDs: MutableSet<String> = mutableSetOf()
//            for (item in selectedItems) transaction.requestedItems[item]?.let { requestItemIDs.add(it) }
//
//            val unselectedItemIDs: MutableSet<String> = mutableSetOf()
//            for (item in unselectedItems) transaction.requestedItems[item]?.let { unselectedItemIDs.add(it) }
//
//            val date = dateSelected.text.toString()
//
//            //mListener?.onDataSent(TransactionsHelper.updateStudentTransaction(date, requestItemIDs, unselectedItemIDs))
//
//            val transaction = TransactionModel(
//                userID,
//                station,
//                "Requested",
//                "${calendar.get(Calendar.DAY_OF_MONTH)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.YEAR)}",
//                dateSelected.text.toString(),
//                "",
//                requestedItems,
//                "",
//                ""
//            )



        }
//
//        Log.d("REQUEST_ITEMS", requestedItems.keys.toString())


    }



    private fun setupRequestedItems() {

        // Set up RecyclerView with the new adapter
        itemsAdapter = TransactionProductItemCardsAdapter(transaction.status == "Requested",  availableItems, object : TransactionProductItemCardsAdapter.OnItemClickListener {
            override fun onItemClick(position: Int, selected: Boolean) {
                val key = availableItems[position].key

                //make initial item requested to false for unchecking
                if(initialItemsRequested.contains(key)) initialItemsRequested[key] = selected

                //add a new state as replacement to the item
                val newEntry = mutableMapOf(key to selected)
                availableItems[position] = newEntry.entries.first()
            }

        })

        viewBinding.rvRequestedItemsView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        viewBinding.rvRequestedItemsView.adapter = itemsAdapter



//        //set initial data
//        requestedItems = transaction.requestedItems.keys
//
//        // Set up RecyclerView with the new adapter
//        itemsAdapter = TransactionProductItemCardsAdapter(true, requestedItems.toMutableList(), object : TransactionProductItemCardsAdapter.OnItemClickListener {
//            override fun onItemClick(item: String) {
//                //on item click check if it is in selected items
//
//            }
//        })
//
//        viewBinding.rvRequestedItemsView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//        viewBinding.rvRequestedItemsView.adapter = itemsAdapter
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
    }

    private fun showQRCode() {
        try {
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.encodeBitmap(transaction.id, BarcodeFormat.QR_CODE, 400, 400)
            qrCodeImageView.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }




//
//    private fun loadDataFromFirestore(station: String) {
//        lifecycleScope.launch(Dispatchers.Main) {
//            try {
//                availableItems = mutableListOf()
//                val documents = withContext(Dispatchers.IO) {
//                    StockItemHelper2.getAvailableItems(station)
//                }
//
//                if (documents.isEmpty()) throw Exception("No transactions found")
//                processData(documents)
//
//                itemsAdapter.updateData(availableItems)
//            } catch (e: Exception) {
//                // Handle exceptions
//                Log.e("EXCEPTION", e.message.toString())
//            }
//        }
//    }
//
//    private fun processData(documents: MutableList<DocumentSnapshot>) {
//        // Handle the data on the main thread
//        for(document in  documents){
//            Log.d("document", document.id)
//            availableItems.add(document.get("itemCategory").toString())
//            Log.d("document", availableItems.last())
//        }
//
//    }
//
//
//    @SuppressLint("SetTextI18n")
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        setupAvailableItems()
//        loadDataFromFirestore(viewBinding.actvDropdownItem.text.toString())
//
//        datePickerButton = viewBinding.cvDatePickerButton
//        dateSelected = viewBinding.tvDate
//
//        datePickerButton.setOnClickListener {
//            showExpectedDatePicker()
//        }
//
//        submitButton = viewBinding.cvSubmitButton
//
//        submitButton.setOnClickListener{
//            try {
//
//                if (dateSelected.text.isEmpty()) throw Exception("ERROR: Please Input an Expected Return Date.")
//                if (itemsAdapter.getSelectedItems().isEmpty()) throw Exception("ERROR: Please Select at Least One Item.")
//                submitTransaction(viewBinding.actvDropdownItem.text.toString(), itemsAdapter.getSelectedItems())
//                dismiss()
//            }catch (e: Exception){
//                Toast.makeText(requireContext(), e.message, Toast.LENGTH_LONG).show()
//            }
//        }
//
//
//
//    }
//
//    @OptIn(DelicateCoroutinesApi::class)
//    @Throws(Exception::class)
//    private fun submitTransaction(station: String, items: MutableSet<String>) {
//
//        val timeZone = TimeZone.getTimeZone("Asia/Singapore")
//        val calendar = Calendar.getInstance(timeZone)
//
//        Log.d("STATIONCREATE", station)
//        Log.d("ITEMSCREATE", items.toList().toString())
//
//        GlobalScope.launch(Dispatchers.Main) {
//
//            requestedItems = mutableMapOf()
//            for (item in items.toList()){
//                Log.d("ITEMSCREATE", item)
//                val documents = withContext(Dispatchers.IO) {
//                    StockItemHelper2.getAvailableItem(station, item)
//                }
//                if (documents.isEmpty()) throw Exception("ERROR: No available $item at the moment. Please try again later.")
//                requestedItems[item] = documents.first().id
//                Log.d("ITEMSCREATED", requestedItems[item].toString())
//            }
//
//            Log.d("REQUEST_ITEMS", requestedItems.toString())
//
//
//
//
//            val transaction = TransactionModel(
//                userID,
//                station,
//                "Requested",
//                "${calendar.get(Calendar.DAY_OF_MONTH)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.YEAR)}",
//                dateSelected.text.toString(),
//                "",
//                requestedItems,
//                "",
//                ""
//            )
//
//            mListener?.onDataSent(TransactionsHelper.addStudentTransaction(transaction))
//
//        }
//
//        Log.d("REQUEST_ITEMS", requestedItems.keys.toString())
//
//    }
//
//    @SuppressLint("SetTextI18n")
//    private fun showExpectedDatePicker() {
//
//        // Set the time zone to Singapore
//        val timeZone = TimeZone.getTimeZone("Asia/Singapore")
//        val calendar = Calendar.getInstance(timeZone)
//
//        if (year == 0 && month == 0 && day == 0) {
//            year = calendar.get(Calendar.YEAR)
//            month = calendar.get(Calendar.MONTH)
//            day = calendar.get(Calendar.DAY_OF_MONTH)
//        }
//
//        calendar.add(Calendar.DAY_OF_MONTH, 1)
//        val minDate = calendar.timeInMillis
//
//        calendar.add(Calendar.DAY_OF_MONTH, 6)
//        val maxDate = calendar.timeInMillis
//
//        val datePickerDialog = DatePickerDialog(
//            requireContext(),
//            DatePickerDialog.OnDateSetListener { _, selectedYear, selectedMonth, selectedDay ->
//                // Do something with the selected date
//                dateSelected.text = "$selectedDay-${selectedMonth + 1}-$selectedYear"
//                // You can use the selectedDate as needed
//
//                year = selectedYear
//                month = selectedMonth
//                day = selectedDay
//            },
//            year,
//            month,
//            day
//        )
//        // Set the minimum and maximum dates
//        datePickerDialog.datePicker.minDate = minDate
//        datePickerDialog.datePicker.maxDate = maxDate
//
//        datePickerDialog.show()
//
//
////        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore"))
////        if (year == 0 && month == 0 && day == 0) {
////            year = calendar.get(Calendar.YEAR)
////            month = calendar.get(Calendar.MONTH)
////            day = calendar.get(Calendar.DAY_OF_MONTH)
////        }
////
////        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
////            // Handle the selected date
////            val selectedCalendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore"))
////            selectedCalendar.set(selectedYear, selectedMonth, selectedDay)
////            selectedDate = selectedCalendar.timeInMillis
////
////            year = selectedYear
////            month = selectedMonth
////            day = selectedDay
////
////
////            // You can convert the timestamp to a formatted date if needed
////            dateSelected.text = convertTimeToDate(selectedDate)
////            // Show or use the selected date as needed
////            // For example, you can update a TextView with the selected date
////            // textViewSelectedDate.text = selectedDateString
////
////        }, year, month, day)
////
////
////        // Set constraints for now and seven days from now
////        val now = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore")).timeInMillis
////        val sevenDaysFromNow = now + (6 * 24 * 60 * 60 * 1000) // 7 days in milliseconds
////        datePickerDialog.datePicker.minDate = now
////        datePickerDialog.datePicker.maxDate = sevenDaysFromNow
////        datePickerDialog.show()
//    }
//
//    private fun convertTimeToDate(selected: Long): String {
//        val utc = Calendar.getInstance(TimeZone.getTimeZone("Asia/Singapore"))
//        utc.timeInMillis = selected
//        return SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(utc.timeZone)
//    }
//
//    private fun setupStationDropDownMenu() {
//        val stations = resources.getStringArray(R.array.stations_list)
//        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.component_station_dropdown_item, stations)
//        dropDown = viewBinding.actvDropdownItem
//        dropDown.setAdapter(arrayAdapter)
//
//        dropDown.onItemClickListener =
//            AdapterView.OnItemClickListener { parent, _, position, _ ->
//                // This code will be executed when an item is selected
//                val selectedItem = parent.getItemAtPosition(position).toString()
//                // Update the TextView to display the selected item
//                Log.d("SELECTED", selectedItem)
//                loadDataFromFirestore(selectedItem)
//            }
//    }
//
//    private fun setupAvailableItems(){
//
//        // Set up RecyclerView with the new adapter
//        itemsAdapter = TransactionProductItemCardsAdapter(availableItems, object : TransactionProductItemCardsAdapter.OnItemClickListener {
//            override fun onItemClick(item: String) {
//                if (itemsAdapter.getSelectedItems().contains(item)) {
//                    itemsAdapter.getSelectedItems().remove(item)
//                } else {
//                    itemsAdapter.getSelectedItems().add(item)
//                }
//            }
//        })
//
//        viewBinding.rvAvailableItems.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
//        viewBinding.rvAvailableItems.adapter = itemsAdapter
//
//    }

}