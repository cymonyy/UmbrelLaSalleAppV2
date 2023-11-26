package com.mobdeve.s15.nadela.oliva.quizon.myapplication.databases

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class TransactionHelper {
    companion object {
        suspend fun getTransaction(): MutableList<DocumentSnapshot> = withContext(
            Dispatchers.IO) {
            try {

                val db = FirebaseFirestore.getInstance()
                val querySnapshot =
                    db.collection("Transactions")
                        .where(Filter.or(
                            Filter.equalTo("status", "Approved"),
                            Filter.equalTo("status", "Returned")
                        ))
                        .get().await()

                return@withContext querySnapshot.documents
            } catch (e: Exception) {
                // Handle exceptions
                return@withContext mutableListOf()
            }
        }
    }

}