package com.example.chatmessenger.views

import com.example.chatmessenger.R
import com.example.chatmessenger.model.ChatMessage
import com.example.chatmessenger.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessageRow(val chatMessage: ChatMessage): Item<ViewHolder>(){
    var chatPartnerUser: User? = null

    override fun bind(viewHolder: ViewHolder, position: Int) {

        val chatPartnerId: String
        if (chatMessage.fromId == FirebaseAuth.getInstance().uid){
            chatPartnerId = chatMessage.toId
        } else {
            chatPartnerId = chatMessage.fromId
        }

        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                chatPartnerUser = p0.getValue(User::class.java) ?: return
                chatPartnerUser?.username
                viewHolder.itemView.username_latest_messages.text = chatPartnerUser?.username
//                  val uri = user.profileImageUrl
                val targetImageView = viewHolder.itemView.profilepic_latestmessages
                Picasso.get().load(chatPartnerUser?.profileImageUrl).into(targetImageView)
            }
            override fun onCancelled(p0: DatabaseError) {

            }
        })
        viewHolder.itemView.latestmessages_textfield.text = chatMessage.text

    }
    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }
}