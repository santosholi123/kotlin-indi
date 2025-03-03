package com.example.booktok.ui.activity


import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.booktok.databinding.ActivityBookDetailBinding
import com.example.booktok.model.BookModel
import com.google.android.material.chip.Chip
import com.squareup.picasso.Picasso

class BookDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize binding
        binding = ActivityBookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Get the MovieModel from the Intent
        val movie = intent.getParcelableExtra<BookModel>("MOVIE_KEY")

        if (movie == null) {
            Toast.makeText(this, "Movie data is missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Debugging logs
        Log.d(
            "MovieDetailActivity",
            "Movie Id: ${movie.BookId}, Name: ${movie.BookName}, Rating: ${movie.Rating}, " +
                    "Desc: ${movie.description}, Image: ${movie.imageUrl}"
        )

        // Set data to UI with null safety
        binding.apply {
            movieName.text = movie.BookName.ifEmpty { "Unnamed Movie" }
            movieRating.text = "Rating: ${movie.Rating ?: 0}"
            releaseYear.text = movie.releaseYear?.toString() ?: "N/A"
            duration.text = movie.duration.ifEmpty { "N/A" }
            ageRating.text = movie.ageRating.ifEmpty { "N/A" }
            movieDescription.text = movie.description.ifEmpty { "No description available" }

            // Handle Genres
            genreChips.removeAllViews()
            movie.genres?.takeIf { it.isNotEmpty() }?.forEach { genre ->
                val chip = Chip(root.context).apply {
                    text = genre
                    isCloseIconVisible = false
                }
                genreChips.addView(chip)
            } ?: run {
                val chip = Chip(root.context).apply {
                    text = "No genres available"
                    isCloseIconVisible = false
                }
                genreChips.addView(chip)
            }

            // Load Image with Picasso
            if (!movie.imageUrl.isNullOrEmpty()) {
                Picasso.get().load(movie.imageUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery) // While loading
                    .error(android.R.drawable.ic_menu_report_image) // If loading fails
                    .into(movieImage)
            } else {
                movieImage.setImageResource(android.R.drawable.ic_menu_gallery) // Fallback
            }
        }


        // Optional: Uncomment and adjust if you need a button action
        /*
        binding.startButton.setOnClickListener {
            val resultIntent = Intent().apply {
                putExtra("MOVIE_ID", movie.MovieId)
                putExtra("MOVIE_NAME", movie.MovieName)
                putExtra("MOVIE_RATING", movie.Rating)
                putExtra("MOVIE_DESC", movie.description)
                putExtra("MOVIE_IMAGE", movie.imageUrl)
            }
            setResult(RESULT_OK, resultIntent)
            finish()
        }
        */
    }
}