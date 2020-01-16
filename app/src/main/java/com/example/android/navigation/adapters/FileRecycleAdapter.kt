package com.example.android.navigation.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.android.navigation.R
import com.example.android.navigation.models.File
import com.example.android.navigation.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FileRecyclerAdapter (val fileList: List<File>, val context: Context, val courseid:String, val type: String): RecyclerView.Adapter<FileRecyclerAdapter.FileViewHolder>()
{

    private var positionType = type

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        return FileViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.layout_file_list_item, parent, false)
        )
    }





    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {

        holder.itemView.setOnClickListener(){
            val perItemPosition = fileList.get(position)
            Log.d("sad",perItemPosition.url)
            var intent = Intent()
            intent.setType(Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.parse(perItemPosition.url), "application/pdf")
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            context.startActivity(intent)
        }

        var name:String = fileList.get(position).filename
        holder.file_title.setText(name)

        if(positionType == "Staff") {

            holder.edit.setOnClickListener()
            {
                val perItemPosition = fileList.get(position)
                updateDialog(perItemPosition)
            }


        holder.delete.setOnClickListener()
        {
            val perItemPosition = fileList.get(position)
            deletedata(perItemPosition.fileid)
            notifyItemRemoved(position)

        }
        }else{
            holder.edit.visibility = View.INVISIBLE
            holder.delete.visibility = View.INVISIBLE
        }



    }

    override fun getItemCount(): Int {
        return fileList.size
    }




    private fun updateDialog(perItemPosition: File) {

        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.update_cf_dialog, null)
        builder.setCancelable(false)

        val editext1 = view.findViewById<EditText>(R.id.editText1)
//        val editext2 = view.findViewById<EditText>(R.id.updatespinerstring)

        // set exist data from recycler to dialog field
        editext1.setText(perItemPosition.filename)
//        editext2.setText(perItemPosition.username)

        // now set view to builder
        builder.setView(view)
        // now set positive negative button in alertdialog
        builder.setPositiveButton("Update", object : DialogInterface.OnClickListener{
            override fun onClick(dialog: DialogInterface?, which: Int) {

                // update operation below
                val filedatabaseref = FirebaseDatabase.getInstance().getReference("Course")

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
                    val std_data = File(perItemPosition.fileid,perItemPosition.url,name)
                    filedatabaseref.child(courseid).child(perItemPosition.fileid).setValue(std_data)
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

    private fun deletedata(fileid: String)
    {
        val studentdatabaseref = FirebaseDatabase.getInstance().getReference("Course").child(courseid).child(fileid)
        studentdatabaseref.removeValue().addOnCompleteListener()
        {
            Toast.makeText(context, "Data Deleted Successfully", Toast.LENGTH_SHORT).show()
        }
    }


    class FileViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val file_title = itemView.findViewById(R.id.file_title) as TextView
//        val blog_author = itemView.findViewById(R.id.blog_author) as TextView
        val edit = itemView.findViewById(R.id.btn_editfile) as ImageView

        val delete = itemView.findViewById(R.id.btn_deletefile) as ImageView

    }

}