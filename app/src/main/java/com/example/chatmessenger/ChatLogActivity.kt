package com.example.chatmessenger

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.chatmessenger.model.ChatMessage
import com.example.chatmessenger.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.activity_new_messages.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row2.view.*

class ChatLogActivity : AppCompatActivity() {

    companion object {
        val TAG = "chatlog"
    }

    val adapter = GroupAdapter<ViewHolder>()

    var toUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerview_chatlog.adapter = adapter

        toUser = intent.getParcelableExtra<User>(NewMessagesActivity.USER_KEY )
        supportActionBar?.title = toUser?.username

        ListenForMessages()

        send_button.setOnClickListener{
            Log.d(TAG, "Send Message Button")
            performSendMessage()
        }
    }

    private fun performSendMessage(){
        val text = edittext_chatlog.text.toString()

        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()

        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessagesActivity.USER_KEY )
        val toId = user.uid
        val chatMessage = ChatMessage(reference.key!!, text, fromId!!, toId, System.currentTimeMillis())

        if (fromId == null) return

        reference.setValue(chatMessage).addOnSuccessListener {
            Log.d(TAG, "Saved our message ${reference.key}")
        }
    }

//    private fun setupDummyData(){
//        val adapter = GroupAdapter<ViewHolder>()
//
//        adapter.add(ChatFromItem("From Message"))
//        adapter.add(ChatToItem("To Message"))
//
//        recyclerview_chatlog.adapter = adapter
//    }
//}

    private fun ListenForMessages(){
        val ref = FirebaseDatabase.getInstance().getReference("/messages")

        ref.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)
                if (chatMessage != null){
                    Log.d(TAG, chatMessage.text)
                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        val currentUser = LatestActivityMessenger.currentUser
                        adapter.add(ChatFromItem(chatMessage.text, currentUser!!))
                    } else {
                        adapter.add(ChatToItem(chatMessage.text, toUser!!))
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })

    }
}

class ChatFromItem(val text: String, val user: User): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_from_row.text = text
        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.profilepic_chat_from_row
        Picasso.get().load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

class ChatToItem(val text: String, val user: User): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textview_to_row.text = text

        //load user pict
        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.profilepic_chat_to_row
        Picasso.get().load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row2
    }
}