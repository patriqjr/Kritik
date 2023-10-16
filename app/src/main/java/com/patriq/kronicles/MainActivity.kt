package com.patriq.kronicles

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.applovin.sdk.AppLovinSdk
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.FirebaseDatabase
import com.patriq.kronicles.fragments.ChatsFragment
import com.patriq.kronicles.fragments.HomeFragment
import com.patriq.kronicles.fragments.ProfileFragment
import com.patriq.kronicles.fragments.SearchFragment


class MainActivity : AppCompatActivity() {

    private var database: FirebaseDatabase? = FirebaseDatabase.getInstance()


    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    moveToFragment(HomeFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_search -> {
                    moveToFragment(SearchFragment())
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_add_post -> {
                    startActivity(Intent(this@MainActivity, AddPostActivity::class.java))
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_chat -> {
                    moveToFragment(ChatsFragment())
//                startActivity(Intent(this@MainActivity, ChatsActivity::class.java))
                    return@OnNavigationItemSelectedListener true
                }
                R.id.nav_profile -> {
                    moveToFragment(ProfileFragment())
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        try {
            if (database == null) {
                FirebaseDatabase.getInstance().apply { setPersistenceEnabled(true) }
            }
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            AppLovinSdk.initializeSdk(this)

            val navView: BottomNavigationView = findViewById(R.id.nav_view)

            navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

            moveToFragment(HomeFragment())
        } catch (e: Exception) {

        }

    }

    private fun moveToFragment(fragment: Fragment) {
        try {
            val fragmentTrans = supportFragmentManager.beginTransaction()
            fragmentTrans.replace(R.id.fragment_container, fragment)
            fragmentTrans.commit()
        } catch (e: Exception) {
        }
    }
}
