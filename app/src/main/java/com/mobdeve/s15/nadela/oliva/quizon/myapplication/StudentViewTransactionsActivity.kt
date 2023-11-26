package com.mobdeve.s15.nadela.oliva.quizon.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.adapters.ListOfBorrowedTransactionsAdapter
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.databases.TransactionsHelper
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.databinding.StudentListOfBorrowedTransactionsBinding
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.fragments.AddTransactionBottomSheetDialogFragment
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.fragments.QRScannerBottomFragment
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.models.TransactionModel


class StudentViewTransactionsActivity: AppCompatActivity(),  ListOfBorrowedTransactionsAdapter.ScrollToPositionCallback {

    private lateinit var viewBinding: StudentListOfBorrowedTransactionsBinding
    private var transactions: MutableList<TransactionModel> = mutableListOf()
    private lateinit var listAdapter: ListOfBorrowedTransactionsAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

    private var transactionsHelper = TransactionsHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.viewBinding = StudentListOfBorrowedTransactionsBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        // Initialize RecyclerView and Adapter
        this.recyclerView = viewBinding.rvTransactions
        recyclerView.layoutManager = LinearLayoutManager(this)

        listAdapter = ListOfBorrowedTransactionsAdapter(mutableListOf()) // Initialize with empty list
        recyclerView.adapter = listAdapter

        // Set the callback in the adapter
        listAdapter.setScrollToPositionCallback(this@StudentViewTransactionsActivity);


        viewBinding.ibAddButton.setOnClickListener {
            showBottomSheetDialog()

        }

        progressBar = viewBinding.progressBar

        transactionsHelper.data.observe(this, Observer { newData ->
            run {
                transactions = newData
                listAdapter.updateData(transactions)
            }
        })

        // Load data from Firestore
        loadStudentTransactions()

    }


    override fun onScrollToPosition(position: Int) {
        recyclerView.smoothScrollToPosition(position)
    }


    private fun showBottomSheetDialog() {
        // Use BottomSheetFragment with view binding
        val bottomSheetFragment = AddTransactionBottomSheetDialogFragment(intent.getStringExtra("userID").toString())
        bottomSheetFragment.setBottomSheetListener(object : AddTransactionBottomSheetDialogFragment.BottomSheetListener {
            override fun onDataSent(transaction: TransactionModel) {
                listAdapter.addData(transaction)
                recyclerView.smoothScrollToPosition(listAdapter.itemCount - 1)
            }
        })
        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
    }


    private fun loadStudentTransactions(){
        progressBar.visibility = View.VISIBLE
        val user = intent.getStringExtra("userID").toString()
        transactionsHelper.getStudentTransactions(user)
        progressBar.visibility = View.GONE
    }
}