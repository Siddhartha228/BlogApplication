package com.example.bookk.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookk.R
import com.example.bookk.adapter.BlogAdapter
import com.example.bookk.model.BlogModel
import com.google.firebase.database.*

class BlogFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var blogAdapter: BlogAdapter
    private val db: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val blogList = mutableListOf<BlogModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_blog, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize adapter with the correct callbacks
        blogAdapter = BlogAdapter(
            requireContext(),
            isInBlogFragment = true,
            deleteBlog = ::deleteBlog,
            updateBlog = ::updateBlog // This now passes the whole BlogModel
        )
        recyclerView.adapter = blogAdapter

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

    // Function to delete a blog
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

    // Function to update a blog (navigate to the update fragment or dialog)
    private fun updateBlog(blog: BlogModel) {
        // Navigate to Update Fragment with the full BlogModel data
        val updateFragment = UpdateFragment.newInstance(blog)
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, updateFragment)
            .addToBackStack(null)
            .commit()
    }
}
