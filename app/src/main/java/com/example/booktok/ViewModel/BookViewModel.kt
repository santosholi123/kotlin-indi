package com.example.booktok.ViewModel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.example.booktok.model.BookModel
import com.example.booktok.repository.BookRepositoryImp

class BookViewModel(val repo: BookRepositoryImp) {

    // Function to add a new Book
    fun addBook(
        bookModel: BookModel,
        callback: (Boolean, String) -> Unit
    ) {
        repo.addBook(bookModel, callback)
    }

    // Function to update an existing Book
    fun updateBook(
        bookId: String,
        data: MutableMap<String, Any>,
        callback: (Boolean, String) -> Unit
    ) {
        repo.updateBook(bookId, data) { success, message ->
            callback(success, message)  // Pass both success and message to the callback
        }
    }

    // Function to delete a Book
    fun deleteBook(
        bookId: String,
        callback: (Boolean, String) -> Unit
    ) {
        repo.deleteBook(bookId, callback)
    }

    // LiveData to hold a single Book's data
    private var _book = MutableLiveData<BookModel?>()
    var book = _book

    // LiveData to hold a list of all Books
    private var _allBooks = MutableLiveData<List<BookModel>?>()
    var allBooks = _allBooks

    // LiveData to track loading state
    private var _loading = MutableLiveData<Boolean>()
    var loading = _loading

    // Function to get a Book by its ID
    fun getBookById(
        bookId: String
    ) {
        repo.geBookbyId(bookId) { book, success, message ->
            if (success) {
                _book.value = book
            }
        }
    }

    // Function to get all Books
    fun getAllBooks() {
        _loading.value = true
        repo.getAllBook() { books, success, message ->
            if (success) {
                _allBooks.postValue(books ?: emptyList())
            }
            _loading.value = false
        }
    }

    // Function to upload an image and get the URL
    fun uploadImage(context: Context, imageUri: Uri, callback: (String?) -> Unit) {
        if (imageUri == null) {
            callback("Image URI is null")
            return
        }

        // Handle the image upload logic here
        repo.uploadImage(context, imageUri) { url ->
            if (url != null) {
                callback(url)  // Successfully uploaded image
            } else {
                callback("Failed to upload image")
            }
        }
    }
}
