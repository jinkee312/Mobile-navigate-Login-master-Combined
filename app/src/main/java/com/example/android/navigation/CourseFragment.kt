package com.example.android.navigation


import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.navigation.adapters.CourseRecyclerAdapter
import com.example.android.navigation.models.Course
import com.example.android.navigation.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_course.*
import kotlinx.android.synthetic.main.fragment_course.view.*
import java.lang.Exception

/**
 * A simple [Fragment] subclass.
 */
class CourseFragment : Fragment() {

    private lateinit var coursetable: DatabaseReference
    private lateinit var courseList: MutableList<Course>
    private lateinit var recyclerView: RecyclerView
    //    private lateinit var progressBar: ProgressBar
    private lateinit var user:User


    private lateinit var courseAdapter: CourseRecyclerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
//        progressBar = findViewById(R.id.progressbar)
        coursetable = FirebaseDatabase.getInstance().getReference("Course")
        courseList = mutableListOf()
        user = User()
        val rootView = inflater.inflate(R.layout.fragment_course, container, false)
        getUser()
//        val uid = arguments!!.getString("uid")!!
//        val postion = arguments!!.getString("position")!!
//        val email = arguments!!.getString("email")!!
//        val username = arguments!!.getString("username")!!


//        addDataSet()

        rootView.btnAdd.setOnClickListener() {
            addDialog(user.username)
        }


        LoadData()

        return rootView
    }
    fun getUser(){
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        var userref = FirebaseDatabase.getInstance().getReference("Users").child(uid)
        userref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(context, "Error Encounter Due to " + databaseError.message, Toast.LENGTH_LONG).show()/**/

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
    }

    fun addDialog(name:String){
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.add_dialog, null)
        builder.setCancelable(false)

        val editext1 = view.findViewById<EditText>(R.id.editText1)
//        val editext2 = view.findViewById<EditText>(R.id.updatespinerstring)

        // set exist data from recycler to dialog field
//        editext2.setText(perItemPosition.username)

        // now set view to builder
        builder.setView(view)
        // now set positive negative button in alertdialog
        builder.setPositiveButton("Add", object : DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                val studentdatabaseref = FirebaseDatabase.getInstance().getReference("Course")
                val id = studentdatabaseref.push().key
                val name = editext1!!.text.trim().toString()
                val course = Course(id.toString(), name.trim(), user.username)
                if (TextUtils.isEmpty(name))
                {
                    editext1!!.error = "please Fill up data"
                    editext1!!.requestFocus()
                }
                else
                {
                    // update data
                    val std_data = course
                    studentdatabaseref.child(id.toString()).setValue(std_data)
                    Toast.makeText(context, "Data Updated", Toast.LENGTH_SHORT).show()

                }



            }
        })

        builder.setNegativeButton("No", object : DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {


            }
        })
        // show dialog now
        val alert = builder.create()
        alert.show()
    }




    private fun initRecyclerView() {
        try {
            recycler_view.apply {
                layoutManager = LinearLayoutManager(context)
                val itemDeco = DividerItemDecoration(context, RecyclerView.VERTICAL)
                addItemDecoration(itemDeco)
                courseAdapter = CourseRecyclerAdapter(courseList, context)
                courseAdapter.notifyDataSetChanged()
                adapter = courseAdapter

            }
        }catch (ex:Exception){
            Log.d("",ex.message)

        }


    }



    private fun savedatatoserver() {
        // get value from edit text & spinner

        val title: String = "Mobile App Development".trim()
        val description: String = "Learn how to do mobile app".trim()
        val username = "Koay Jin Kee"

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(description)) {
            val courseid = coursetable.push().key

            val STD = Course(courseid.toString(), title, username)
            coursetable.child(courseid.toString()).setValue(STD)

            coursetable.child(courseid.toString()).setValue(STD).addOnCompleteListener {
                Toast.makeText(context, "Successfull", Toast.LENGTH_LONG).show()
            }


        } else {
            Toast.makeText(context, "Please Enter the name of student", Toast.LENGTH_LONG).show()
        }

    }

    // load data from firebase database
    fun LoadData() {

        // show progress bar when call method as loading concept
//        progressBar.visibility = View.VISIBLE
        coursetable.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(context, "Error Encounter Due to " + databaseError.message, Toast.LENGTH_LONG).show()/**/

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    //before fetch we have clear the list not to show duplicate value
                    courseList.clear()
                    // fetch data & add to list
                    for (data in dataSnapshot.children) {
                        val std = data.getValue(Course::class.java)
                        courseList.add(std!!)
                    }

                    // bind data to adapter
                    initRecyclerView()
//                    progressBar.visibility = View.GONE
                } else {
                    // if no data found or you can check specefici child value exist or not here
                    Toast.makeText(context, "No data Found", Toast.LENGTH_LONG).show()
//                    progressBar.visibility = View.GONE
                }

            }

        })
    }


}
