
package com.mobdeve.s15.nadela.oliva.quizon.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.databinding.AdminCreateAccErrorBinding
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.databinding.ChooseUserBinding


class AdminCreateAccountActivity : AppCompatActivity(){

    private lateinit var viewBinding: AdminCreateAccErrorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.viewBinding = AdminCreateAccErrorBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)


        this.viewBinding.clBackButton.imageButton.setOnClickListener(View.OnClickListener {
            finish()
        })

        this.viewBinding.llProceedLoginButton.setOnClickListener {
            val newIntent = Intent(this@AdminCreateAccountActivity, LoginActivity::class.java)
            startActivity(newIntent)
            finish()
        }

    }



}