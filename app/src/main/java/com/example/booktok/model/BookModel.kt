package com.example.booktok.model

import android.os.Parcel
import android.os.Parcelable

data class BookModel(
    var BookId: String = "",
    var BookName: String = "",
    var description: String = "",
    var movielink: String = "",
    var Rating: Float = 0f,  // Changed to Float for decimal ratings
    var imageUrl: String = "",
    // New fields
    var releaseYear: String = "",
    var duration: String = "",
    var ageRating: String = "",
    var genres: List<String> = listOf(),
    var trailerUrl: String = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readFloat(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: listOf(),
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(BookId)
        parcel.writeString(BookName)
        parcel.writeString(description)
        parcel.writeString(movielink)
        parcel.writeFloat(Rating)
        parcel.writeString(imageUrl)
        parcel.writeString(releaseYear)
        parcel.writeString(duration)
        parcel.writeString(ageRating)
        parcel.writeStringList(genres)
        parcel.writeString(trailerUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BookModel> {
        override fun createFromParcel(parcel: Parcel): BookModel {
            return BookModel(parcel)
        }

        override fun newArray(size: Int): Array<BookModel?> {
            return arrayOfNulls(size)
        }
    }
}

