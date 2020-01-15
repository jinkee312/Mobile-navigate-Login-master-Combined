@file:Suppress("DEPRECATION")

package com.example.android.navigation.adapters


import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.android.navigation.models.Course
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import android.os.Bundle
import com.example.android.navigation.FileFragment
import com.example.android.navigation.R
import androidx.appcompat.app.AppCompatActivity


//

class CourseRecyclerAdapter (val courseList: List<Course>, val context: Context): RecyclerView.Adapter<CourseRecyclerAdapter.CourseViewHolder>()
{
    private lateinit var coursetable: DatabaseReference


//    private var items: List<BlogPost> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        return CourseViewHolder(
                LayoutInflater.from(parent.context).inflate(com.example.android.navigation.R.layout.layout_course_list_item, parent, false)
        )
    }





    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {


        holder.itemView.setOnClickListener(){
            val perItemPosition = courseList.get(position)

            val bundle = Bundle()
            bundle.putString("courseid", perItemPosition.courseid)
            bundle.putString("title",perItemPosition.title)


            val myFragment = FileFragment()
            myFragment.setArguments(bundle)
            val activity = context as AppCompatActivity


//            (context as FragmentActivity).fragmentManager.beginTransaction()
//                    .replace(R.id.courseFragment, myFragment as Fragment)
//                    .commit()

            activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.courseFragment,myFragment).addToBackStack(null).commit()//

        }
        holder.blog_title.text = courseList.get(position).title
        holder.blog_author.text = courseList.get(position).username
//        holder.bind(fileList.get(position))
        holder.edit.setOnClickListener()
        {
            val perItemPosition = courseList.get(position)
            updateDialog(perItemPosition)
        }

        holder.delete.setOnClickListener()
        {
            val perItemPosition = courseList.get(position)
            deletedata(perItemPosition.courseid)
            notifyItemRemoved(position)

        }


    }

    override fun getItemCount(): Int {
        return courseList.size
    }



    private fun updateDialog(perItemPosition: Course) {

        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.update_cf_dialog, null)
        builder.setCancelable(false)

        val editext1 = view.findViewById<EditText>(R.id.editText1)
//        val editext2 = view.findViewById<EditText>(R.id.updatespinerstring)

        // set exist data from recycler to dialog field
        editext1.setText(perItemPosition.title)
//        editext2.setText(perItemPosition.username)

        // now set view to builder
        builder.setView(view)
        // now set positive negative button in alertdialog
        builder.setPositiveButton("Update", object : DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {

                // update operation below
                val studentdatabaseref = FirebaseDatabase.getInstance().getReference("Course")

                val name = editext1.text.toString()
//                val department = editext2.text.toString()

                if (name.isEmpty() ) //&& department.isEmpty()
                {
                    editext1.error = "please Fill up data"
                    editext1.requestFocus()
                    return
                }
                else
                {
                    // update data
                    val std_data = Course(perItemPosition.courseid,name,perItemPosition.desc,perItemPosition.username)
                    studentdatabaseref.child(perItemPosition.courseid).setValue(std_data)
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

    private fun deletedata(courseid: String)
    {

        val studentdatabaseref = FirebaseDatabase.getInstance().getReference("Course").child(courseid)
        studentdatabaseref.removeValue().addOnCompleteListener()
        {
            Toast.makeText(context, "Data Deleted Successfully", Toast.LENGTH_SHORT).show()
        }
        Log.d("sad",""+courseList.size)




    }






//    fun submitList(fileList: List<Course>){
//        this.fileList = fileList
//    }

    class CourseViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
//    constructor(
//        itemView: View
//    ): RecyclerView.ViewHolder(itemView){

        //   val blog_image = itemView.blog_image
        val blog_title = itemView.findViewById(R.id.blog_title) as TextView
        val blog_author = itemView.findViewById(R.id.blog_author) as TextView
        val edit = itemView.findViewById(R.id.btn_editcourse) as ImageView
        val delete = itemView.findViewById(R.id.btn_deletecourse) as ImageView


//        fun bind(course: Course){
//
//            blog_title.setText(course.title)
//            blog_author.setText(course.username)
//        }

    }

}