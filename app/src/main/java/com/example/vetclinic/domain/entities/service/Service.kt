package com.example.vetclinic.domain.entities.service

import android.os.Parcel
import android.os.Parcelable

data class Service(
    val id: String,
    val serviceName: String,
    val departmentId: String,
    val price: String,
    val duration: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readString().orEmpty(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(serviceName)
        parcel.writeString(departmentId)
        parcel.writeString(price)
        parcel.writeInt(duration)

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Service> {
        override fun createFromParcel(parcel: Parcel): Service {
            return Service(parcel)
        }

        override fun newArray(size: Int): Array<Service?> {
            return arrayOfNulls(size)
        }
    }
}
