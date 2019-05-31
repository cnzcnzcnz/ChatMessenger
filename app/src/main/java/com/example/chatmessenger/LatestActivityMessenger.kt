package com.example.chatmessenger

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.example.chatmessenger.NewMessagesActivity.Companion.USER_KEY
import com.example.chatmessenger.model.ChatMessage
import com.example.chatmessenger.model.User
import com.example.chatmessenger.prevalent.Prevalent.currentOnlineUsers
import com.example.chatmessenger.views.LatestMessageRow
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_latest_messenger.*

class LatestActivityMessenger : AppCompatActivity() {

    companion object{
        var currentUser: User? = null
    }

    val TAG = "LatestActivityMessenger"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_messenger)
        Paper.init(this)
        latest_messages_recyclerview.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        latest_messages_recyclerview.adapter = adapter
        currentUser = Paper.book().read<User>("user")
        button.setOnClickListener{

            Log.d("LatestActivityMessenger", "Get from paper : ${currentUser?.username}")
            //Log.d("LatestActivityMessenger", "From paper : $Usernamekey")
            Log.d("LatestActivityMessenger", "Current uid : ${currentUser?.uid}")
            Log.d("LatestActivityMessenger", "Current user : ${currentUser?.username}")
        }

        adapter.setOnItemClickListener { item, _ ->
            Log.d(TAG, "123")
            val intent = Intent(this, ChatLogActivity::class.java)
            val row = item as LatestMessageRow
            row.chatPartnerUser

            intent.putExtra(USER_KEY, row.chatPartnerUser)
            startActivity(intent)
        }
//        setupDummyRows()

        fetchCurrentUser()

        latestMessageRow()

        verifyUserLoggedIn{

        }
    }

    val adapter = GroupAdapter<ViewHolder>()

    val latestMessagesMap = HashMap<String, ChatMessage>()

    private fun refreshRecyclerViewMessages(){
        adapter.clear()
        latestMessagesMap.values.forEach{
            adapter.add(LatestMessageRow(it))
        }
    }

    private fun latestMessageRow() {

        val fromId = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                latestMessagesMap[p0.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java) ?: return
                latestMessagesMap[p0.key!!] = chatMessage
                refreshRecyclerViewMessages()
            }
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }
            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
    }

//    private fun setupDummyRows(){
//        adapter.add(LatestMessageRow())
//        adapter.add(LatestMessageRow())
//        adapter.add(LatestMessageRow())
//    }
    private fun fetchCurrentUser(){
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(User::class.java)
                Log.d("Latest Message", "Current user ${currentUser?.username}")
            }
        })
    }

    private fun verifyUserLoggedIn(function: () -> Unit) {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null){
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.newmessage_button -> {
                val intent = Intent (this, NewMessagesActivity::class.java)
                startActivity(intent)
            }

            R.id.settings_button -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }

            R.id.profile_button -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
