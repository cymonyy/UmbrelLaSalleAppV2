package com.mobdeve.s15.nadela.oliva.quizon.myapplication.databases

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class StockItemHelper2 {

    companion object {
        suspend fun getAvailableItems(station: String): MutableList<DocumentSnapshot> = withContext(
            Dispatchers.IO) {
            try {
                val db = FirebaseFirestore.getInstance()
                var querySnapshot =
                    db.collection("Stations")
                        .whereEqualTo("name", station)
                        .limit(1)
                        .get().await()

                if (querySnapshot.documents.isEmpty()) throw Exception()
                querySnapshot = db.collection("Stations")
                    .document(querySnapshot.documents.first().id)
                    .collection("Stock")
                    .whereGreaterThan("available", 0)
                    .get().await()

                if (querySnapshot.documents.isEmpty()) throw Exception()
                return@withContext querySnapshot.documents
            } catch (e: Exception) {
                // Handle exceptions
                return@withContext mutableListOf()
            }
        }

        suspend fun getAvailableItem(station: String, item: String): MutableList<DocumentSnapshot> = withContext(
            Dispatchers.IO) {
            try {
                val db = FirebaseFirestore.getInstance()
                var querySnapshot =
                    db.collection("Stations")
                        .whereEqualTo("name", station)
                        .limit(1)
                        .get().await()


                if (querySnapshot.documents.isEmpty()) throw Exception()
                val stationID = querySnapshot.documents.first().id

                querySnapshot =
                    db.collection("Items")
                        .whereEqualTo("station", stationID)
                        .whereEqualTo("itemCategory", item)
                        .whereEqualTo("requested", false)
                        .limit(1)
                        .get().await()

                if (querySnapshot.documents.isEmpty()) throw Exception()
                Log.d("querySnapshot", querySnapshot.documents.first().id)

                return@withContext querySnapshot.documents
            } catch (e: Exception) {
                // Handle exceptions
                return@withContext mutableListOf()
            }

        }


    }


}