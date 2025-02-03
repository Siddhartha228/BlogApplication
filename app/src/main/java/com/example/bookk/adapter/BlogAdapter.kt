package com.example.bookk.adapter

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bookk.model.BlogModel
import com.example.bookk.R

class BlogAdapter(private val context: Context) : RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {

    private val blogList = mutableListOf<BlogModel>()

    // This function updates the list in the adapter and notifies the RecyclerView
    fun submitList(blogs: List<BlogModel>) {
        blogList.clear()
        blogList.addAll(blogs)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.blog_item, parent, false)
        return BlogViewHolder(view)
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        val blog = blogList[position]
        holder.bind(blog)
    }

    override fun getItemCount() = blogList.size

    // ViewHolder class binds data from the BlogModel to the item layout
    inner class BlogViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleTextView: TextView = view.findViewById(R.id.blogTitle)
        private val descriptionTextView: TextView = view.findViewById(R.id.blogPreview)
        private val readMoreButton: Button = view.findViewById(R.id.readMoreBtn)

        fun bind(blog: BlogModel) {
            // Bind data to the UI elements
            titleTextView.text = blog.title
            descriptionTextView.text = if (blog.description.length > 100) {
                blog.description.take(100) + "..."  // Show truncated text if longer than 100 characters
            } else {
                blog.description
            }

            // Set the onClickListener to show the full description in a dialog
            readMoreButton.setOnClickListener {
                // Create a ScrollView to hold the full description in the dialog
                val scrollView = ScrollView(context)
                val textView = TextView(context)
                textView.text = blog.description  // Set the full description
                textView.setPadding(16, 16, 16, 16)
                textView.textSize = 16f
                textView.setTextColor(context.resources.getColor(android.R.color.black))

                // Set the ScrollView to contain the TextView
                scrollView.addView(textView)

                // Create the AlertDialog
                val dialog = AlertDialog.Builder(context)
                    .setTitle(blog.title)  // Set the title to the blog's title
                    .setView(scrollView)   // Set the ScrollView as the view of the dialog
                    .setCancelable(true)    // Allow the dialog to be dismissed when clicked outside
                    .setNegativeButton("Close") { dialog, _ ->
                        dialog.dismiss()  // Close the dialog when "Close" is clicked
                    }
                    .create()

                dialog.show()  // Show the dialog
            }
        }
    }
}
