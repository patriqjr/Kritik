package com.patriq.kronicles.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Bitmap
import android.os.Bundle
import android.util.LruCache
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.applovin.sdk.AppLovinSdk
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.patriq.kronicles.adapter.PostAdapter
import com.patriq.kronicles.model.Post
import com.patriq.kronicles.R
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*

class HomeFragment : Fragment() {
    private var postAdapter: PostAdapter? = null
    private var postList: MutableList<Post>? = null
    private var followingList: MutableList<Post>? = null
    private lateinit var memoryCache: LruCache<String, Bitmap>
    private var checker: String? = "0"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        try {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val cacheSize = maxMemory / 8
        memoryCache = object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                return bitmap.byteCount / 1024
            }
        }

        AppLovinSdk.initializeSdk(context)

        val recyclerView: RecyclerView?
        recyclerView = view.findViewById(R.id.recycler_view_home)
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager

        view.fab.setOnClickListener {
            recyclerView.smoothScrollToPosition(1000)
        }
//        checkFilter()

        postList = ArrayList()
        postAdapter = PostAdapter(requireContext(), postList as ArrayList<Post>)
        recyclerView.adapter = postAdapter
        checkFollowings()
        retrievePosts()


        view.filter.setOnClickListener {
            val options = arrayOf<CharSequence>(
                "All Posts",
                "Art\uD83D\uDDBC",
                "Cars\uD83D\uDE97",
                "Fashion/Beauty\uD83D\uDC85\uD83C\uDFFE",
                "Food\uD83E\uDD58",
                "Mind Blowing\uD83E\uDD2F",
                "Question\uD83E\uDD14",
                "Inspirational\uD83C\uDF1E",
                "LOL\uD83D\uDE00",
                "Wallpapers\uD83D\uDD25",
                "Nature\uD83C\uDF38"
            )
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setTitle("Filter Category")
            builder.setItems(options, DialogInterface.OnClickListener { _, which ->
                when (which) {
                    0 -> {
                        retrievePosts()
                        checker = "0"
                        posts_category.text = "all"
                    }
                    1 -> {
                        retrieveArtPosts()
                        checker = "1"
                        posts_category.text = "Art\uD83D\uDDBC"

                    }
                    2 -> {
                        retrieveCarPosts()
                        checker = "2"
                        posts_category.text = "Cars\uD83D\uDE97"
                    }
                    3 -> {
                        retrieveFBPosts()
                        checker = "3"
                        posts_category.text = "Fashion/Beauty\uD83D\uDC85\uD83C\uDFFE"

                    }
                    4 -> {
                        retrieveFoodPosts()
                        checker = "4"
                        posts_category.text = "Food\uD83E\uDD58"

                    }
                    5 -> {
                        retrieveMBPosts()
                        checker = "5"
                        posts_category.text = "Mind Blowing\uD83E\uDD2F"

                    }
                    6 -> {
                        retrieveQuestPosts()
                        checker = "6"
                        posts_category.text = "Question\uD83E\uDD14"

                    }
                    7 -> {
                        retrieveInspirePosts()
                        checker = "7"
                        posts_category.text = "Inspirational\uD83C\uDF1E"

                    }
                    8 -> {
                        retrieveLOLPosts()
                        checker = "8"
                        posts_category.text = "LOL\uD83D\uDE00"
                    }
                    9 -> {
                        retrieveWallpaperPosts()
                        checker = "9"
                        posts_category.text = "Wallpapers\uD83D\uDD25"

                    }
                    10 -> {
                        retrieveNaturePosts()
                        checker = "10"
                        posts_category.text = "Nature\uD83C\uDF38"

                    }
                }

            })
            builder.show()
        }
//        }
//        catch (e: Exception){
//        }
        return view
    }


    private fun checkFollowings() {
        try {
            followingList = ArrayList()
            val followingRef = FirebaseDatabase.getInstance().reference
                .child("Follow").child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child("Following")
            followingRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    if (p0.exists()) {
                        (followingList as ArrayList<String>).clear()
                        for (snapshot in p0.children) {
                            snapshot.key.let { (followingList as ArrayList<String>).add(it!!) }
                        }
//                        retrievePosts()
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                }
            })
        } catch (e: Exception) {
        }
    }

    private fun retrievePosts() {
        try {
            val postRef = FirebaseDatabase.getInstance().reference.child("Posts")
            postRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    postList?.clear()
                    for (snapshot in p0.children) {
                        val post = snapshot.getValue(Post::class.java)
                        for (id in (followingList as ArrayList<String>)) {
                            if (post!!.getpublisher() == id) {
                                postList!!.add(post)
                            }
                            postAdapter!!.notifyDataSetChanged()
                        }
                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        } catch (e: Exception) {

        }
    }

    private fun retrieveArtPosts() {
        try {

            val postRef = FirebaseDatabase.getInstance().reference.child("Posts")

            postRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    postList?.clear()
                    for (snapshot in p0.children) {
                        val post = snapshot.getValue(Post::class.java)
                        for (id in (followingList as ArrayList<String>)) {
                            if (post!!.getpublisher() == id && post.getCategory() == "Art\uD83D\uDDBC") {
                                postList!!.add(post)
                            }
                            postAdapter!!.notifyDataSetChanged()
                        }
                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        } catch (e: Exception) {

        }
    }

    private fun retrieveCarPosts() {
        try {

            val postRef = FirebaseDatabase.getInstance().reference.child("Posts")

            postRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    postList?.clear()
                    for (snapshot in p0.children) {
                        val post = snapshot.getValue(Post::class.java)
                        for (id in (followingList as ArrayList<String>)) {
                            if (post!!.getpublisher() == id && post.getCategory() == "Cars\uD83D\uDE97") {
                                postList!!.add(post)
                            }
                            postAdapter!!.notifyDataSetChanged()
                        }
                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        } catch (e: Exception) {

        }
    }

    private fun retrieveLOLPosts() {
        try {

            val postRef = FirebaseDatabase.getInstance().reference.child("Posts")

            postRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    postList?.clear()
                    for (snapshot in p0.children) {
                        val post = snapshot.getValue(Post::class.java)
                        for (id in (followingList as ArrayList<String>)) {
                            if (post!!.getpublisher() == id && post.getCategory() == "LOL\uD83D\uDE00") {
                                postList!!.add(post)
                            }
                            postAdapter!!.notifyDataSetChanged()
                        }
                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        } catch (e: Exception) {

        }
    }

    private fun retrieveFBPosts() {
        try {

            val postRef = FirebaseDatabase.getInstance().reference.child("Posts")

            postRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    postList?.clear()
                    for (snapshot in p0.children) {
                        val post = snapshot.getValue(Post::class.java)
                        for (id in (followingList as ArrayList<String>)) {
                            if (post!!.getpublisher() == id && post.getCategory() == "Fashion/Beauty\uD83D\uDC85\uD83C\uDFFE") {
                                postList!!.add(post)
                            }
                            postAdapter!!.notifyDataSetChanged()
                        }
                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        } catch (e: Exception) {

        }
    }

    private fun retrieveFoodPosts() {
        try {

            val postRef = FirebaseDatabase.getInstance().reference.child("Posts")

            postRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    postList?.clear()
                    for (snapshot in p0.children) {
                        val post = snapshot.getValue(Post::class.java)
                        for (id in (followingList as ArrayList<String>)) {
                            if (post!!.getpublisher() == id && post.getCategory() == "Food\uD83E\uDD58") {
                                postList!!.add(post)
                            }
                            postAdapter!!.notifyDataSetChanged()
                        }
                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        } catch (e: Exception) {

        }
    }

    private fun retrieveMBPosts() {
        try {

            val postRef = FirebaseDatabase.getInstance().reference.child("Posts")

            postRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    postList?.clear()
                    for (snapshot in p0.children) {
                        val post = snapshot.getValue(Post::class.java)
                        for (id in (followingList as ArrayList<String>)) {
                            if (post!!.getpublisher() == id && post.getCategory() == "Mind Blowing\uD83E\uDD2F") {
                                postList!!.add(post)
                            }
                            postAdapter!!.notifyDataSetChanged()
                        }
                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        } catch (e: Exception) {

        }
    }

    private fun retrieveQuestPosts() {
        try {

            val postRef = FirebaseDatabase.getInstance().reference.child("Posts")

            postRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    postList?.clear()
                    for (snapshot in p0.children) {
                        val post = snapshot.getValue(Post::class.java)
                        for (id in (followingList as ArrayList<String>)) {
                            if (post!!.getpublisher() == id && post.getCategory() == "Question\uD83E\uDD14") {
                                postList!!.add(post)
                            }
                            postAdapter!!.notifyDataSetChanged()
                        }
                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        } catch (e: Exception) {

        }
    }

    private fun retrieveInspirePosts() {
        try {
            val postRef = FirebaseDatabase.getInstance().reference.child("Posts")

            postRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    postList?.clear()
                    for (snapshot in p0.children) {
                        val post = snapshot.getValue(Post::class.java)
                        val cat = post!!.getCategory()
                        for (id in (followingList as ArrayList<String>)) {
                            if (post!!.getpublisher() == id && post.getCategory() == "Inspirational\uD83C\uDF1E") {
//                                Toast.makeText(context, "$cat", Toast.LENGTH_LONG).show()
                                postList!!.add(post)
                            }
                            postAdapter!!.notifyDataSetChanged()
                        }

                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        } catch (e: Exception) {

        }
    }

    private fun retrieveWallpaperPosts() {
        try {

            val postRef = FirebaseDatabase.getInstance().reference.child("Posts")

            postRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    postList?.clear()
                    for (snapshot in p0.children) {
                        val post = snapshot.getValue(Post::class.java)
                        for (id in (followingList as ArrayList<String>)) {
                            if (post!!.getpublisher() == id && post.getCategory() == "Wallpapers\uD83D\uDD25") {
                                postList!!.add(post)
                            }
                            postAdapter!!.notifyDataSetChanged()
                        }
                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        } catch (e: Exception) {

        }
    }

    private fun retrieveNaturePosts() {
        try {

            val postRef = FirebaseDatabase.getInstance().reference.child("Posts")

            postRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    postList?.clear()
                    for (snapshot in p0.children) {
                        val post = snapshot.getValue(Post::class.java)
                        for (id in (followingList as ArrayList<String>)) {
                            if (post!!.getpublisher() == id && post.getCategory() == "Nature\uD83C\uDF38") {
                                postList!!.add(post)
                            }
                            postAdapter!!.notifyDataSetChanged()
                        }
                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }
            })
        } catch (e: Exception) {

        }
    }
}
