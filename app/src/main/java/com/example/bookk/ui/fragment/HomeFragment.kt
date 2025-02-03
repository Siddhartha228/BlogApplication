package com.example.bookk.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookk.R
import com.example.bookk.adapter.BlogAdapter
import com.example.bookk.model.BlogModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import androidx.appcompat.widget.SearchView



class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var blogAdapter: BlogAdapter
    private val db: DatabaseReference = FirebaseDatabase.getInstance().reference // Realtime Database reference
    private val blogList = mutableListOf<BlogModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Pass the context to the adapter to show the dialog
        blogAdapter = BlogAdapter(requireContext())
        recyclerView.adapter = blogAdapter

        // Set up SearchView listener
        val searchView = view.findViewById<androidx.appcompat.widget.SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // If the query is empty, show all blogs
                if (newText.isNullOrEmpty()) {
                    blogAdapter.submitList(blogList)
                } else {
                    // Filter blogs based on title
                    val filteredBlogs = blogList.filter { blog ->
                        blog.title.contains(newText, ignoreCase = true)
                    }
                    blogAdapter.submitList(filteredBlogs)
                }
                return true
            }
        })

        // Fetch blogs from Realtime Database
        fetchBlogs()

        return view
    }

    private fun fetchBlogs() {
        // Reference to the "blogs" node in the Realtime Database
        db.child("blogs").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                blogList.clear()
                for (snapshot in dataSnapshot.children) {
                    val blog = snapshot.getValue(BlogModel::class.java)
                    blog?.let {
                        blogList.add(it)
                    }
                }
                blogAdapter.submitList(blogList) // Pass the full list to the adapter initially
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), "Error getting blogs: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString("param1", param1)
                    putString("param2", param2)
                }
            }
    }
}
