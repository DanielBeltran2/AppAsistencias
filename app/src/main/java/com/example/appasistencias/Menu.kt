package com.example.appasistencias

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class Menu : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)




        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigationView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected)
    }

    private fun onNavigationItemSelected(item: MenuItem): Boolean {

        val fragment: Fragment = when (item.itemId) {
            R.id.buscar -> {

                buscar.newInstance()
            }
            R.id.tomarlista -> {

                capturar.newInstance()
            }
            R.id.modificar -> {

                modificar.newInstance()
            }
            else -> return false
        }

        openFragment(fragment)
        return true
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}