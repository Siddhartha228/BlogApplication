package com.example.bookk.model

import android.os.Parcel
import android.os.Parcelable

data class BlogModel(
    val id: String = "", // Keep id as val since it is typically immutable
    var title: String = "",  // Change to var so it can be reassigned
    var preview: String = "",  // Change to var
    var description: String = ""  // Change to var
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(preview)
        parcel.writeString(description)
    }

    override fun describeContents(): Int = 0

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<BlogModel> {
            override fun createFromParcel(parcel: Parcel): BlogModel {
                return BlogModel(parcel)
            }

            override fun newArray(size: Int): Array<BlogModel?> {
                return arrayOfNulls(size)
            }
        }
    }
}
