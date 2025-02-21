package com.example.vetclinic.domain.selectDoctorFeature

import android.os.Parcel
import android.os.Parcelable

data class Doctor(
    val uid: String,
    val doctorName: String,
    val department: String,
    val role: String,
    val photoUrl: String
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) = with(parcel) {
        writeString(uid)
        writeString(doctorName)
        writeString(department)
        writeString(role)
        writeString(photoUrl)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Doctor> {
        override fun createFromParcel(parcel: Parcel): Doctor = Doctor(parcel)
        override fun newArray(size: Int): Array<Doctor?> = arrayOfNulls(size)
    }
}
