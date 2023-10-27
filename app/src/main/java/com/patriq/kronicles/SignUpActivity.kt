package com.patriq.kronicles

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.blogspot.atifsoftwares.animatoolib.Animatoo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.patriq.kronicles.databinding.ActivitySignUpBinding
import java.util.Locale


class SignUpActivity : AppCompatActivity() {
    private var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    private lateinit var binding: ActivitySignUpBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_sign_up)

        binding.signInBtn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            Animatoo.animateSlideDown(this)
        }
        binding.signUpBtn.setOnClickListener {
            createAccount()
        }
    }

    private fun createAccount() {
        val alias = binding.fullNameSignUp.text.toString().toLowerCase(Locale.ENGLISH)
        val email = binding.emailSignUp.text.toString()
        val password = binding.passwordSignUp.text.toString()

        when {
            TextUtils.isEmpty(alias) -> Toast.makeText(
                this,
                "Provide an alias please.",
                Toast.LENGTH_LONG
            ).show()

            TextUtils.isEmpty(email) -> Toast.makeText(
                this,
                "Provide an email for post ownership and storage. It will be kept anonymous.",
                Toast.LENGTH_LONG
            ).show()

            TextUtils.isEmpty(password) -> Toast.makeText(
                this,
                "Provide a password",
                Toast.LENGTH_LONG
            ).show()

            else -> {
                val prgDialog = ProgressDialog(this@SignUpActivity)
                prgDialog.setTitle("Creating account")
                prgDialog.setMessage("Sending verification email.")
                prgDialog.setCanceledOnTouchOutside(false)
                prgDialog.show()
                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
                mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            sendVerificationEmail()
                            val currentUser = FirebaseAuth.getInstance().currentUser
                            val uid = currentUser!!.uid
                            saveUserInfo(alias, email, prgDialog)
                            mAuth.signOut()
                            finish()
                        } else {
                            val message = it.exception!!.toString()
                            Toast.makeText(this, "$message", Toast.LENGTH_LONG).show()
//                            mAuth.signOut()
                            prgDialog.dismiss()
                        }
                    }
            }
        }
    }

    private fun saveUserInfo(alias: String, email: String, prgDialog: ProgressDialog) {
        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val usersRef: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")
        val userMap = HashMap<String, Any>()
        userMap["uid"] = currentUserID
        userMap["alias"] = alias
        userMap["email"] = email
        userMap["bio"] = "not sharing any info"
        userMap["image"] =
            "https://firebasestorage.googleapis.com/v0/b/kronicles-anon.appspot.com/o/Default%20Images%2Fprofile.png?alt=media&token=d468e9e6-3182-4ddc-ad0b-092b5f55d43b"

        usersRef.child(currentUserID).setValue(userMap)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    Animatoo.animateDiagonal(this)
                } else {
                    val message = it.exception!!.toString()
                    Toast.makeText(this, "$message", Toast.LENGTH_LONG).show()
                    FirebaseAuth.getInstance().signOut()
                    prgDialog.dismiss()
                }
            }

    }

    private fun sendVerificationEmail() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        firebaseUser?.sendEmailVerification()
            ?.addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Check inbox and verify email", Toast.LENGTH_LONG).show()

                } else {
                    Toast.makeText(this, "Couldn't send verification mail", Toast.LENGTH_LONG)
                        .show()
                }
            }
    }
}

