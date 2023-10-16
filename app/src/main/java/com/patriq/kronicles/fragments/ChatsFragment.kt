package com.patriq.kronicles.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import com.patrick.kronicles.Model.User
import com.patriq.kronicles.adapter.UserAdapter
import com.patriq.kronicles.model.ChatList
import com.patriq.kronicles.notifications.Token
import com.patriq.kronicles.R

class ChatsFragment : Fragment() {

    private var userAdapter: UserAdapter? = null
    private var mUser: List<User>? = null
    private var usersChatList: List<ChatList>? = null
    private var firebaseUser: FirebaseUser? = null
    lateinit var recyclerViewChatList: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        try {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chats, container, false)
        recyclerViewChatList = view.findViewById(R.id.recycler_chatsList)
        recyclerViewChatList.setHasFixedSize(true)
        recyclerViewChatList.layoutManager = LinearLayoutManager(context)
        firebaseUser = FirebaseAuth.getInstance().currentUser
        usersChatList = ArrayList()
        val ref = FirebaseDatabase.getInstance().reference.child("ChatList")
            .child(firebaseUser!!.uid)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                (usersChatList as ArrayList).clear()
                for (dataSnapshot in p0.children) {
                    val chatList = dataSnapshot.getValue(ChatList::class.java)!!
                    (usersChatList as ArrayList).add(chatList)
                }
                retrieveChatList()
            }
        })
        updateToken(FirebaseInstanceId.getInstance().token)
//        }
//        catch (e: Exception){
//        }
        return view
    }


    private fun updateToken(token: String?) {
//        try {
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1 = Token(token!!)
        ref.child(firebaseUser!!.uid).setValue(token1)
//        }
//        catch (e: Exception){
//        }
    }

    private fun retrieveChatList() {
        try {
            mUser = ArrayList()
            val ref = FirebaseDatabase.getInstance().reference.child("Users")
            ref.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    try {
                        (mUser as ArrayList).clear()
                        for (dataSnapshot in p0.children) {
                            val user = dataSnapshot.getValue(User::class.java)
                            for (eachChatList in usersChatList!!) {
                                if (user!!.getuid() == eachChatList.getId()) {
                                    (mUser as ArrayList).add(user)
                                }
                            }
                        }
                        userAdapter = UserAdapter(context!!, (mUser as ArrayList<User>), false)
                        recyclerViewChatList.adapter = userAdapter
                    } catch (e: KotlinNullPointerException) {

                    }

                }
            })
        } catch (e: Exception) {

        }

    }
}
