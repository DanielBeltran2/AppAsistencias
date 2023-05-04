package com.example.appasistencias

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.appasistencias.databinding.ActivityMenuBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class Menu : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_menu)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        val mOnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.modificar -> {
                        val intent1 = Intent(this, ModificarActivity::class.java)
                        startActivity(intent1)
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.tomarlista -> {
                        val intent2 = Intent(this, AgregarActivity::class.java)
                        startActivity(intent2)
                        return@OnNavigationItemSelectedListener true
                    }
                    R.id.buscar -> {
                        val intent3 = Intent(this, BuscarActivity::class.java)
                        startActivity(intent3)
                        return@OnNavigationItemSelectedListener true
                    }
                }
                false
            }




        navView.setupWithNavController(navController)
    }
}