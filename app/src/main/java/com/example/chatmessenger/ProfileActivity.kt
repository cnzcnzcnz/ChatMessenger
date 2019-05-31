package com.example.chatmessenger

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.example.chatmessenger.LatestActivityMessenger.Companion.currentUser
import com.example.chatmessenger.model.User
import com.example.chatmessenger.prevalent.Prevalent
import com.example.chatmessenger.prevalent.Prevalent.currentOnlineUsers
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {
    companion object{
        var onlineUser: User? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        supportActionBar?.title = "Profile"

        Paper.init(this)
//        Paper.book().read<String>(Prevalent.Usernamekey)
//        Paper.book().read<String>(Prevalent.Userpasswordkey)
        onlineUser = Paper.book().read<User>("user")
        val signOut = findViewById(R.id.signout_button) as Button
        val deleteAccount = findViewById(R.id.delete_account_button) as Button
        //var profilePic = findViewById(R.id.profilepicture_profile) as ImageView
        val editProfile = findViewById(R.id.edit_profile_button) as Button
//        fetchCurrentUser()

        username_profile.text = onlineUser?.username
        Picasso.get().load(onlineUser?.profileImageUrl).into(profilepicture_profile)

        //val user = FirebaseDatabase.getInstance().getReference("/users")
        signOut.setOnClickListener {
            signOutProccess()
            Paper.book().destroy()
        }

        deleteAccount.setOnClickListener{
            deleteAccountProcess()
        }

        editProfile.setOnClickListener{
            intent = Intent(this, UpdateProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun deleteAccountProcess() {
        FirebaseAuth.getInstance().currentUser?.delete()
            deleteFromDb()
            currentOnlineUsers = null
            Paper.book().destroy()
            val intent = Intent(this, loginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
    }

    private fun deleteFromDb() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("users").child(uid.toString())
        ref.removeValue()
    }


    private fun signOutProccess() {

        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    //untuk dapat mengambil data dari yang online sekarang, ditaroh di User
//    private fun fetchCurrentUser() {
//        val uid = FirebaseAuth.getInstance().uid
//        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
//        ref.addListenerForSingleValueEvent(object: ValueEventListener {
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//
//            override fun onDataChange(p0: DataSnapshot) {
//                currentUser = p0.getValue(User::class.java)
//                Log.d("Profile Activity", "Current user ${onlineUser?.profileImageUrl}")
//                username_profile.text = onlineUser?.username
//                Picasso.get().load(onlineUser?.profileImageUrl).into(profilepicture_profile)
//            }
//        })
//    }
}
