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
import com.example.bookk.R
import com.example.bookk.model.BlogModel

class BlogAdapter(
    private val context: Context,
    private val isInBlogFragment: Boolean,
    private val deleteBlog: (String) -> Unit, // Expecting a String (ID) for delete
    private val updateBlog: (BlogModel) -> Unit // Expecting BlogModel for update (not just the ID)
) : RecyclerView.Adapter<BlogAdapter.BlogViewHolder>() {

    private var blogList = listOf<BlogModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlogViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.blog_item, parent, false)
        return BlogViewHolder(view)
    }

    override fun onBindViewHolder(holder: BlogViewHolder, position: Int) {
        val blog = blogList[position]
        holder.title.text = blog.title
        holder.preview.text = blog.preview

        if (isInBlogFragment) {
            holder.updateButton.visibility = View.VISIBLE
            holder.deleteButton.visibility = View.VISIBLE

            holder.deleteButton.setOnClickListener {
                deleteBlog(blog.id)  // Pass blog.id as a String to delete
            }

            holder.updateButton.setOnClickListener {
                updateBlog(blog)  // Pass the entire BlogModel for update
            }
        } else {
            holder.updateButton.visibility = View.GONE
            holder.deleteButton.visibility = View.GONE
        }

        holder.readMoreButton.setOnClickListener {
            showFullBlog(blog)
        }
    }

    override fun getItemCount(): Int {
        return blogList.size
    }

    fun submitList(list: List<BlogModel>) {
        blogList = list
        notifyDataSetChanged()
    }

    private fun showFullBlog(blog: BlogModel) {
        val scrollView = ScrollView(context)
        val textView = TextView(context)
        textView.text = blog.description
        textView.setPadding(32, 32, 32, 32)
        textView.textSize = 16f
        scrollView.addView(textView)

        AlertDialog.Builder(context)
            .setTitle(blog.title)
            .setView(scrollView)
            .setNegativeButton("Close") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    inner class BlogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.blogTitle)
        val preview: TextView = itemView.findViewById(R.id.blogPreview)
        val readMoreButton: Button = itemView.findViewById(R.id.readMoreBtn)
        val updateButton: Button = itemView.findViewById(R.id.updateBtn)
        val deleteButton: Button = itemView.findViewById(R.id.deleteBtn)
    }
}

