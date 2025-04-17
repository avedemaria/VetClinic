package com.example.vetclinic.domain.entities.doctor

import android.os.Parcel
import android.os.Parcelable

data class Doctor(
    val uid: String,
    val doctorName: String,
    val departmentId: String,
    val role: String,
    val photoUrl: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        with(parcel) {
            writeString(uid)
            writeString(doctorName)
            writeString(departmentId)
            writeString(role)
            writeString(photoUrl)
        }
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Doctor> {
        override fun createFromParcel(parcel: Parcel): Doctor = Doctor(parcel)

        override fun newArray(size: Int): Array<Doctor?> = arrayOfNulls(size)
    }
}



