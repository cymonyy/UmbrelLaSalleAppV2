package com.mobdeve.s15.nadela.oliva.quizon.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mobdeve.s15.nadela.oliva.quizon.myapplication.databinding.ChooseUserBinding

class ChooseUserActivity : AppCompatActivity() {

    private lateinit var viewBinding: ChooseUserBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.viewBinding = ChooseUserBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        this.viewBinding.cvAdmin.setOnClickListener{
            val newIntent = Intent(this@ChooseUserActivity, AdminCreateAccountActivity::class.java)
            startActivity(newIntent)
            finish()
        }

        this.viewBinding.cvStudent.setOnClickListener{
            val newIntent = Intent(this@ChooseUserActivity, StudentCreateAccountActivity::class.java)
            startActivity(newIntent)
            finish()
        }

        this.viewBinding.clBackButton.imageButton.setOnClickListener(View.OnClickListener {
            finish()
        })

    }

}