package com.example.booktok.repository

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.util.Log
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.example.booktok.model.BookModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.InputStream
import java.util.concurrent.Executors

class BookRepositoryImp : BookRepository {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val ref: DatabaseReference = database.reference.child("Books")

    override fun addBook(bookModel: BookModel, callback: (Boolean, String) -> Unit) {
        val id = ref.push().key.toString()
        bookModel.BookId = id
        Log.d("Firebase", "Adding book: $bookModel")

        ref.child(id).setValue(bookModel).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("Firebase", "Books added successfully")
                callback(true, "Book added successfully")
            } else {
                Log.e("Firebase", "Failed to add Book", task.exception)
                callback(false, task.exception?.message ?: "Unknown error")
            }
        }
    }

    override fun updateBook(
        BookId: String,
        data: MutableMap<String, Any>,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(BookId).updateChildren(data).addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Book updated successfully")
            } else {
                callback(false, "${it.exception?.message}")
            }
        }
    }

    override fun deleteBook(BookId: String, callback: (Boolean, String) -> Unit) {
        ref.child(BookId).removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                callback(true, "Book deleted successfully")
            } else {
                callback(false, "${it.exception?.message}")
            }
        }
    }

    override fun getBookFromDatabase(
        BookId: String,
        callback: (List<BookModel>?, Boolean, String) -> Unit
    ) {
        ref.orderByChild("productName").equalTo(BookId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val books = mutableListOf<BookModel>()
                    for (data in snapshot.children) {
                        val book = data.getValue(BookModel::class.java)
                        if (book != null) {
                            books.add(book)
                        }
                    }
                    if (books.isNotEmpty()) {
                        callback(books, true, "Books fetched successfully")
                    } else {
                        callback(emptyList(), true, "No books found for the given BookId")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null, false, error.message)
                }
            })
    }

    override fun geBookbyId(BookId: String, callback: (BookModel?, Boolean, String) -> Unit) {
        ref.child(BookId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val book = snapshot.getValue(BookModel::class.java)
                    callback(book, true, "Book fetched")
                } else {
                    callback(null, false, "No book found with the given BookId")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null, false, error.message)
            }
        })
    }

    override fun getAllBook(callback: (List<BookModel>?, Boolean, String) -> Unit) {
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val books = mutableListOf<BookModel>()
                for (data in snapshot.children) {
                    val book = data.getValue(BookModel::class.java)
                    if (book != null) {
                        books.add(book)
                    }
                }
                callback(books, true, "Books fetched successfully")
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null, false, error.message)
            }
        })
    }

    private val cloudinary = Cloudinary(
        mapOf(
            "cloud_name" to "dkscpr3wa",
            "api_key" to "776537619471962",
            "api_secret" to "S_YN8k3Ne5Vlnc96hsQ5bAnOPik"
        )
    )

    override fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit) {
        val executor = Executors.newSingleThreadExecutor()
        executor.execute {
            try {
                val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
                var fileName = getFileNameFromUri(context, imageUri)

                fileName = fileName?.substringBeforeLast(".") ?: "uploaded_image"

                val response = cloudinary.uploader().upload(
                    inputStream, ObjectUtils.asMap(
                        "public_id", fileName,
                        "resource_type", "image"
                    )
                )

                var imageUrl = response["url"] as String?
                imageUrl = imageUrl?.replace("http://", "https://")

                Handler(Looper.getMainLooper()).post {
                    callback(imageUrl)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                Handler(Looper.getMainLooper()).post {
                    callback(null)
                }
            }
        }
    }

    override fun getFileNameFromUri(context: Context, uri: Uri): String? {
        var fileName: String? = null
        val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex != -1) {
                    fileName = it.getString(nameIndex)
                }
            }
        }
        return fileName
    }
}
