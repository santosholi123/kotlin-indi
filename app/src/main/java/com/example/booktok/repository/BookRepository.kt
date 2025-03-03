package com.example.booktok.repository

import android.content.Context
import android.net.Uri
import com.example.booktok.model.BookModel

interface BookRepository {
    fun addBook(
        bookModel: BookModel,
        callback: (Boolean, String) -> Unit
    )

    fun updateBook(
        BookId: String,
        data: MutableMap<String, Any>,
        callback: (Boolean, String) -> Unit
    )

    fun deleteBook(
        BookId: String,
        callback: (Boolean, String) -> Unit
    )

    fun getBookFromDatabase(
        BookId: String,
        callback: (List<BookModel>?, Boolean, String) -> Unit
    )

    fun geBookbyId(
        BookId: String,
        callback: (BookModel?, Boolean, String) -> Unit
    )

    fun getAllBook(
        callback: (List<BookModel>?, Boolean, String) -> Unit
    )

    fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit)

    fun getFileNameFromUri(context: Context, uri: Uri): String?
}
