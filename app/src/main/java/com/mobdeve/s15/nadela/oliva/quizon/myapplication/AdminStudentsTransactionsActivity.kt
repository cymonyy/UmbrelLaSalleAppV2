package com.mobdeve.s15.nadela.oliva.quizon.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.toObject

import com.mobdeve.s15.nadela.oliva.quizon.myapplication.adapters.TransactionsAdapter
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.databases.TransactionHelper
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.databinding.AdminTransactionsListBinding
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.fragments.AdminTransactionFormFragment
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.fragments.QRScannerFragment
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.models.TransactionModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AdminStudentsTransactionsActivity : AppCompatActivity() {


    private lateinit var binding: AdminTransactionsListBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionsAdapter
    private lateinit var data: MutableList<TransactionModel>

//    companion object{
//        val REQUIRED_PERMISSIONS = arrayOf<String>("CAMERA")
//    }
//
//    private val activityResultLauncher =
//        registerForActivityResult(
//            ActivityResultContracts.RequestMultiplePermissions())
//        { permissions ->
//            // Handle Permission granted/rejected
//            var permissionGranted = true
//            permissions.entries.forEach {
//                if (it.key in REQUIRED_PERMISSIONS && !it.value)
//                    permissionGranted = false
//            }
//            if (!permissionGranted) {
//                Toast.makeText(baseContext,
//                    "Permission request denied",
//                    Toast.LENGTH_SHORT).show()
//            } else {
//                startCamera()
//            }
//        }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        this.binding = AdminTransactionsListBinding.inflate(layoutInflater)
        setContentView(binding.root)


        recyclerView = this.binding.rvTransactions
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = TransactionsAdapter(mutableListOf())
        recyclerView.adapter = adapter

        loadTransactions()

        binding.btnScanTransaction.setOnClickListener {
            showCameraScannerDialog()
        }

    }

//    private fun startCamera() {
//        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
//
//        cameraProviderFuture.addListener({
//            // Used to bind the lifecycle of cameras to the lifecycle owner
//            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
//
//            // Preview
//            val preview = Preview.Builder()
//                .build()
//                .also {
//                    it.setSurfaceProvider(binding.cameraView.surfaceProvider)
//                }
//
//            // Select back camera as a default
//            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
//
//            try {
//                // Unbind use cases before rebinding
//                cameraProvider.unbindAll()
//
//                // Bind use cases to camera
//                cameraProvider.bindToLifecycle(
//                    this, cameraSelector, preview)
//
//            } catch(exc: Exception) {
//                Log.e("USECASEFAILURE", "Use case binding failed", exc)
//            }
//
//        }, ContextCompat.getMainExecutor(this))
//    }
//
//    private fun requestPermissions() {
//        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
//    }

//    override fun onSaveInstanceState(savedInstanceState: Bundle) {
//        savedInstanceState.putBoolean(KEY_ALLOW_MANUAL_INPUT, allowManualInput)
//        savedInstanceState.putBoolean(KEY_ENABLE_AUTO_ZOOM, enableAutoZoom)
//        super.onSaveInstanceState(savedInstanceState)
//    }

//    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
//        super.onRestoreInstanceState(savedInstanceState)
//        allowManualInput = savedInstanceState.getBoolean(KEY_ALLOW_MANUAL_INPUT)
//        enableAutoZoom = savedInstanceState.getBoolean(KEY_ENABLE_AUTO_ZOOM)
//    }




//    private fun codeScanner() {
//
//        val options = GmsBarcodeScannerOptions.Builder()
//            .setBarcodeFormats(
//                Barcode.FORMAT_QR_CODE,
//                Barcode.FORMAT_AZTEC)
//            .build()
//
//
//        val gmsBarcodeScanner = GmsBarcodeScanning.getClient(this@AdminStudentsTransactionsActivity, options)
//
//        gmsBarcodeScanner
//            .startScan()
//            .addOnSuccessListener { barcode ->
//                // Task completed successfully
//                val transactionID = barcode.rawValue
//
////                if (transactionID != null) {
////                    launchAdminTransactionFormFragment(transactionID)
////                }
//                Toast.makeText(this@AdminStudentsTransactionsActivity, "SCANNING SUCCESS", Toast.LENGTH_LONG).show()
//
//            }
//            .addOnCanceledListener {
//                // Task canceled
//                Toast.makeText(this@AdminStudentsTransactionsActivity, "SCANNING FAILED", Toast.LENGTH_LONG).show()
//
//            }
//            .addOnFailureListener { e ->
//
//                Log.d("QR FAIL", e.stackTraceToString())
//                // Task failed with an exception
//                Toast.makeText(this@AdminStudentsTransactionsActivity, e.message, Toast.LENGTH_LONG).show()
//
//
//            }
//    }

    private fun launchAdminTransactionFormFragment(transactionID: String) {
        val fragment = AdminTransactionFormFragment(transactionID)
        fragment.setBottomSheetListener(object : AdminTransactionFormFragment.BottomSheetListener{
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
        fragment.show(supportFragmentManager, fragment.tag)
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
    }

    fun openItemsInventory(view: View?) {
        val intent = Intent(this, ItemsInventoryActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun openStudentsPage(view: View?) {
        val intent = Intent(this, AdminStudentsTableActivity::class.java)
        startActivity(intent)
        finish()
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