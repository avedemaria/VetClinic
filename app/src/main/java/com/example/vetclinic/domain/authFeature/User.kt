package com.example.vetclinic.domain.authFeature

import android.os.Parcel
import android.os.Parcelable

data class User(
    val uid: String,
    val userName: String,
    val userLastName: String,
    val petName: String,
    val phoneNumber: String,
    val email: String,

    ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

        with(parcel) {
            writeString(uid)
            writeString(userName)
            writeString(userLastName)
            writeString(petName)
            writeString(phoneNumber)
            writeString(email)
        }

    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User = User(parcel)

        override fun newArray(size: Int): Array<User?> = arrayOfNulls(size)
    }
}