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
import com.google.firebase.Firebase
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.databinding.ForgotPasswordBinding
import java.util.regex.Matcher
import java.util.regex.Pattern

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var viewBinding: ForgotPasswordBinding

    private lateinit var etEmail: EditText

    private lateinit var pattern: Pattern
    private lateinit var matcher: Matcher
    private var errorOccured = false

    private lateinit var authProfile: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.viewBinding = ForgotPasswordBinding.inflate(layoutInflater)
        setContentView(this.viewBinding.root)

        this.etEmail = this.viewBinding.etEmail
        authProfile = Firebase.auth

        this.viewBinding.clBackButton.imageButton.setOnClickListener{
            finish()
        }

        this.viewBinding.btProceed.setOnClickListener{

            val email = this.etEmail.text.toString()

            //Email Errors
            pattern = Pattern.compile("[a-z](?:[a-z]+(_|\\.))+[a-z]+@dlsu.edu.ph") //DLSU email
            matcher = pattern.matcher(email)
            if(TextUtils.isEmpty(email)){
                triggerError(viewBinding.tvLoginEmailError, etEmail, resources.getStringArray(R.array.error_general_email_fields)[0])
            }
            else if(!matcher.matches()){
                triggerError(viewBinding.tvLoginEmailError, etEmail, resources.getStringArray(R.array.error_general_email_fields)[1])
            }

            if(!errorOccured){
                resetPassword(email)
            }
        }

        etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            @SuppressLint("ResourceAsColor", "PrivateResource")
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                cancelError(viewBinding.tvLoginEmailError, etEmail)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun resetPassword(email: String) {
        authProfile.sendPasswordResetEmail(email).addOnCompleteListener { task ->
            if(task.isSuccessful){
                Toast.makeText(this@ForgotPasswordActivity, "Please check your inbox for password reset link.", Toast.LENGTH_LONG).show()

                val intent = Intent(this@ForgotPasswordActivity, MainActivity::class.java)

                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            else{
                try{
                    throw task.exception!!
                } catch (e: FirebaseException){ //on error
                    triggerError(viewBinding.tvLoginEmailError, etEmail, resources.getString(R.string.error_login_email))

                    Log.e(TAG, e.message.toString())
                    Toast.makeText(this@ForgotPasswordActivity, "Error! " + e.message.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }

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


    companion object {
        const val TAG : String = "ForgetPasswordActivity"
    }


}