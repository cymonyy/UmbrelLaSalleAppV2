package com.mobdeve.s15.nadela.oliva.quizon.myapplication.databases

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class StationsHelper {
    suspend fun getOneStation(station: String): MutableList<DocumentSnapshot> = withContext(
        Dispatchers.IO) {
        try {
            val db = FirebaseFirestore.getInstance()
            val querySnapshot =
                db.collection("Stations")
                    .whereEqualTo("name", station)
                    .limit(1)
                    .get().await()

            if (querySnapshot.documents.isEmpty()) throw Exception()
            return@withContext querySnapshot.documents
        } catch (e: Exception) {
            // Handle exceptions
            return@withContext mutableListOf()
        }
    }
}