package com.example.booktok.adapter


import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.booktok.R
import com.example.booktok.model.BookModel
import com.example.booktok.ui.activity.BookDetailActivity

import com.example.booktok.ui.activity.UpdateBookActivity
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso

class BookAdapter(
    private val context: Context,
    private var data: ArrayList<BookModel>
) : RecyclerView.Adapter<BookAdapter.ProductViewHolder>() {

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.cardWorkout)
        val imageView: ImageView = itemView.findViewById(R.id.getImage)
        val loading: ProgressBar = itemView.findViewById(R.id.progressBar2)
        val productName: TextView = itemView.findViewById(R.id.displayname)
        val productPrice: TextView = itemView.findViewById(R.id.displaySets)
        val productDesc: TextView = itemView.findViewById(R.id.displayDesc)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val itemView: View = LayoutInflater.from(context).inflate(
            R.layout.sample_movie,
            parent, false
        )
        return ProductViewHolder(itemView)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val movie = data[position]

        // Bind data with null safety
        holder.productName.text = movie.BookName ?: "Unnamed Movie"
        holder.productPrice.text = movie.Rating?.toString() ?: "0"
        holder.productDesc.text = movie.description ?: "No description"

        // Load image with Picasso
        val imageUrl = movie.imageUrl
        if (!imageUrl.isNullOrEmpty()) {
            holder.loading.visibility = View.VISIBLE
            Picasso.get().load(imageUrl).into(holder.imageView, object : Callback {
                override fun onSuccess() {
                    holder.loading.visibility = View.GONE
                }

                override fun onError(e: Exception?) {
                    Log.e("Picasso", "Error loading image: ${e?.message}")
                    holder.loading.visibility = View.GONE
                    holder.imageView.setImageResource(R.drawable.placeholder) // Fallback image
                }
            })
        } else {
            holder.imageView.setImageResource(R.drawable.placeholder)
            holder.loading.visibility = View.GONE
        }

//        // Edit button click listener
//        holder.btnEdit.setOnClickListener {
//            val intent = Intent(context, UpdateMovieActivity::class.java).apply {
//                putExtra("products", movie.MovieId) // Key matches your UpdateMovieActivity
//            }
//            context.startActivity(intent)
//        }

        // Card click listener to open MovieDetailActivity
        holder.cardView.setOnClickListener {
            val intent = Intent(context, BookDetailActivity::class.java).apply {
                putExtra("MOVIE_KEY", movie) // Pass the entire MovieModel as Parcelable
            }
            context.startActivity(intent)
        }
    }

    fun updateData(products: List<BookModel>) {
        data.clear()
        data.addAll(products)
        notifyDataSetChanged() // Uncommented to refresh the UI
    }

    fun getProductId(position: Int): String {
        return data[position].BookId
    }
}