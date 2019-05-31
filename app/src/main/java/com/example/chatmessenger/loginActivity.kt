package com.example.chatmessenger

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Button
import android.widget.Toast
import com.example.chatmessenger.model.User
import com.example.chatmessenger.prevalent.Prevalent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.paperdb.Paper
import kotlinx.android.synthetic.main.activity_login.*
import java.util.regex.Pattern
//import android.support.test.orchestrator.junit.BundleJUnitUtils.getResult
import com.google.android.gms.tasks.Task
import android.support.annotation.NonNull
import android.view.View
//import com.example.chatmessenger.prevalent.Prevalent.Userpasswordkey
import com.example.chatmessenger.prevalent.Prevalent.currentOnlineUsers
//import com.example.chatmessenger.prevalent.Prevalent.emailkey
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.SignInMethodQueryResult

class loginActivity : AppCompatActivity() {

    companion object{
        var currentUser: User? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)

        Paper.init(this)

        val loadingBar = ProgressDialog(this)

        val submitButton = findViewById(R.id.submit_button) as Button

        submitButton.setOnClickListener() {
            val password = password_login.text.toString()
            val email = email_login.text.toString()

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Please input email first", Toast.LENGTH_SHORT).show()
            } else if (TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Please input password first", Toast.LENGTH_SHORT).show()
            } else if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
//                loadingBar.setTitle("Logging in")
//                loadingBar.setMessage("Please wait, while we are checking credentials")
//                loadingBar.setCancelable(false)
//                loadingBar.show()

                AllowAccessToAccount(email, password)
            }
        }

//        fun isEmailValid(email: String): Boolean {
//            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()


//            fun String.isValidEmail(): Boolean = this.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
//            email.validate({})
//        }
    }

    private fun AllowAccessToAccount(email: String, password: String) {

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener {
            fetchCurrentUser()
            moveActivity()
        }
    }

    private fun moveActivity() {
        val intent = Intent(this, LatestActivityMessenger::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

//    fun isCheckEmail(email: String, listener: OnEmailCheckListener) {
//        FirebaseAuth.getInstance().fetchProvidersForEmail(email).addOnCompleteListener(OnCompleteListener<Any> { task ->
//            val check = !task.result!!.getProviders().isEmpty()
//
//            listener.onSucess(check)
//        })
//
//    }
//    fun checkEmailExistsOrNot() {
//        FirebaseAuth.fetchSignInMethodsForEmail(email.getText().toString())
//            .addOnCompleteListener(OnCompleteListener<SignInMethodQueryResult> { task ->
//                Log.d(FragmentActivity.TAG, "" + task.result!!.signInMethods!!.size)
//                if (task.result!!.signInMethods!!.size == 0) {
//                    // email not existed
//                } else {
//                    // email existed
//                }
//            }).addOnFailureListener(OnFailureListener { e -> e.printStackTrace() })
//    }


    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("users").child(uid.toString())
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                currentOnlineUsers = p0.getValue(User::class.java)
                Paper.book().write("user", currentOnlineUsers)
//                Paper.book().read<User>("user")
//                Log.d("Latest Message", "Current user from paper : $currentOnlineUsers")
//                Toast.makeText("loginActivity", "Welcome ${currentUser?.username}",Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(p0: DatabaseError) {

            }


        })
    }
}




