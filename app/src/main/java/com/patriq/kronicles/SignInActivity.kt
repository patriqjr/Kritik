package com.patriq.kronicles

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.patrick.kronicles.Model.User
import com.patriq.kronicles.adapter.UserAdapter
import kotlinx.android.synthetic.main.activity_sign_in.*


class SignInActivity : AppCompatActivity() {

    private var recyclerView: RecyclerView? = null
    private var database: FirebaseDatabase? = FirebaseDatabase.getInstance()
    private var userAdapter: UserAdapter? = null
    private var mUser: MutableList<User>? = null
    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {

        if (database == null) {
            FirebaseDatabase.getInstance().apply { setPersistenceEnabled(true) }
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        signup_btn.setOnClickListener {
            startActivity(Intent(this, SplashActivity::class.java))
        }

        login_btn.setOnClickListener {
            loginUser()
        }

        resetPasswordBtn.setOnClickListener {
            val email = email_login.text.toString()
            if (email == "") {
                Toast.makeText(this, "no email provided", Toast.LENGTH_LONG).show()
            } else {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("reset password?")
                    .setMessage("your password will be reset and sent to you via email.\n\ndo you wish to proceed?")
                    .setPositiveButton("reset, please") { _, _ ->
                        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    Toast.makeText(
                                        this,
                                        "password has been reset. please check your email inbox",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        this,
                                        "there's a mistake somewhere. please try again",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                    }
                builder.setNegativeButton("i think i remember now") { _, _ ->
                }
                builder.setNeutralButton("") { _, _ ->
                }
                val dialog: AlertDialog = builder.create()
                dialog.setCanceledOnTouchOutside(true)
                dialog.show()
            }
        }
    }

    private fun loginUser() {
        try {
            val email = email_login.text.toString()
            val password = password_login.text.toString()

            when {
                TextUtils.isEmpty(email) -> Toast.makeText(
                    this,
                    "Provide your email",
                    Toast.LENGTH_LONG
                ).show()
                TextUtils.isEmpty(password) -> Toast.makeText(
                    this,
                    "Enter your password",
                    Toast.LENGTH_LONG
                ).show()
                else -> {
                    val prgDialog = ProgressDialog(this@SignInActivity)
                    prgDialog.setTitle("")
                    prgDialog.setMessage("Logging in")
                    prgDialog.setCanceledOnTouchOutside(false)
                    prgDialog.show()
                    val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                        if (it.isSuccessful) {
                            followEverybody()
                            prgDialog.dismiss()
                            val intent = Intent(this@SignInActivity, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            Animatoo.animateDiagonal(this)
                            finish()
                        } else {
                            val message = it.exception!!.toString()
                            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                            FirebaseAuth.getInstance().signOut()
                            prgDialog.dismiss()
                        }
                    }
                }
            }
        } catch (e: Exception) {

        }
        unfollowMuted()
    }

    override fun onStart() {
        try {
            if (database == null) {
                FirebaseDatabase.getInstance().apply { setPersistenceEnabled(true) }
            }
            if (FirebaseAuth.getInstance().currentUser?.uid != null) {
                followEverybody()
                unfollowMuted()
                val intent = Intent(this@SignInActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } else {
            }
            super.onStart()
        } catch (e: Exception) {
        }
    }

    private fun unfollowMuted() {
        try {
            val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
            val usersRef = FirebaseDatabase.getInstance().reference
                .child("Muted")
                .child(firebaseUser!!.uid)
            usersRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    for (snapshot in p0.children) {
                        try {
                            val user = snapshot.key.toString()
                            if (FirebaseAuth.getInstance().currentUser != null && firebaseUser.uid != null) {
                                FirebaseDatabase.getInstance().reference
                                    .child("Follow").child(firebaseUser.uid)
                                    .child("Following").child(user)
                                    .removeValue()
                            } else {
                                Toast.makeText(this@SignInActivity, ":(", Toast.LENGTH_LONG).show()
                            }
                            userAdapter?.notifyDataSetChanged()
                        } catch (e: DatabaseException) {
                        }
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        } catch (e: Exception) {
        }
    }

    private fun followEverybody() {
        try {
            val usersRef = FirebaseDatabase.getInstance().reference.child("Users")
            usersRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    for (snapshot in p0.children) {
                        val user = snapshot.getValue(User::class.java)!!
                        if (FirebaseAuth.getInstance().currentUser != null) {
                            firebaseUser?.uid.let {
                                FirebaseDatabase.getInstance().reference
                                    .child("Follow").child(it.toString())
                                    .child("Following").child(user.getuid())
                                    .setValue(true).addOnCompleteListener { task ->
                                        if (task.isSuccessful) {

                                        }
                                    }
                            }
                        } else {
                            Toast.makeText(this@SignInActivity, ":(", Toast.LENGTH_LONG).show()
                        }
                        userAdapter?.notifyDataSetChanged()
                    }
                    unfollowMuted()
                }

                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        } catch (e: Exception) {
        }
    }
}
