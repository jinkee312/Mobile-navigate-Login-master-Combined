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
    private lateinit var userCourseList: MutableList<Course>

    private lateinit var user:User
    private var count:Long = 1

    private lateinit var courseAdapter: CourseRecyclerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
//        progressBar = findViewById(R.id.progressbar)
        coursetable = FirebaseDatabase.getInstance().getReference("Course")
        courseList = mutableListOf()
        userCourseList = mutableListOf()
        user = User()
        val rootView = inflater.inflate(R.layout.fragment_course, container, false)
//        val uid = arguments!!.getString("uid")!!
//        val postion = arguments!!.getString("position")!!
//        val email = arguments!!.getString("email")!!
//        val username = arguments!!.getString("username")!!


//        addDataSet()

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        btnAdd.setOnClickListener() {
                addDialog()
        }
    }

    override fun onStart(){
        super.onStart()
        getUser()
        getUserCourse()
        LoadData()
    }

    fun getUser(){
        val uid = FirebaseAuth.getInstance().currentUser?.uid.toString()
        var userref = FirebaseDatabase.getInstance().getReference("Users").child(uid)

        userref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(context, "Error Encounter Due to " + databaseError.message, Toast.LENGTH_LONG).show()/**/

            }
            //
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                user = dataSnapshot.getValue(User::class.java)!!
                if (user != null) {
                    Log.d("sad",user.position + " " + user.username)
                    getUserCourse()
                }else{
                    Log.d("sad"," no entered")
                }
            }
        })
//        val c = User("C1","","","")
//        userref.child("try").setValue(c)
//        userref.child("try").removeValue()
    }

    fun addDialog(){

        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.add_dialog, null)
        builder.setCancelable(false)

        val editext1 = view.findViewById<EditText>(R.id.editText1)
        // now set view to builder
        builder.setView(view)
        // now set positive negative button in alertdialog
        builder.setPositiveButton("Add", object : DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {
                val studentdatabaseref = FirebaseDatabase.getInstance().getReference("Course")
                val userdatabaseref = FirebaseDatabase.getInstance().getReference("Users")

                val id = "C"+ count
                val name = editext1!!.text.trim().toString()
                val course = Course(id, name.trim(), user.username)
                if (TextUtils.isEmpty(name))
                {
                    editext1!!.error = "please Fill up data"
                    editext1!!.requestFocus()
                }
                else
                {
                    if (user.position.equals("Staff")) {
                        val std_data = course
                        studentdatabaseref.child(id).setValue(std_data)
                        Toast.makeText(context, "Data Added", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        for (x in courseList) {
                            if (x.courseid.equals(name)) {
                                val std_data = x
                                userdatabaseref.child(user.uid).child("course").child(x.courseid).setValue(std_data)
                                Toast.makeText(context, "Data Added", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
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

    fun getUserCourse(){

        var userref = FirebaseDatabase.getInstance().getReference("Users").child(user.uid).child("course")
        userref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(context, "Error Encounter Due to " + databaseError.message, Toast.LENGTH_LONG).show()/**/

            }

            //
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userCourseList.clear()

                if (dataSnapshot.exists()) {
                    try {
                        for (data in dataSnapshot.children) {
                            val std = data.getValue(Course::class.java)!!
                            userCourseList.add(std)
                            Log.d("sad", userCourseList[userCourseList.size - 1].title)

                        }
                    }catch (ex:Exception){
                        Log.d("sad",ex.message)
                    }

                    initRecyclerView()
                }
            }
        })

    }


    private fun initRecyclerView() {
        try {
            Log.d("sad",user.position)
                if(user.position.equals("Student")) {
                    recycler_view.apply {
                        layoutManager = LinearLayoutManager(context)
                        val itemDeco = DividerItemDecoration(context, RecyclerView.VERTICAL)
                        addItemDecoration(itemDeco)
                        courseAdapter = CourseRecyclerAdapter(userCourseList, context, user.position)
                        courseAdapter.notifyDataSetChanged()
                        adapter = courseAdapter
                    }
                }else{
                    recycler_view.apply {
                        layoutManager = LinearLayoutManager(context)
                        val itemDeco = DividerItemDecoration(context, RecyclerView.VERTICAL)
                        addItemDecoration(itemDeco)
                        courseAdapter = CourseRecyclerAdapter(courseList, context, user.position)
                        courseAdapter.notifyDataSetChanged()
                        adapter = courseAdapter

                    }
                }


        }catch (ex:Exception){
            Log.d("",ex.message)
        }
    }

    // load data from firebase database
    fun LoadData() {
        Log.d("sad",user.position)
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
                        count = dataSnapshot.childrenCount+1

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
