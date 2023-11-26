package com.mobdeve.s15.nadela.oliva.quizon.myapplication.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.R
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.adapters.ItemAdapter
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.databases.ItemTransactionHelper
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.databinding.AdminTransactionFormBinding
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.fragments.AddTransactionBottomSheetDialogFragment
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.fragments.EditTransactionBottomSheetDialogFragment
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.models.ItemModel
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.models.TransactionModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.TimeZone

class AdminTransactionFormFragment(private val transactionID: String) : BottomSheetDialogFragment() {
    private lateinit var binding: AdminTransactionFormBinding
    private lateinit var itemAdapter: ItemAdapter
    private lateinit var itemTransactionHelper: ItemTransactionHelper
    private lateinit var transaction: TransactionModel
    private var requestItemsWithStatus: MutableList<ItemModel> = mutableListOf()
    private var itemStatusMap: MutableMap<String, String> = mutableMapOf()
    private var mListener: BottomSheetListener? = null


    interface BottomSheetListener {
        fun onDataSent(requestMode: Boolean, transaction: TransactionModel)
    }

    fun setBottomSheetListener( listener: BottomSheetListener) {
        mListener = listener
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AdminTransactionFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize RecyclerView and Adapter
        // Retrieve the transactionID from arguments


        setupRecyclerView()
        loadStudentTransaction()

        // Observe changes in the item list
        itemTransactionHelper.data.observe(viewLifecycleOwner, Observer { newData ->
            transaction = newData
            binding.tvTransactionID.text = transactionID
            binding.tvDate.text = transaction.expectedReturnDate



            if (transaction.status != "Requested") {
                binding.tvFormLabel.text = "Return Form"
                binding.btnApprove.text = "RETURN"
            } else {
                binding.tvFormLabel.text = "Request Form"
                binding.btnApprove.text = "APPROVE"
                binding.btnApprove.setBackgroundResource(R.drawable.admin_request_form_approve_button)
            }

            loadRequestedItems()
        })

        //Update DB as per changes
        //if transaction.status == "Requested", if the Approve btn is clicked,
        //update the status of transaction.status to "Approved", else Rejected, change to "Rejected"
        //if transaction.status == "Approved", when the Approve btn is clicked,
        //update the status of transaction.status to "Returned", else Rejected

        binding.btnApprove.setOnClickListener {
            val newNote = binding.etNote.text

            updateTransaction(isApprove = true, newNote)
            //dialogue "r u sure"
            dismiss()

        }

        // Add click listener for the "Reject" button
        binding.btnReject.setOnClickListener {
            val newNote = binding.etNote.text
            updateTransaction(isApprove = false, newNote)
            //dialogue "r u sure"
            dismiss()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun updateTransaction(isApprove: Boolean, newNote: Editable) {
        GlobalScope.launch(Dispatchers.Main ) {

            // Set the time zone to Singapore
            val timeZone = TimeZone.getTimeZone("Asia/Singapore")
            val calendar = Calendar.getInstance(timeZone)


            val newTransactionStatus: String = if (transaction.status == "Requested") {
                // If the transaction status is "Requested", update it based on the button clicked
                if (isApprove) "Approved" else "Denied"
            } else {
                // If the transaction status is "Approved", update it based on the button clicked
                if (isApprove) "Returned" else "Denied"
            }

            val newData = TransactionModel(
                transaction.id,
                transaction.borrower,
                transaction.station,
                newTransactionStatus,
                transaction.transactionDate,
                transaction.expectedReturnDate,
                "${calendar.get(Calendar.DAY_OF_MONTH)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.YEAR)}",
                transaction.requestedItems,
                transaction.requestNote,
                newNote.toString()
            )

            // Update transaction status in Firebase
//            mListener?.onDataSent(
//                transaction.status == "Requested",
//                ItemTransactionHelper.updateTransaction(
//                    transactionID,
//                    newTransactionStatus,
//                    newNote,
//                    "${calendar.get(Calendar.DAY_OF_MONTH)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.YEAR)}",
//                    itemStatusMap
//                )
//            )
            mListener?.onDataSent(transaction.status == "Requested", newData)

        }

    }

    private fun setupRecyclerView() {
        binding.rvRequest.layoutManager = LinearLayoutManager(requireContext())
        itemAdapter = ItemAdapter(mutableListOf())
        itemAdapter.setOnItemSelectedListener(object : ItemAdapter.OnItemSelectedListener{
            override fun onItemsSelected(itemID: String, newStatus: String) {
                itemStatusMap[itemID] = newStatus
            }

        })
        binding.rvRequest.adapter = itemAdapter

    }

    private fun loadStudentTransaction() {
        // Initialize ItemTransactionHelper
        itemTransactionHelper = ItemTransactionHelper()

        // Load transaction data of student for forms
        itemTransactionHelper.getTransactionForAdminForms(transactionID)
    }

    private fun loadRequestedItems() {
        lifecycleScope.launch(Dispatchers.Main) {
            try {
                requestItemsWithStatus = mutableListOf()

                for (itemID in transaction.requestedItems.values) {
                    Log.d("ITEMTRANSACTION", itemID)
                    val item = withContext(Dispatchers.IO) {
                        ItemTransactionHelper.getItemWithStatus(itemID)
                    }

                    if (item != null) {
                        Log.d("ITEMTRANSACTION", item.id)
                        requestItemsWithStatus.add(item)
                    }
                }

                itemAdapter.updateItems(requestItemsWithStatus)

            } catch (e: Exception) {
                // Handle exceptions if necessary
            }
        }
    }
}