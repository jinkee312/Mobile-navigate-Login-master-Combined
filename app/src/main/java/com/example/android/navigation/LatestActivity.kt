package com.example.android.navigation


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.android.navigation.models.ChatMessage
import com.example.android.navigation.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_latest_activity.*

/**
 * A simple [Fragment] subclass.
 */
class LatestActivity : Fragment() {
    companion object {
        var currentUser: User? = null
    }
    private val adapter = GroupAdapter<ViewHolder>()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_latest_activity, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        //Toast.makeText(activity, "Successfull5", Toast.LENGTH_LONG).show()
        Log.d("asd","asd")
        inflater?.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        recyclerview_latest_messages.adapter = adapter
        //add a vertical line only
        recyclerview_latest_messages.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))

        adapter.setOnItemClickListener { item, view ->
            val intent = Intent(activity, chat::class.java)

            val row  = item as LatestMessageRow

            intent.putExtra(NewMessage.USER_KEY, row.chatPartnerUser)
            startActivity(intent)
        }

        listenlatest()
        fetchCurrentUser()
        verifyUserLogged()
    }

    val latestMap = HashMap<String, ChatMessage>()
    fun refreshView(){
        adapter.clear()
        latestMap.values.forEach {
            adapter.add(LatestMessageRow(it))
        }
    }

    fun listenlatest(){
        val fromId = FirebaseAuth.getInstance().uid
        val reference = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")


        reference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatlatestmessage = p0.getValue(ChatMessage::class.java) ?: return

                latestMap[p0.key!!] = chatlatestmessage
                refreshView()

            }
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatlatestmessage = p0.getValue(ChatMessage::class.java) ?: return
                latestMap[p0.key!!] = chatlatestmessage
                refreshView()

            }

            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }
            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
    }

    fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/Users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
                Log.d("LatestMessages", "Current user ${currentUser}")
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    fun verifyUserLogged() {
        val uid = FirebaseAuth.getInstance().uid
        //here need changes later

        if (uid == null) {
            val intent = Intent(activity, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            R.id.menu_new_message -> {
                val intent = Intent(activity, NewMessage::class.java)
                startActivity(intent)
            }
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(activity, RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }



}
