package com.distributedsystems.multi.setup

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.distributedsystems.multi.R
import com.distributedsystems.multi.common.addAndReplaceFragment
import com.distributedsystems.multi.common.getViewModel
import com.distributedsystems.multi.common.replaceFragment
import com.distributedsystems.multi.setup.steps.IntroFragment

class SetupActivity : AppCompatActivity() {

    private lateinit var viewModel : SetupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)
        viewModel = getViewModel(SetupViewModel::class.java)

        if(savedInstanceState == null) {
            replaceFragment(R.id.main_fragment_holder, IntroFragment())
        }
    }

    override fun onBackPressed() {
        if(fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }

    }

    fun replaceFragment(fragment: Fragment) {
        replaceFragment(R.id.main_fragment_holder, fragment)
    }

    fun addAndReplaceFragment(fragment: Fragment) {
        addAndReplaceFragment(R.id.main_fragment_holder, fragment)
    }

}