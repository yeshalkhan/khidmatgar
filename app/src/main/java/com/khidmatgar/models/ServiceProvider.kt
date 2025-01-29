package com.khidmatgar.models

import android.os.Parcel
import android.os.Parcelable

data class ServiceProvider(
    val id: Int?,
    val name: String,
    val phoneNumber: String,
    val address: String,
    val image: String,
    val description: String,
    val rating: Double?,
    val rate: Int,
    val discount: Int?,
    val serviceName: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        id = parcel.readNullableInt(),
        name = parcel.readString() ?: "",
        phoneNumber = parcel.readString() ?: "",
        address = parcel.readString() ?: "",
        image = parcel.readString() ?: "",
        description = parcel.readString() ?: "",
        rating = parcel.readNullableDouble(),
        rate = parcel.readInt(),
        discount = parcel.readNullableInt(),
        serviceName = parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeNullableInt(id)
        parcel.writeString(name)
        parcel.writeString(phoneNumber)
        parcel.writeString(address)
        parcel.writeString(image)
        parcel.writeString(description)
        parcel.writeNullableDouble(rating)
        parcel.writeInt(rate)
        parcel.writeNullableInt(discount)
        parcel.writeString(serviceName)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<ServiceProvider> {
        override fun createFromParcel(parcel: Parcel): ServiceProvider = ServiceProvider(parcel)
        override fun newArray(size: Int): Array<ServiceProvider?> = arrayOfNulls(size)
    }
}

// Extension functions for nullable types

private fun Parcel.writeNullableInt(value: Int?) {
    if (value == null) {
        writeInt(-1)
    } else {
        writeInt(1)
        writeInt(value)
    }
}

private fun Parcel.readNullableInt(): Int? {
    return if (readInt() == -1) null else readInt()
}

private fun Parcel.writeNullableDouble(value: Double?) {
    if (value == null) {
        writeInt(-1)
    } else {
        writeInt(1)
        writeDouble(value)
    }
}

private fun Parcel.readNullableDouble(): Double? {
    return if (readInt() == -1) null else readDouble()
}
