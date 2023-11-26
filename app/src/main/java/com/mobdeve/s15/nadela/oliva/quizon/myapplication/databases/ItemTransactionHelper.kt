package com.mobdeve.s15.nadela.oliva.quizon.myapplication.databases


import android.text.Editable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
                    request.id = request.id
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
            transactionId: String,
            transactionStatus: String,
            transactionNote: Editable,
            transactionActualDate: String,
            itemStatusMap: MutableMap<String, String>) : TransactionModel
        {
            val db = FirebaseFirestore.getInstance()

            val snapshot = db.collection("Transactions").document(transactionId).get().await()
            if (!snapshot.exists()) throw Exception("Service Error Detected!")

            val result = db.runTransaction { transaction ->

                // Update item details
                for ((itemId, status) in itemStatusMap) {
                    val itemRef = db.collection("Items").document(itemId)
                    val itemDoc = transaction.get(itemRef)

                    if (itemDoc.exists()) {
                        // Update borrowed, requested, and status
                        transaction.update(itemRef, "borrowed", true)
                        transaction.update(itemRef, "requested", true)
                        transaction.update(itemRef, "status", status)
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
                    throw Exception("Service Error Detected!")
                }
                .await()
            // Indicate whether to commit or rollback the transaction

            return result
        }
    }

}












