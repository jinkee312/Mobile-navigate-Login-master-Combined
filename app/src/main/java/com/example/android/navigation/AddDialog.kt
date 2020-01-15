package com.example.android.navigation

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.android.navigation.models.Course
import com.google.firebase.database.FirebaseDatabase

class AddDialog : AppCompatDialogFragment() {
    private var editTextTitle: EditText? = null
    //    private EditText editTextPassword;
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)

        val inflater = activity!!.layoutInflater
        val view = inflater.inflate(R.layout.add_dialog, null)
        builder.setView(view)
            .setTitle("Add")
            .setNegativeButton("Cancel") { dialog, i ->}
            .setPositiveButton("OK") { dialog, i ->
                val studentdatabaseref = FirebaseDatabase.getInstance().getReference("Course")
                val id = studentdatabaseref.push().key
                val name = editTextTitle!!.text.trim().toString()
                val course = Course(id.toString(), name.trim(), "asd","Koay Jin Kee")
                if (TextUtils.isEmpty(name))
                {
                    editTextTitle!!.error = "please Fill up data"
                    editTextTitle!!.requestFocus()
                }
                else
                {
                    // update data
                    val std_data = course
                    studentdatabaseref.child(id.toString()).setValue(std_data)
                    Toast.makeText(context, "Data Updated", Toast.LENGTH_SHORT).show()

                }
            }
        editTextTitle = view.findViewById(R.id.editText1)
        return builder.create()
    }
}
