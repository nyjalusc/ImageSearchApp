package com.codinginflow.imagesearchapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import dagger.hilt.android.AndroidEntryPoint
import java.security.AccessController

/**
 * @AndroidEntryPoint annotation is used for performing DI in Android components such as
 * Actvities, Fragments, Services and Broadcast Receivers.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // findNavController() would work in other fragments and activities but not if the layout
        // contains FragmentContainerView, that is the case here. Getting ref to NavController is
        // a little different in that case. First find the Fragment attached to FragmentContainerView
        // and cast it to NavHostFragment. Use this fragment to access NavController
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_main) as NavHostFragment
        navController = navHostFragment.findNavController()

        val appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    // If the navController.navigateUp() returns false that means the intended navigation is outside the graph.
    // If false is returned we let the system handle it.
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}