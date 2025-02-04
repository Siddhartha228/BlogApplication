package com.example.bookk.ui.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookk.R
import com.example.bookk.adapter.BlogAdapter
import com.example.bookk.model.BlogModel
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var blogAdapter: BlogAdapter
    private val db: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val blogList = mutableListOf<BlogModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize adapter with Read More, Update, and Delete functionality
        blogAdapter = BlogAdapter(requireContext(), isInBlogFragment = false, deleteBlog = ::deleteBlog, updateBlog = ::updateBlog)
        recyclerView.adapter = blogAdapter

        val searchView = view.findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    blogAdapter.submitList(blogList)
                } else {
                    val filteredBlogs = blogList.filter { blog ->
                        blog.title.contains(newText, ignoreCase = true)
                    }
                    blogAdapter.submitList(filteredBlogs)
                }
                return true
            }
        })

        // Fetch blogs from the database
        fetchBlogs()
        return view
    }

    private fun fetchBlogs() {
        db.child("blogs").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                blogList.clear()
                for (snapshot in dataSnapshot.children) {
                    val blog = snapshot.getValue(BlogModel::class.java)
                    blog?.let { blogList.add(it) }
                }
                blogAdapter.submitList(blogList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), "Error getting blogs: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Function to delete a blog from the database
    private fun deleteBlog(blogId: String) {
        val blogRef = db.child("blogs").child(blogId)
        blogRef.removeValue()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Blog deleted successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error deleting blog: $e", Toast.LENGTH_SHORT).show()
            }
    }

    // Function to update a blog (navigate to an update fragment or dialog)
    private fun updateBlog(blog: BlogModel) {
        // Use the full BlogModel object to update the blog
        val updateFragment = UpdateFragment.newInstance(blog)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, updateFragment)
            .addToBackStack(null)
            .commit()
    }


    // Function to show full blog in a popup dialog
    private fun showFullBlog(blog: BlogModel) {
        val scrollView = ScrollView(requireContext())
        val textView = TextView(requireContext())
        textView.text = blog.description
        textView.setPadding(32, 32, 32, 32)
        textView.textSize = 16f
        scrollView.addView(textView)

        AlertDialog.Builder(requireContext())
            .setTitle(blog.title)
            .setView(scrollView)
            .setNegativeButton("Close") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
