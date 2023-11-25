package com.mobdeve.s15.nadela.oliva.quizon.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.toObject
import com.mobdeve.qrscannertemp.activity.QRScannerFragment
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.adapters.TransactionsAdapter
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.databases.TransactionHelper
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.databinding.AdminTransactionsListBinding
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.fragments.AddTransactionBottomSheetDialogFragment
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.models.TransactionModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AdminStudentsTransactionsActivity : AppCompatActivity() {

    private lateinit var binding: AdminTransactionsListBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionsAdapter
    private lateinit var data: MutableList<TransactionModel>

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        this.binding = AdminTransactionsListBinding.inflate(layoutInflater)
        setContentView(binding.root)


        recyclerView = this.binding.rvTransactions
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = TransactionsAdapter(mutableListOf())
        recyclerView.adapter = adapter

        binding.scanTransaction.setOnClickListener {
            showCameraScannerDialog()

        }

        loadTransactions()

    }

    private fun showCameraScannerDialog() {
        // Use BottomSheetFragment with view binding
        val bottomSheetFragment = QRScannerFragment()
        bottomSheetFragment.setBottomSheetListener(object: QRScannerFragment.BottomSheetListener{
            override fun onDataSent(requestMode: Boolean, transaction: TransactionModel) {
//                if (requestMode){
//                    adapter.addData(transaction)
//                    recyclerView.smoothScrollToPosition(data.size - 1)
//                }
//                else {
//                    val newlyAddedPosition = data.indexOf(data.first { it.id == transaction.id })
//                    adapter.updateTransactionItem(newlyAddedPosition, transaction)
//                    recyclerView.smoothScrollToPosition(newlyAddedPosition)
//                }
                Log.d("ADMINTRANSACTIONREQUEST", requestMode.toString())
                Log.d("ADMINTRANSACTIONITEM", transaction.id)
                Log.d("ADMINTRANSACTIONITEM", transaction.borrower)
                Log.d("ADMINTRANSACTIONITEM", transaction.status)
                Log.d("ADMINTRANSACTIONITEM", transaction.station)
                Log.d("ADMINTRANSACTIONITEM", transaction.transactionDate)
                Log.d("ADMINTRANSACTIONITEM", transaction.actualReturnDate)
                Log.d("ADMINTRANSACTIONITEM", transaction.expectedReturnDate)
                Log.d("ADMINTRANSACTIONITEM", transaction.requestedItems.toString())
                Log.d("ADMINTRANSACTIONITEM", transaction.requestNote)
                Log.d("ADMINTRANSACTIONITEM", transaction.returnNote)
            }

        })
        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        
//        val bottomSheetFragment = AddTransactionBottomSheetDialogFragment(intent.getStringExtra("userID").toString())
//        bottomSheetFragment.setBottomSheetListener(object : AddTransactionBottomSheetDialogFragment.BottomSheetListener {
//            override fun onDataSent(transaction: TransactionModel) {
//                listAdapter.addData(transaction)
//                recyclerView.smoothScrollToPosition(listAdapter.itemCount - 1)
//            }
//        })
//        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
    }

    fun openItemsInventory(view: View?) {
        val intent = Intent(this, ItemsInventoryActivity::class.java)
        startActivity(intent)

    }

    fun openStudentsPage(view: View?) {
        val intent = Intent(this, AdminStudentsTableActivity::class.java)
        startActivity(intent)
    }

    private fun loadTransactions(){
        lifecycleScope.launch(Dispatchers.Main) {
            try{
                data = mutableListOf()
                val documents = withContext(Dispatchers.IO){
                    TransactionHelper.getTransaction()
                }

                if (documents.isEmpty()) throw Exception("No transactions found")

                processData(documents)
                adapter.updateData(data)

            } catch (e: Exception){
                Log.e("EXCEPTION", e.message.toString())
            }
        }
    }

    private fun processData(documents: List<DocumentSnapshot>){
        // Handle the data on the main thread
        for(document in  documents){
            val transaction = document.toObject<TransactionModel>()
            if (transaction != null) {
                transaction.id=document.id
                data.add(transaction)
            }
        }
    }



}