package com.example.bookk.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.bookk.model.BlogModel
import com.google.firebase.database.*

class BlogViewModel : ViewModel() {

    private val db: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val _blogList = MutableLiveData<List<BlogModel>>()
    val blogList: LiveData<List<BlogModel>> get() = _blogList

    init {
        fetchBlogs()
    }

    private fun fetchBlogs() {
        db.child("blogs").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val blogList = mutableListOf<BlogModel>()
                for (snapshot in dataSnapshot.children) {
                    val blog = snapshot.getValue(BlogModel::class.java)
                    blog?.let { blogList.add(it) }
                }
                _blogList.value = blogList
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error
            }
        })
    }
}
