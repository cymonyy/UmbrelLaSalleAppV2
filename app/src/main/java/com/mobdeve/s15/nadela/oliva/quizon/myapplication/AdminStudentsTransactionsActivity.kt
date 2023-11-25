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
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.adapters.TransactionsAdapter
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.databases.TransactionHelper
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.databinding.AdminTransactionsListBinding
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

        loadTransactions()

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