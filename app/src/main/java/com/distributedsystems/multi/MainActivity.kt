package com.distributedsystems.multi

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var currentFragment : Int = R.id.transactionsFragment
    private lateinit var navController : NavController
    private var navOptions = NavOptions.Builder()
            .setEnterAnim(R.anim.default_enter)
            .setExitAnim(R.anim.default_exit)
            .build()

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        if(item.itemId != currentFragment) {
            currentFragment = item.itemId
            when (item.itemId) {
                R.id.browseFragment -> {
                    navController.navigate(R.id.browseFragment, null, navOptions)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.tokenFragment -> {
                    navController.navigate(R.id.browseFragment, null, navOptions)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.transactionsFragment -> {
                    navController.navigate(R.id.transactionsFragment, null, navOptions)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.profileFragment -> {
                    navController.navigate(R.id.profileFragment, null, navOptions)
                    return@OnNavigationItemSelectedListener true
                }
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        MultiApp.get().getComponent().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navController = findNavController(R.id.nav_host_fragment)
        if(savedInstanceState == null) {
            navigation.selectedItemId = R.id.transactionsFragment
        }

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        setOnNavigationListener()
    }

    private fun setOnNavigationListener() =
            findNavController(R.id.nav_host_fragment).addOnNavigatedListener { _, destination ->
                navigation.selectedItemId = destination.id
            }

    override fun onSupportNavigateUp(): Boolean = findNavController(R.id.nav_host_fragment).navigateUp()

}