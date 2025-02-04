package com.example.bookk.ui.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.bookk.R
import com.example.bookk.model.BlogModel
import com.google.firebase.database.FirebaseDatabase

class UpdateFragment : Fragment() {

    private lateinit var blogTitle: EditText
    private lateinit var blogDescription: EditText
    private lateinit var updateButton: Button
    private lateinit var progressBar: ProgressBar // Added for loading state feedback
    private val db = FirebaseDatabase.getInstance().getReference("blogs")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_update, container, false)

        blogTitle = view.findViewById(R.id.blogTitle)
        blogDescription = view.findViewById(R.id.blogDescription)
        updateButton = view.findViewById(R.id.updateButton)
        progressBar = view.findViewById(R.id.progressBar) // Initialize progress bar

        // Retrieve the BlogModel passed from the HomeFragment
        var blog = arguments?.getParcelable<BlogModel>("blog")

        if (blog == null) {
            // Show an error message if the blog is null
            Toast.makeText(requireContext(), "Blog data not found", Toast.LENGTH_SHORT).show()
            requireActivity().supportFragmentManager.popBackStack() // Navigate back
        } else {
            // Prepopulate the EditTexts with the blog data
            blogTitle.setText(blog.title)
            blogDescription.setText(blog.description)

            // Handle the update button click
            updateButton.setOnClickListener {
                val updatedTitle = blogTitle.text.toString()
                val updatedDescription = blogDescription.text.toString()

                if (TextUtils.isEmpty(updatedTitle) || TextUtils.isEmpty(updatedDescription)) {
                    // Display a toast if either field is empty
                    Toast.makeText(requireContext(), "Please fill out both fields", Toast.LENGTH_SHORT).show()
                } else {
                    // Show loading indicator
                    progressBar.visibility = View.VISIBLE

                    // Update the blog data in Firebase
                    blog.title = updatedTitle
                    blog.description = updatedDescription

                    db.child(blog.id).setValue(blog)
                        .addOnSuccessListener {
                            // Hide loading indicator and show success message
                            progressBar.visibility = View.GONE
                            Toast.makeText(requireContext(), "Blog updated successfully", Toast.LENGTH_SHORT).show()
                            requireActivity().supportFragmentManager.popBackStack()
                        }
                        .addOnFailureListener {
                            // Hide loading indicator and show error message
                            progressBar.visibility = View.GONE
                            Toast.makeText(requireContext(), "Error updating blog", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }

        return view
    }

    companion object {
        /**
         * Factory method to create a new instance of UpdateFragment
         * @param blog The BlogModel to be updated
         * @return A new instance of UpdateFragment
         */
        @JvmStatic
        fun newInstance(blog: BlogModel) = UpdateFragment().apply {
            arguments = Bundle().apply {
                putParcelable("blog", blog)
            }
        }
    }
}
