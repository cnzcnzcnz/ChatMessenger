package com.example.chatmessenger

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.example.chatmessenger.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

@SuppressLint("ByteOrderMark")
class MainActivity : AppCompatActivity() {

    private val READ_REQUEST_CODE = 5

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        setupPermissions()

        val username = username_text_field.text.toString()
        val registerButton = findViewById(R.id.register_button) as Button
        val selectPhoto = findViewById(R.id.selectphoto_register_button) as Button

        registerButton.setOnClickListener{
            registerAction()
        }

        already_have_account_button.setOnClickListener{
            val i = Intent (this, loginActivity::class.java)
            startActivity(i)
        }

        selectPhoto.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }
    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            selectedPhotoUri = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            selectphoto_register_button.alpha = 0f
            selectphoto_imageview.setImageBitmap(bitmap)
            //val bitmapDrawable = BitmapDrawable(bitmap)
            //selectphoto_register_button.setBackgroundDrawable(bitmapDrawable)
        }
    }
    //asking permission for access external storage function
    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("MainActivity", "Permission to read denied")
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            READ_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            READ_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {

                    Log.i("MainActivity", "Permission has been denied by user")
                } else {
                    Log.i("MainActivity", "Permission has been granted by user")
                }
            }
        }
    }

    //registration function proccess
    private fun registerAction() {
        val password = password_text_field.text.toString()
        val email = email_text_field.text.toString()
        //checking textfield is empty or not
        if (email.isEmpty() || password.isEmpty()){
            Toast.makeText(this,"Please enter your email or password first", Toast.LENGTH_SHORT)
                .show()
            return
        }

        Log.d("MainActivity", "Email is "+ email)
        Log.d("MainActivity", "the password is : " + password)
        //Firebase authentication to create a user with email and password
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{
                if(!it.isSuccessful) return@addOnCompleteListener

                Log.d("Main", "Succesfully created with uid: ${it.result?.user?.uid}")
                uploadImagetoFirebaseStorage()
            }
            .addOnFailureListener{
                Log.d("Main", "Failed to create user: ${it.message}")
                Toast.makeText(this,"Failed to create user: ${it.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    //Function to save user to firebase
    private fun uploadImagetoFirebaseStorage() {
        if (selectedPhotoUri == null)return
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!).addOnSuccessListener {
            Log.d("MainActivity", "Successfuly upload image: ${it.metadata?.path}")

            ref.downloadUrl.addOnSuccessListener {
                Log.d("MainaActivity", "File location: $it")

                saveUsertoFirebaseDb(it.toString())
            }
        }
    }

    private fun saveUsertoFirebaseDb(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, username_text_field.text.toString(), profileImageUrl)

        ref.setValue(user).addOnSuccessListener{
            Log.d("MainActivity", "We saved user to Firebase Database")

            val intent = Intent(this, LatestActivityMessenger::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
            .addOnFailureListener{
                Log.d("MainActivity", "Failed to set Value to database : ${it.message}")
            }
    }
}

//test commit

