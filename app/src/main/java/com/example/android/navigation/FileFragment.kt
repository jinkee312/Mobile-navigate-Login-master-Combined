package com.example.android.navigation

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.navigation.adapters.FileRecyclerAdapter
import com.example.android.navigation.models.File
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.fragment_file.*


/**
 * A simple [Fragment] subclass.
 */
class FileFragment : Fragment() {

    private lateinit var filetable: DatabaseReference
    private lateinit var fileList: MutableList<File>
    private lateinit var recyclerView: RecyclerView
    private lateinit var filestorage: StorageReference
    private lateinit var pdfuri: Uri
    private lateinit var fileAdapter: FileRecyclerAdapter
    private lateinit var courseid: String
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val rootView = inflater.inflate(R.layout.fragment_file, container, false)

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        courseid = arguments!!.getString("courseid")!!

        val title = arguments!!.getString("title")!!

        filetable = FirebaseDatabase.getInstance().getReference("Course").child(courseid)
        LoadData()
        filestorage = FirebaseStorage.getInstance().getReference(title)
        fileList = mutableListOf()


//        addDataSet()
        btnAddFile.setOnClickListener() {
            if (!filename.text.toString().equals("")) {
                if (ContextCompat.checkSelfPermission(activity!!, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    selectPDF()
                } else {

                    var str = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)

                    ActivityCompat.requestPermissions(activity!!, str, 9)

                }
            }
        }

    }




    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 9 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectPDF()
        } else
            Toast.makeText(context, "Please provide permission..", Toast.LENGTH_SHORT).show()

    }

    fun selectPDF() {
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 86)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 86 && resultCode == RESULT_OK && data != null) {
            pdfuri = data.getData()
            uploadFile(pdfuri)
        } else {
            Toast.makeText(context, "Please select a file", Toast.LENGTH_SHORT).show()

        }
    }


    private fun initRecyclerView() {
        recycler_view_file.apply {
            layoutManager = LinearLayoutManager(context)
            val itemDeco = DividerItemDecoration(context, RecyclerView.VERTICAL)
            addItemDecoration(itemDeco)
            fileAdapter = FileRecyclerAdapter(fileList, context, courseid)
            fileAdapter.notifyDataSetChanged()
            adapter = fileAdapter

        }

    }

    private fun uploadFile(pdfuri: Uri) {

        filestorage.child(courseid).child(filename.text.toString()).putFile(pdfuri).addOnSuccessListener {//
            taskSnapshot ->
                filestorage.child(courseid).child(filename.text.toString()).downloadUrl.addOnSuccessListener(){
                    uri ->
                    var url: String = uri.toString()
                    var id: String = filetable.push().key.toString()
                    var name: String = filename.text.trim().toString()
                    var file = File(id, url, name)
                    filetable.child(id).setValue(file)

                }.addOnCompleteListener { task ->

                    Toast.makeText(context, "File Successfully Uploaded", Toast.LENGTH_SHORT).show()
                    filename.setText("")
                }

        }.addOnFailureListener { exception ->
            Toast.makeText(context, "File Not Successfully Uploaded", Toast.LENGTH_SHORT).show()
        }.addOnCompleteListener { task ->

            Toast.makeText(context, "File Successfully Uploaded", Toast.LENGTH_SHORT).show()
        }

    }



    // load data from firebase database
    fun LoadData() {
        // show progress bar when call method as loading concept
//        progressBar.visibility = View.VISIBLE

        filetable.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(context, "Error Encounter Due to " + databaseError.message, Toast.LENGTH_LONG).show()/**/

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {


                    //before fetch we have clear the list not to show duplicate value
                    fileList.clear()
                    // fetch data & add to list

                    for (data in dataSnapshot.children) {
                        val fileid = data.child("fileid").value.toString()
                        val url = data.child("url").value.toString()
                        val filename = data.child("filename").value.toString()
                        val std = File(fileid, url, filename)
//                            val std = data.getValue(File::class.java)
                        if (fileid == "null") {
                            break
                        }
                        fileList.add(std)
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
