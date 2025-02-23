package com.example.vetclinic.domain.entities

import android.os.Parcel
import android.os.Parcelable

data class Pet(
    val petId: String,
    val userId: String,
    val petName: String,
    val petType: String? = null,
    val petAge: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        with(parcel) {
            writeString(petId)
            writeString(userId)
            writeString(petName)
            writeString(petType)
            writeValue(petAge)
        }
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Pet> {
        override fun createFromParcel(parcel: Parcel): Pet = Pet(parcel)

        override fun newArray(size: Int): Array<Pet?> = arrayOfNulls(size)
    }
}