package com.mobdeve.s15.nadela.oliva.quizon.myapplication.databases


import android.text.Editable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.toObject
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.models.ItemModel
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.models.TransactionModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class ItemTransactionHelper {

    private val _data = MutableLiveData<TransactionModel>()
    val data: LiveData<TransactionModel>
        get() = _data

    @OptIn(DelicateCoroutinesApi::class)
    fun getTransactionForAdminForms(transactionID: String) {
        GlobalScope.launch(Dispatchers.Main) {
            try {

                withContext(Dispatchers.IO) {
                    val db = FirebaseFirestore.getInstance()

                    val document =
                        db.collection("Transactions").document(transactionID).get().await()
                    val transaction: TransactionModel? = document.toObject<TransactionModel>()

                    if (transaction != null) {
                        transaction.id = transactionID
                        _data.postValue(transaction)
                    }
                }
            } catch (e: FirebaseFirestoreException) {
                // Handle Firestore-specific exceptions
                return@launch
            }
        }
    }


    companion object {
        suspend fun getItemWithStatus(item: String): ItemModel? = withContext(
            Dispatchers.IO
        ) {
            try {
                val db = FirebaseFirestore.getInstance()

                val request = db.collection("Items").document(item).get().await().toObject<ItemModel>()
                Log.d("REQUESTDOCUMENT", request.toString())

                if (request != null) {
                    Log.d("REQUESTITEM", request.itemCategory)
                    request.id = item
                }

                return@withContext request
            } catch (e: Exception) {
                // Handle exceptions
                return@withContext null
            }

        }
        @Suppress("UNCHECKED_CAST")
        @Throws(Exception::class)
        suspend fun updateTransaction(
            requestMode: Boolean,
            transactionStation: String,
            transactionId: String,
            transactionStatus: String,
            transactionNote: Editable,
            transactionActualDate: String,
            requestItems: MutableSet<String>,
            itemStatusMap: MutableMap<String, String>) : TransactionModel
        {
            val db = FirebaseFirestore.getInstance()

            var querySnapshot =
                db.collection("Stations")
                    .whereEqualTo("name", transactionStation)
                    .limit(1)
                    .get().await()

            val stationID = querySnapshot.documents.first().id
            val references: MutableSet<DocumentReference> = mutableSetOf()
            for (item in requestItems){
                querySnapshot = db.collection("Stations")
                    .document(stationID)
                    .collection("Stock")
                    .whereEqualTo("itemCategory", item)
                    .limit(1)
                    .get()
                    .await()

                references.add(querySnapshot.documents.first().reference)
            }

            val snapshot = db.collection("Transactions").document(transactionId).get().await()
            if (!snapshot.exists()) {
                Log.d("ERRORUPDATE", "No snapshot")
                throw Exception("Service Error Detected!")
            }

            val result = db.runTransaction { transaction ->

                // Update item details
                when(transactionStatus) {
                    "Approved" -> {
                        for ((itemId, status) in itemStatusMap) {
                            transaction.update(db.collection("Items").document(itemId), "borrowed", true)
                            transaction.update(db.collection("Items").document(itemId), "requested", true)
                            transaction.update(db.collection("Items").document(itemId), "status", status)
                        }

                        //increase borrowed, decrease available
                        for (reference in references){
                            reference.update("borrowedCount", FieldValue.increment(1))
                            reference.update("available", FieldValue.increment(-1))
                        }
                    }

                    "Returned" -> {
                        for ((itemId, status) in itemStatusMap) {
                            transaction.update(db.collection("Items").document(itemId), "borrowed", false)
                            transaction.update(db.collection("Items").document(itemId), "requested", false)
                            transaction.update(db.collection("Items").document(itemId), "status", status)
                        }

                        //decrease borrowed, increase available
                        for (reference in references){
                            reference.update("borrowedCount", FieldValue.increment(-1))
                            reference.update("available", FieldValue.increment(1))
                        }
                    }

                    "Denied" -> {
                        for ((itemId, status) in itemStatusMap) {
                            transaction.update(db.collection("Items").document(itemId), "borrowed", false)
                            transaction.update(db.collection("Items").document(itemId), "requested", false)
                            transaction.update(db.collection("Items").document(itemId), "status", status)
                        }

                        if (!requestMode){
                            //decrease borrowed, increase available
                            for (reference in references){
                                reference.update("borrowedCount", FieldValue.increment(-1))
                                reference.update("available", FieldValue.increment(1))
                            }
                        }
                    }
                }



                val newData = TransactionModel(
                    snapshot.id,
                    snapshot.getString("borrower")!!,
                    snapshot.getString("station")!!,
                    transactionStatus,
                    snapshot.getString("transactionDate")!!,
                    snapshot.getString("expectedReturnDate")!!,
                    transactionActualDate,
                    snapshot.get("requestedItems") as MutableMap<String, String>,
                    snapshot.getString("requestNote")!!,
                    transactionNote.toString()
                )

                transaction.set(db.collection("Transactions").document(transactionId), newData)

                newData

//                // Get the transaction document
//                val transactionRef = db.collection("Transactions").document(transactionId)
//
//
//                transaction.update(transactionRef, "status", transactionStatus)
//                transaction.update(transactionRef, "returnNote", transactionNote.toString())
//                //transaction.update(transactionRef, "actualReturnDate", transactionActualDate) IMPLEMENT IN MERGER

            }
                .addOnFailureListener {
                    Log.e("ERRORUPDATE", it.stackTraceToString())
                    throw Exception("Service Error Detected!")
                }
                .await()
            // Indicate whether to commit or rollback the transaction

            return result
        }
    }

}












