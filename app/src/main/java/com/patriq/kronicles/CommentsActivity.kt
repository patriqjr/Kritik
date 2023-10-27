package com.patriq.kronicles

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.patrick.kronicles.Model.User
import com.patriq.kronicles.adapter.CommentsAdapter
import com.patriq.kronicles.databinding.ActivityCommentsBinding
import com.patriq.kronicles.databinding.ImagesItemLayoutBinding
import com.patriq.kronicles.model.Comment
import com.squareup.picasso.Picasso
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions

class CommentsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCommentsBinding
    private var postId = ""
    private var publisherId = ""
    private var firebaseUser: FirebaseUser? = null
    private var commentsAdapter: CommentsAdapter? = null
    private var commentList: MutableList<Comment>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            super.onCreate(savedInstanceState)
            binding = ActivityCommentsBinding.inflate(layoutInflater)
            setContentView(binding.root)
            val intent = intent
            postId = intent.getStringExtra("postId")!!
            publisherId = intent.getStringExtra("publisherId")!!
            firebaseUser = FirebaseAuth.getInstance().currentUser
            val recyclerView: RecyclerView = findViewById(R.id.recycler_view_comments)
            val linearLayoutManager = LinearLayoutManager(this)
            linearLayoutManager.reverseLayout = false
            recyclerView.layoutManager = linearLayoutManager
            commentList = ArrayList()
            commentsAdapter = CommentsAdapter(this, commentList)
            recyclerView.adapter = commentsAdapter
            readComments()
            userInfo()
            getPostImage()

            val emojiIcon =
                EmojIconActions(this, recyclerView, binding.addComment, binding.emojiconIcon)
            emojiIcon.ShowEmojIcon()

            binding.postComment.setOnClickListener {
                if (binding.addComment.text.toString() == "") {
                    Toast.makeText(
                        this@CommentsActivity,
                        "Unable to read your mind. Please type comment",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    addComment()
                }
            }
            ImagesItemLayoutBinding.inflate(layoutInflater).postImage.setOnClickListener {
                val intentComment = Intent(this, ImageFullscreenActivity::class.java)
                intentComment.putExtra("postId", postId)
                intentComment.putExtra("publisherId", publisherId)
                this.startActivity(intentComment)
                Animatoo.animateSlideLeft(this)
            }
            binding.closeComments.setOnClickListener {
                finish()
                Animatoo.animateSlideDown(this)

            }
        } catch (e: Exception) {
        }
    }

    private fun addComment() {
        try {
            val commentsRef = FirebaseDatabase.getInstance().reference.child("Comments")
                .child(postId)
            val commentsMap = HashMap<String, Any>()
            commentsMap["comment"] = binding.addComment.text.toString().trim()
            commentsMap["publisher"] = firebaseUser!!.uid
            commentsMap["post"] = postId
            val hashmapKey = commentsRef.push().key
            commentsMap["hashmapKey"] = hashmapKey.toString()
            commentsRef.child(hashmapKey!!).setValue(commentsMap)

            binding.addComment!!.text!!.clear()
        } catch (e: Exception) {
        }
    }

    private fun userInfo() {
        try {
            val usersRef =
                FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
            usersRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()) {
                        val user = p0.getValue<User>(User::class.java)
                        Picasso.get().load(user!!.getimage()).placeholder(R.mipmap.profile)
                            .into(binding.profileImageComment)
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                }
            })
        } catch (e: Exception) {
        }
    }

    private fun getPostImage() {
        try {
            val postRef = FirebaseDatabase.getInstance().reference.child("Posts")
                .child(postId).child("postimage")
            postRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()) {
                        val image = p0.value.toString()
                        Picasso.get().load(image).placeholder(R.mipmap.profile)
                            .into(binding.postImageComment)
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                }
            })
        } catch (e: Exception) {
        }
    }

    private fun readComments() {
        try {
            val commentsRef = FirebaseDatabase.getInstance().reference.child("Comments")
                .child(postId)

            commentsRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()) {
                        commentList!!.clear()

                        for (snapshot in p0.children) {
                            val comment = snapshot.getValue(Comment::class.java)
                            commentList!!.add(comment!!)
                        }

                        commentsAdapter!!.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                }
            })
        } catch (e: Exception) {
        }
    }
}
