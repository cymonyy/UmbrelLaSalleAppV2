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
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.adapters.StockItemAdapter
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.databases.StockItemHelper
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.databinding.AdminHomepageContainerBinding
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.models.StockItemModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ItemsInventoryActivity: AppCompatActivity() {

    private lateinit var binding: AdminHomepageContainerBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StockItemAdapter
    private lateinit var data: MutableList<StockItemModel>

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        this.binding = AdminHomepageContainerBinding.inflate(layoutInflater)
        setContentView(binding.root)


        recyclerView = this.binding.rvItemsInventory
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = StockItemAdapter(mutableListOf())
        recyclerView.adapter = adapter

        loadStockItems()

    }

    fun openStudentsPage(view: View?) {
        val intent = Intent(this, AdminStudentsTableActivity::class.java)
        startActivity(intent)
    }


    fun openTransactionsPage(view: View?) {
        val intent = Intent(this, AdminStudentsTransactionsActivity::class.java)
        startActivity(intent)
    }
    private fun loadStockItems(){
        lifecycleScope.launch(Dispatchers.Main) {
            try{
                data = mutableListOf()
                val documents = withContext(Dispatchers.IO){
                    StockItemHelper.getStockItems()
                }

                if (documents.isEmpty()) throw Exception("No items found")

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
            document.toObject<StockItemModel>()?.let { data.add(it) }
        }
    }

}