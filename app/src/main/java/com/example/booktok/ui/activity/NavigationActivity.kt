package com.example.booktok.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.booktok.R
import com.example.booktok.databinding.ActivityNavigationBinding
import com.example.booktok.ui.fragment.BookDashboardFragment
import com.example.booktok.ui.fragment.SettingFragment


class NavigationActivity : AppCompatActivity() {

    lateinit var binding: ActivityNavigationBinding
    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager : FragmentManager = supportFragmentManager

        val fragmentTransaction : FragmentTransaction = fragmentManager.beginTransaction()

        fragmentTransaction.replace(R.id.frameLayout,fragment)
        fragmentTransaction.commit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityNavigationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        replaceFragment(BookDashboardFragment())

        binding.buttomNavigation.setOnItemSelectedListener {menu->
            when(menu.itemId){
                R.id.nav_home ->replaceFragment(BookDashboardFragment())
                R.id.nav_settings -> replaceFragment(SettingFragment())

                else -> {}
            }
            true
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
    }


}