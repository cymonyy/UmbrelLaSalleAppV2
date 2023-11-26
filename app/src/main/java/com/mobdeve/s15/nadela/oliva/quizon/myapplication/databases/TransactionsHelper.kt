package com.mobdeve.s15.nadela.oliva.quizon.myapplication.databases

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.models.TransactionModel
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class TransactionsHelper {

    private val _data = MutableLiveData<MutableList<TransactionModel>>()

    val data: LiveData<MutableList<TransactionModel>>
        get() = _data


    @OptIn(DelicateCoroutinesApi::class)
    fun getStudentTransactions(userID: String){
        GlobalScope.launch(Dispatchers.Main) {
            try {

                val documents = withContext(Dispatchers.IO) {
                    fetchData(userID)
                }

                if (documents.isEmpty()) throw Exception("No transactions found")
                processData(documents)

            } catch (e: Exception) {
                // Handle exceptions
                Log.e("EXCEPTION", e.message.toString())
            }
        }
    }




    private suspend fun fetchData(userID: String): MutableList<DocumentSnapshot> = withContext(Dispatchers.IO) {
        try {
            val db = FirebaseFirestore.getInstance()
            val querySnapshot = db.collection("Transactions").whereEqualTo("borrower", userID).get().await()
            return@withContext querySnapshot.documents
        } catch (e: Exception) {
            // Handle exceptions
            return@withContext mutableListOf()
        }
    }

    private fun processData(documents: List<DocumentSnapshot>){

        val data : MutableList<TransactionModel> = mutableListOf()

        // Handle the data on the main thread
        for(document in  documents){
            Log.d("document", document.id)

            val new = document.toObject<TransactionModel>()
            if (new != null) {
                new.id = document.id
                data.add(new)
            }
            Log.d("document", data.last().id)
        }

        _data.postValue(data)
    }

    companion object {


        //        suspend fun updateStudentTransaction(id: String, date: String, requested: MutableSet<String>, unselected: MutableSet<String>): TransactionModel {
//            val db = FirebaseFirestore.getInstance()
//
//            val result = db.runTransaction { transaction ->
//
//                //get the transaction
//
//                // per requested itemID, update the item.reque
//
//
//
//            }
//
//
//        }

        @Throws(Exception::class)
        suspend fun deleteStudentTransaction(transaction: TransactionModel){
            val db = FirebaseFirestore.getInstance()

            var querySnapshot =
                db.collection("Stations")
                    .whereEqualTo("name", transaction.station)
                    .limit(1)
                    .get().await()

            val stationID = querySnapshot.documents.first().id
            val references: MutableSet<DocumentReference> = mutableSetOf()
            for (item in transaction.requestedItems.keys){
                querySnapshot = db.collection("Stations")
                    .document(stationID)
                    .collection("Stock")
                    .whereEqualTo("itemCategory", item)
                    .limit(1)
                    .get()
                    .await()

                references.add(querySnapshot.documents.first().reference)
            }

            db.runTransaction { runner ->

                //make each item in requests not borrowed and not requested
                for (itemID in transaction.requestedItems.values){
                    runner.update(db.collection("Items").document(itemID), "borrowed", false)
                    runner.update(db.collection("Items").document(itemID), "requested", false)
                }

                //  DELETE the transaction
                runner.delete(db.collection("Transactions").document(transaction.id))

            }
            .addOnFailureListener {
                throw Exception("Service Error Detected!")
            }
            .await()
        }


        @Throws(Exception::class)
        suspend fun updateStudentTransaction(
            id: String,
            newExpectedDate: String,
            requestNote: String,
            unselected: MutableMap<String, String>,
            newRequests: MutableMap<String, String>) : TransactionModel
        {
            Log.d("UPDATEID", id)

            val db = FirebaseFirestore.getInstance()

            val snapshot = db.collection("Transactions").document(id).get().await()
            if (!snapshot.exists()) throw Exception("Service Error Detected!")


            val result = db.runTransaction { transaction ->

                //update document(unselected).requested = false
                for (item in unselected.values){
                    transaction.update(db.collection("Items").document(item), "requested", false)
                }

                //update document(newRequests).requested = true
                for (item in newRequests.values){
                    transaction.update(db.collection("Items").document(item), "requested", true)
                }

                val newData = TransactionModel(
                    id,
                    snapshot.getString("borrower")!!,
                    snapshot.getString("station")!!,
                    snapshot.getString("status")!!,
                    snapshot.getString("transactionDate")!!,
                    newExpectedDate,
                    snapshot.getString("actualReturnDate")!!,
                    newRequests,
                    requestNote,
                    snapshot.getString("returnNote")!!
                )

                //add the data to the firestore
                transaction.set(db.collection("Transactions").document(id), newData)

                newData
            }
                .addOnFailureListener {
                    throw Exception("Service Error Detected!")
                }
                .await()

            return result
        }

        @Throws(Exception::class)
        suspend fun addStudentTransaction(data: TransactionModel): TransactionModel {
            val db = FirebaseFirestore.getInstance()

            val result = db.runTransaction { transaction ->
                //update document(requestItems).requested = true
                for (item in data.requestedItems.values){
                    transaction.update(db.collection("Items").document(item), "requested", true)
                }

                //create a new document under transactions
                val newEntry = db.collection("Transactions").document()

                //update transaction.id to added document.id
                data.id = newEntry.id

                //add the data to the firestore
                transaction.set(newEntry, data)

                //return transaction
                data
            }
                .addOnFailureListener {
                    throw Exception("Service Error Detected!")
                }
                .await()

            return result
        }
    }







    /*
    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var  transactions: MutableList<TransactionModel>
    fun getStudentTransactions(userID: String): MutableList<TransactionModel>{
        transactions =  mutableListOf()

        db.collection("Transactions").
            whereEqualTo("borrower", userID).get()
            .addOnSuccessListener {documents ->
                for(document in  documents){
                    Log.d("document", document.id)
                    transactions.add(document.toObject<TransactionModel>())
                    Log.d("document", transactions.last().toString())
                }

                Log.d("size of transaction on success", transactions.size.toString())
            }
            .addOnFailureListener{e ->
                Log.e("ERROR GETTING DATA", e.message.toString())
            }

        Log.d("size of transaction", transactions.size.toString())

        return transactions
    }

    */
}