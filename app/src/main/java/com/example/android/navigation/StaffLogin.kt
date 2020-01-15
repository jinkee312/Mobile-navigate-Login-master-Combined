@file:Suppress("DEPRECATION")

package com.example.android.navigation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.ProgressDialog
import android.content.Intent
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.android.navigation.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.login_activity.*

class StaffLogin : AppCompatActivity() {

    private val TAG = "StaffLogin"
    //global variables
    private var email: String? = null
    private var password: String? = null
    //UI elements
    private var emailA: EditText? = null
    private var passwordA: EditText? = null
    private var btnLogin: Button? = null
    private var mProgressBar: ProgressDialog? = null
    //Firebase references
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        register.setOnClickListener(){
            val intent2 = Intent(this,StaffRegister::class.java)
            startActivity(intent2)
        }

        initialise()
    }

    private fun initialise() {
        emailA = findViewById<View>(R.id.email) as EditText
        passwordA = findViewById<View>(R.id.password) as EditText
        btnLogin = this.findViewById<View>(R.id.signInBtn) as Button
        mProgressBar = ProgressDialog(this)
        mAuth = FirebaseAuth.getInstance()

        btnLogin!!.setOnClickListener {
            loginUser()
        }
    }

    private fun loginUser() {
        email = emailA?.text.toString()
        password = this.passwordA?.text.toString()
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        var userref = FirebaseDatabase.getInstance().getReference("Users").child(uid)
        var user = User()
        userref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@StaffLogin, "Error Encounter Due to " + databaseError.message, Toast.LENGTH_LONG).show()/**/

            }
            //
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val std = dataSnapshot.getValue(User::class.java)!!
                    if(std.uid.equals(uid))
                        user = std



                }

            }

        })
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            mProgressBar!!.setMessage("Logging User...")
            mProgressBar!!.show()
            Log.d(TAG, "Logging in user.")
            mAuth!!.signInWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener(this) { task ->
                    mProgressBar!!.hide()
                    if (task.isSuccessful) {
                        // Sign in success, update UI with signed-in user's information
                        Log.d(TAG, "signInWithEmail:success")
                            val intent = Intent(this@StaffLogin, NavActivity::class.java)
                            startActivity(intent)

                    } else {
                        // If sign in fails, display a message to the user.
                        Log.e(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(this@StaffLogin, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@StaffLogin, StudentRegister::class.java)
                        intent.putExtra("uid", user.uid)
                        intent.putExtra("position", user.position)
                        intent.putExtra("email", user.email)
                        intent.putExtra("username", user.username)
                        startActivity(intent)
                    }
                }
        } else {
            Toast.makeText(this, "Enter all details", Toast.LENGTH_SHORT).show()
        }
    }


}
