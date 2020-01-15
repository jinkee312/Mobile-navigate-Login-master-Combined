package com.example.android.navigation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.android.navigation.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_message.view.*
import java.text.FieldPosition

class NewMessage : AppCompatActivity() {
    companion object{
        val USER_KEY = "USER_KEY"
    }
    var toUser: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        toUser = intent.getParcelableExtra<User>(NewMessage.USER_KEY)

        supportActionBar?.title = "Select User"

        getUsers()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, LatestActivity::class.java)
        startActivity(intent)
    }

    private fun getUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/Users")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()

                p0.children.forEach {
//                    currentUser = p0.getValue(User::class.java)
                    val currentUser = FirebaseAuth.getInstance().currentUser?.uid
                    val user = it.getValue(User::class.java)
                    if (user != null) {
                        Log.d("sad",currentUser?.toString())
                        Log.d("sad",user.uid)
                        if (user.uid != currentUser) {
                            adapter.add(UserItem(user))
                        }
                    }
                }
                adapter.setOnItemClickListener { item, view ->

                    val userItem = item as UserItem

                    val intent = Intent(view.context, chat::class.java)
                    //intent.putExtra(USER_KEY,item.user.username)
                    intent.putExtra(USER_KEY, userItem.user)
                    startActivity(intent)

                    finish()
                }

                recycleview_chat.adapter = adapter
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}
class UserItem(val user: User): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.username_row.text = user.username
    }

    override fun getLayout(): Int {
        return R.layout.user_row_message
    }
}
