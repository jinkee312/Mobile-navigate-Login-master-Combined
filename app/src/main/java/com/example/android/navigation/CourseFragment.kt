package com.example.android.navigation


import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.navigation.adapters.CourseRecyclerAdapter
import com.example.android.navigation.models.Course
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_course.*
import kotlinx.android.synthetic.main.fragment_course.view.*

/**
 * A simple [Fragment] subclass.
 */
class CourseFragment : Fragment() {

    private lateinit var coursetable: DatabaseReference
    private lateinit var courseList: MutableList<Course>
    private lateinit var recyclerView: RecyclerView
    //    private lateinit var progressBar: ProgressBar

    private lateinit var courseAdapter: CourseRecyclerAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
//        progressBar = findViewById(R.id.progressbar)
        coursetable = FirebaseDatabase.getInstance().getReference("Course")
        courseList = mutableListOf()

        val rootView = inflater.inflate(R.layout.fragment_course, container, false)

//        addDataSet()

        rootView.btnAdd.setOnClickListener() {
            openDialog()
        }


        LoadData()

        return rootView
    }


    fun openDialog() {
        var adddialog: AddDialog
        adddialog = AddDialog()
        adddialog.show(getFragmentManager(), "Add Dialog")
    }


    private fun initRecyclerView() {
        recycler_view.apply {
            layoutManager = LinearLayoutManager(context)
            val itemDeco = DividerItemDecoration(context, RecyclerView.VERTICAL)
            addItemDecoration(itemDeco)
            courseAdapter = CourseRecyclerAdapter(courseList, context)
            courseAdapter.notifyDataSetChanged()
            adapter = courseAdapter

        }

    }


    private fun initdefaultdata() {
        val courseid = coursetable.push().key
        val title: String = "Mobile App Development".trim()
        val description: String = "Learn how to do mobile app".trim()
        val username = "Koay Jin Kee"
        val STD = Course(courseid.toString(), title, description, username)
        coursetable.child(courseid.toString()).setValue(STD)

        coursetable.child(courseid.toString()).setValue(STD).addOnCompleteListener {

            Toast.makeText(context, "Successfull", Toast.LENGTH_LONG).show()
//            progressBar.visibility = View.GONE

        }
    }

    private fun savedatatoserver() {
        // get value from edit text & spinner

        val title: String = "Mobile App Development".trim()
        val description: String = "Learn how to do mobile app".trim()
        val username = "Koay Jin Kee"

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(description)) {
            val courseid = coursetable.push().key

            val STD = Course(courseid.toString(), title, description, username)
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
