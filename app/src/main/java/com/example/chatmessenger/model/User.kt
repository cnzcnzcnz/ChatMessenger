package com.example.chatmessenger.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(val uid: String, val email: String, val username: String, val password: String, val profileImageUrl: String): Parcelable {
    constructor() : this("","","","", "")
}