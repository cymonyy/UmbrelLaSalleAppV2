package com.mobdeve.s15.nadela.oliva.quizon.myapplication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.toObject
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.adapters.TransactionsAdapter
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.databases.TransactionHelper
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.databinding.AdminTransactionsListBinding
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.fragments.AddTransactionBottomSheetDialogFragment
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.fragments.QRScannerBottomFragment
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

        binding.scanTransaction.setOnClickListener {
            requestPermission()
        }

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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                scanCode()

                showBottomSheetDialog()

            } else {
                // Camera permission denied, handle accordingly
                Toast.makeText(
                    this@AdminStudentsTransactionsActivity,
                    "Camera permission is required to use the QR scanner",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun requestPermission() {
        if (ContextCompat.checkSelfPermission(
                this@AdminStudentsTransactionsActivity,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestCameraPermission()
        } else {
//            showBottomSheetDialog()
            showBottomSheetDialog()
//            scanCode()
        }
    }


    private fun showBottomSheetDialog() {
        // Use BottomSheetFragment with view binding
        val bottomSheetFragment = QRScannerBottomFragment()
        bottomSheetFragment.setBottomSheetListener(object: QRScannerBottomFragment.BottomSheetListener {
            override fun onDataSent(transaction: TransactionModel) {
            }
        })
        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
    }

    private fun scanCode() {

        Log.d("OPENSCAN", "HERE")
        val options: ScanOptions = ScanOptions()

        options.setPrompt("Volume up to flash on")
        options.setBeepEnabled(true)
        options.setOrientationLocked(true)
        options.captureActivity = QRScannerBottomFragment::class.java

        barcodeLauncher.launch(options)
    }

    private final val barcodeLauncher = registerForActivityResult(ScanContract())
        { result: ScanIntentResult ->
            if (result.contents == null) {
                Log.d("SCANNED", result.contents)

    //                    Toast.makeText(this@MyActivity, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(
                    this@AdminStudentsTransactionsActivity,
                    "Scanned: " + result.contents,
                    Toast.LENGTH_LONG
                ).show()
            }
        }



    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this@AdminStudentsTransactionsActivity,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST
        )
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST = 100
    }



}