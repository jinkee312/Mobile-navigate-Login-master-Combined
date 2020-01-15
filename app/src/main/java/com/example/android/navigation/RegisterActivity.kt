package com.example.android.navigation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_register_main.*

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_main)

        button5.setOnClickListener(){
            val intent2 = Intent(this,StaffRegister::class.java)
            startActivity(intent2)
        }

        button6.setOnClickListener(){
            val intent = Intent(this, StudentRegister::class.java)
            startActivity(intent)
        }


    }
}
