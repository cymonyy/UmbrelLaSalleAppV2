package com.mobdeve.s15.nadela.oliva.quizon.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginTop
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.toObject
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.databases.UserDataHelper
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.databinding.AdminStudentsTableBinding
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.models.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AdminStudentsTableActivity : AppCompatActivity(){

    private lateinit var binding: AdminStudentsTableBinding

    private var data: MutableList<UserModel> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = AdminStudentsTableBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val tvStudentsLabel: TextView = findViewById(R.id.tvStudentsLabel)

        fetchUsersDataFromFirebase()
    }

    fun openItemsInventory(view: View?) {
        val intent = Intent(this, ItemsInventoryActivity::class.java)
        startActivity(intent)
    }

    fun openTransactionsPage(view: View?) {
        val intent = Intent(this, AdminStudentsTransactionsActivity::class.java)
        startActivity(intent)
    }

//    private fun underlineText(textView: TextView){
//        val content = SpannableString(textView.text)
//        content.setSpan(UnderlineSpan(), 0, content.length, 0)
//        textView.text = content
//    }

    private fun fetchUsersDataFromFirebase() {
        lifecycleScope.launch(Dispatchers.Main) {
            try {
                data = mutableListOf()
                val documents = withContext(Dispatchers.IO) {
                    UserDataHelper.getUserData()
                }

                if (documents.isNotEmpty()) {
                    processData(documents)
                    createTable()

                    Log.d("DATA_ACQUIRED", "Data acquired from Firestore: $data")
                } else {
                    Log.e("EXCEPTION", "No user data found")
                }

            } catch (e: Exception) {
                Log.e("EXCEPTION", e.message.toString())
            }
        }
    }

    private fun processData(documents: List<DocumentSnapshot>) {
        for (document in documents) {
            document.toObject<UserModel>()?.let { data.add(it) }
        }
    }

    private fun addCell(tableRow: TableRow, text: String, columnIndex: Int) {
        val cell = TextView(this)
        cell.text = text
        cell.setBackgroundResource(R.drawable.general_table_border)
        cell.setTextColor(resources.getColor(R.color.black, null))
        cell.setPadding(10, 10, 10, 10)

        cell.textAlignment = View.TEXT_ALIGNMENT_CENTER

        tableRow.addView(cell)
    }

    @SuppressLint("SetTextI18n")
    private fun createTable() {
        for (user in data) {

            if(!user.isAdmin){
                val tableRow = TableRow(this)

                val params = TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                )
                tableRow.layoutParams = params
                tableRow.gravity = Gravity.CENTER

                addCell(tableRow, user.id, 0)
                addCell(tableRow, "${user.firstName} ${user.lastName}", 1)

                binding.tlAdminStudentsTable.addView(tableRow)
            }
        }
    }





}