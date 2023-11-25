package com.mobdeve.s15.nadela.oliva.quizon.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.databinding.StudentCreateAccountBinding
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.models.UserModel
import java.util.regex.Matcher
import java.util.regex.Pattern

class StudentCreateAccountActivity : AppCompatActivity() {

    private lateinit var viewBinding: StudentCreateAccountBinding
    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etMobile: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPass: EditText

    private lateinit var pattern: Pattern
    private lateinit var matcher: Matcher
    private var errorOccured = false

    private var db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var userRef: CollectionReference = db.collection("Users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.viewBinding = StudentCreateAccountBinding.inflate(layoutInflater)
        setContentView(this.viewBinding.root)

        etFirstName = this.viewBinding.etFirstname
        etLastName = this.viewBinding.etLastname
        etEmail = this.viewBinding.etEmail
        etMobile = this.viewBinding.etPhonenum
        etPassword = this.viewBinding.etPassword
        etConfirmPass = this.viewBinding.etConfPassword


        this.viewBinding.clBackButton.imageButton.setOnClickListener(View.OnClickListener {
            finish()
        })

        this.viewBinding.btGoToLogin.setOnClickListener{
            val newIntent = Intent(this@StudentCreateAccountActivity, LoginActivity::class.java)
            startActivity(newIntent)
            finish()
        }

        this.viewBinding.btProceed.setOnClickListener(View.OnClickListener {
            val firstName = etFirstName.text.toString()
            val lastName = etLastName.text.toString()
            val email = etEmail.text.toString()
            val mobileNumber = etMobile.text.toString()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPass.text.toString()


            //First Name Errors
            if(TextUtils.isEmpty(firstName)){
                triggerError(viewBinding.tvFirstNameError, etFirstName, resources.getStringArray(R.array.error_register_name)[0])
            }

            //Last Name Errors
            if(TextUtils.isEmpty(lastName)){
                triggerError(viewBinding.tvLastNameError, etLastName, resources.getStringArray(R.array.error_register_name)[1])
            }

            //Email Errors
            pattern = Pattern.compile("[a-z](?:[a-z]+(_|\\.))+[a-z]+@dlsu.edu.ph") //DLSU email
            matcher = pattern.matcher(email)
            if(TextUtils.isEmpty(email)){
                triggerError(viewBinding.tvEmailError, etEmail, resources.getStringArray(R.array.error_general_email_fields)[0])
            }
            else if(!matcher.matches()){
                triggerError(viewBinding.tvEmailError, etEmail, resources.getStringArray(R.array.error_general_email_fields)[1])
            }

            //Mobile Number Errors
            Log.d("Mobile Number", mobileNumber)
            pattern = Pattern.compile("09[0-9]{9}") //Philippines number
            matcher = pattern.matcher(mobileNumber)
            if(TextUtils.isEmpty(mobileNumber)){
                triggerError(viewBinding.tvPhoneError, etMobile, resources.getStringArray(R.array.error_register_mobile)[0])
            }
            else if(!matcher.matches()){
                triggerError(viewBinding.tvPhoneError, etMobile, resources.getStringArray(R.array.error_register_mobile)[1])
            }

            //Password Errors
            if(TextUtils.isEmpty(password)){
                triggerError(viewBinding.tvPassError, etPassword, resources.getStringArray(R.array.error_general_pass_fields)[0])
            }
            else if(password.length <= 6){
                triggerError(viewBinding.tvPassError, etPassword, resources.getStringArray(R.array.error_register_password)[0])
            }

            //Confirm Password Errors
            if(TextUtils.isEmpty(confirmPassword)){
                triggerError(viewBinding.tvConfirmError, etConfirmPass, resources.getStringArray(R.array.error_register_confirm_password)[0])
            }
            else if(!TextUtils.equals(password, confirmPassword)){
                triggerError(viewBinding.tvConfirmError, etConfirmPass, resources.getStringArray(R.array.error_register_confirm_password)[1])
            }

            if(!errorOccured){
                registerStudent(lastName, firstName, email, mobileNumber, password)
            }
        })


        etFirstName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            @SuppressLint("ResourceAsColor", "PrivateResource")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                cancelError(viewBinding.tvFirstNameError, etFirstName)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        etLastName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            @SuppressLint("ResourceAsColor", "PrivateResource")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                cancelError(viewBinding.tvLastNameError, etLastName)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            @SuppressLint("ResourceAsColor", "PrivateResource")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                cancelError(viewBinding.tvEmailError, etEmail)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        etMobile.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            @SuppressLint("ResourceAsColor", "PrivateResource")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                cancelError(viewBinding.tvPhoneError, etMobile)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            @SuppressLint("ResourceAsColor", "PrivateResource")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                cancelError(viewBinding.tvPassError, etPassword)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        etConfirmPass.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            @SuppressLint("ResourceAsColor", "PrivateResource")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                cancelError(viewBinding.tvConfirmError, etConfirmPass)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun triggerError(textView: TextView, editText: EditText, message: String){
        textView.visibility = View.VISIBLE
        textView.text = message
        val parentCard : MaterialCardView =  editText.parent as MaterialCardView
        parentCard.strokeColor = Color.parseColor("#E31414")
        parentCard.strokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.5f, resources.displayMetrics ).toInt()
        this.errorOccured = true
    }

    private fun cancelError(textView: TextView, editText: EditText) {
        textView.visibility = View.GONE
        val parentCard : MaterialCardView =  editText.parent as MaterialCardView
        parentCard.strokeWidth = 0
        this.errorOccured = false
    }

    private fun registerStudent(lastName: String, firstName: String, email: String, mobileNumber: String, password: String){
        val auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = UserModel(lastName, firstName, email, mobileNumber)
                user.id = auth.currentUser?.uid.toString()

                userRef.document(user.id).set(user)
                    .addOnSuccessListener {
                        Toast.makeText(
                            this@StudentCreateAccountActivity,
                            "user created successfully",
                            Toast.LENGTH_LONG
                        ).show()

                        val newIntent = Intent(this@StudentCreateAccountActivity, LoginActivity::class.java)
                        startActivity(newIntent)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, e.message.toString())
                        Toast.makeText(
                            this@StudentCreateAccountActivity,
                            "Error!: " + e.message.toString(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
            else{
                try{
                    throw task.exception!!
                }catch (e: FirebaseAuthInvalidCredentialsException){
                    triggerError(viewBinding.tvEmailError, etEmail, resources.getStringArray(R.array.error_register_email)[0])

                } catch (e : FirebaseAuthUserCollisionException){
                    triggerError(viewBinding.tvEmailError, etEmail, resources.getStringArray(R.array.error_register_email)[1])
                } catch (e : Exception){
                    Log.e(Companion.TAG, e.message.toString())
                    Toast.makeText(this@StudentCreateAccountActivity, "Error! " + e.message.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }

    }

    companion object {
        const val TAG : String = "RegisterActivity"
    }


}