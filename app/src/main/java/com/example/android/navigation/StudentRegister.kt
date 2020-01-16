@file:Suppress("DEPRECATION")

package com.example.android.navigation

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils.isEmpty
import android.util.Log
import android.view.View
import android.widget.*
import com.example.android.navigation.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.login_activity.*

class StudentRegister : AppCompatActivity() {

    private var etName: EditText? = null
    private var etEmail: EditText? = null
    private var etPassword: EditText? = null
    private var btnCreateAccount: Button? = null
    private var mProgressBar: ProgressDialog? = null
    private var etPicture: ImageButton?=null

    //firebase
    private var mDatabaseReference: DatabaseReference? = null
    private var mDatabase: FirebaseDatabase? = null
    private var mAuth: FirebaseAuth? = null

    private val TAG = "CreateAccountActivity"
    //global variables
    private var name: String? = null
    private var email: String? = null
    private var password: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.studentregister)

        register.setOnClickListener(){
            val intent2 = Intent(this, StudentLogin::class.java)
            startActivity(intent2)
        }



        initialise()


    }

    private fun initialise() {
        etName = findViewById<View>(R.id.txtUsername) as EditText
        etEmail = findViewById<View>(R.id.txtEmail) as EditText
        etPassword = this.findViewById<View>(R.id.txtPassword) as EditText
        btnCreateAccount = findViewById<View>(R.id.signUpBtn) as Button

        mProgressBar = ProgressDialog(this)
        mDatabase = FirebaseDatabase.getInstance()
        mDatabaseReference = mDatabase!!.reference.child("Users")
        mAuth = FirebaseAuth.getInstance()

        btnCreateAccount!!.setOnClickListener {
            createNewAccount()

        }


    }


    private fun createNewAccount(){

        name = etName?.text.toString()
        email = etEmail?.text.toString()
        password = etPassword?.text.toString()

        if (!isEmpty(name) && !isEmpty(email) && !isEmpty(password)) {

            mProgressBar!!.setMessage("Registering User...")
            mProgressBar!!.show()

            mAuth!!
                .createUserWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener(this) { task ->
                    mProgressBar!!.hide()
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")
                        val userId = mAuth!!.currentUser!!.uid
                        //Verify Email



                        val ref = FirebaseDatabase.getInstance().getReference("/Users/$userId")
                        val uid = FirebaseAuth.getInstance().uid
                        val position = "Student"
                        val userM = User(uid.toString(),position.toString(),name.toString(),email.toString())
                        ref.setValue(userM)

                        updateUserInfoAndUI()
                        Toast.makeText(this,"Successfully Registered!!",Toast.LENGTH_SHORT).show()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(this@StudentRegister, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }


        } else {
            Toast.makeText(this, "Enter all details", Toast.LENGTH_SHORT).show()
        }


    }

    private fun updateUserInfoAndUI() {
        //start next activity
        val intent = Intent(this@StudentRegister, StudentLogin::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }




}
