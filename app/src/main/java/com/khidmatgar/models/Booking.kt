package com.khidmatgar.models

import java.util.Date
import android.os.Parcel
import android.os.Parcelable

data class Booking(
    val id: Int? = null,
    val serviceProviderId: Int,
    val customerEmail: String,
    val serviceName: String,
    var price: Double? = null,
    val noOfHours: Int = 1,
    var createdAt: String?,
    var deliveryAt: String,
    val location: String,
    var paymentOption: String? = null,
    var status: String? = "Active"
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readValue(Double::class.java.classLoader) as? Double,
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeInt(serviceProviderId)
        parcel.writeString(customerEmail)
        parcel.writeString(serviceName)
        parcel.writeValue(price)
        parcel.writeInt(noOfHours)
        parcel.writeString(createdAt)
        parcel.writeString(deliveryAt)
        parcel.writeString(location)
        parcel.writeString(status)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Booking> {
        override fun createFromParcel(parcel: Parcel): Booking {
            return Booking(parcel)
        }

        override fun newArray(size: Int): Array<Booking?> {
            return arrayOfNulls(size)
        }
    }
}

