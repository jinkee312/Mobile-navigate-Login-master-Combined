package com.example.android.navigation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_login.*

class LoginMain : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        button.setOnClickListener(){
            val intent2 = Intent(this,StaffLogin::class.java)
            startActivity(intent2)
        }

        button2.setOnClickListener(){
            val intent = Intent(this, StudentLogin::class.java)
            startActivity(intent)
        }


    }



}
