package com.example.bookk.ui.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.bookk.R
import com.example.bookk.model.BlogModel // Import your BlogModel class
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AddFragment : Fragment() {

    private lateinit var blogTitle: EditText
    private lateinit var blogDescription: EditText
    private lateinit var addBlogButton: Button
    private val db = FirebaseDatabase.getInstance().reference // Realtime Database reference
    private val auth = FirebaseAuth.getInstance() // Firebase Auth instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // Handle any parameters here if needed
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        blogTitle = view.findViewById(R.id.blogTitle)
        blogDescription = view.findViewById(R.id.blogDescription)
        addBlogButton = view.findViewById(R.id.addBlogButton)

        addBlogButton.setOnClickListener {
            val title = blogTitle.text.toString()
            val description = blogDescription.text.toString()

            if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description)) {
                Toast.makeText(requireContext(), "Please fill out both fields", Toast.LENGTH_SHORT).show()
            } else {
                // Get the logged-in user's email
                val currentUserEmail = auth.currentUser?.email
                if (currentUserEmail != null) {
                    // Create a BlogModel instance with authorEmail
                    val blog = BlogModel(
                        title = title,
                        preview = title, // You can use title as preview
                        description = description,
                        authorEmail = currentUserEmail // Add the user's email
                    )
                    addBlogToDatabase(blog)
                } else {
                    Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
                }
            }
        }

        return view
    }

    private fun addBlogToDatabase(blog: BlogModel) {
        val blogRef = db.child("blogs").push() // Auto-generate a unique key under "blogs"
        val blogWithId = blog.copy(id = blogRef.key ?: "") // Set the blog ID to the generated key
        blogRef.setValue(blogWithId)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Blog added successfully", Toast.LENGTH_SHORT).show()
                navigateToHomeFragment() // Navigate to HomeFragment after success
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error adding blog: $e", Toast.LENGTH_SHORT).show()
            }
    }

    private fun navigateToHomeFragment() {
        // Navigate to HomeFragment after successfully adding the blog
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.frameLayout, HomeFragment()) // Make sure the ID matches your container
            .addToBackStack(null) // Optional: If you want to allow back navigation
            .commit()
    }

    companion object {
        @JvmStatic
        fun newInstance() = AddFragment()
    }
}
