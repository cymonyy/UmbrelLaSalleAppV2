package com.mobdeve.s15.nadela.oliva.quizon.myapplication.databases

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UserDataHelper {
    companion object {
        suspend fun getUserData(): MutableList<DocumentSnapshot> = withContext(
            Dispatchers.IO) {
            try {

                val db = FirebaseFirestore.getInstance()
                val querySnapshot =
                    db.collection("Users").get().await()

                return@withContext querySnapshot.documents
            } catch (e: Exception) {
                // Handle exceptions
                return@withContext mutableListOf()
            }
        }
    }


}