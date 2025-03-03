package com.example.booktok.ui.fragment

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.booktok.ViewModel.BookViewModel
import com.example.booktok.adapter.BookAdapter
import com.example.booktok.databinding.FragmentBookDashboardBinding
import com.example.booktok.repository.BookRepositoryImp
import com.example.booktok.ui.activity.AddBookActivity


class BookDashboardFragment : Fragment() {
    private lateinit var binding: FragmentBookDashboardBinding
    private lateinit var movieViewModel: BookViewModel
    private lateinit var adapter: BookAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBookDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val repo = BookRepositoryImp()
        movieViewModel= BookViewModel(repo)
        adapter = BookAdapter(requireContext(), ArrayList())

        // Setup RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        movieViewModel.getAllBooks()
        // Observe product list
        movieViewModel.allBooks.observe(viewLifecycleOwner) { products ->
            products?.let { adapter.updateData(it) }
        }

        // Observe loading state
        movieViewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Floating button to add workouts
        binding.floatingActionButton.setOnClickListener {
            startActivity(Intent(requireContext(), AddBookActivity::class.java))
        }

        // Swipe to delete feature
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false // Disables moving items
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val productId = adapter.getProductId(viewHolder.adapterPosition)

                // Show confirmation dialog before deleting
                AlertDialog.Builder(requireContext())
                    .setTitle("Confirm Deletion")
                    .setMessage("Are you sure you want to delete this item?")
                    .setPositiveButton("OK") { _, _ ->
                        // User clicked OK, delete the item
                        movieViewModel.deleteBook(productId) { success, message ->
                            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        }
                    }
                    .setNegativeButton("Cancel") { _, _ ->
                        // User clicked Cancel, restore the item
                        adapter.notifyItemChanged(viewHolder.adapterPosition)
                    }
                    .setCancelable(false) // Prevent dismissing by tapping outside
                    .show()
            }
        }).attachToRecyclerView(binding.recyclerView)

    }

}