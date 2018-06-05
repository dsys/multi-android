package com.distributedsystems.multi.common

import android.arch.lifecycle.*
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity

internal fun <T: ViewModel> Fragment.getViewModel(modelClass: Class<T>) : T {
    return ViewModelProviders.of(this.activity!!).get(modelClass)
}

internal fun <T: ViewModel> AppCompatActivity.getViewModel(modelClass: Class<T>) : T {
    return ViewModelProviders.of(this).get(modelClass)
}

internal fun AppCompatActivity.replaceFragment(holder: Int, fragment: Fragment) {
    this.supportFragmentManager
            .beginTransaction()
            .replace(holder, fragment)
            .commit()
}

internal fun AppCompatActivity.addAndReplaceFragment(holder: Int, fragment: Fragment) {
    this.supportFragmentManager
            .beginTransaction()
            .replace(holder, fragment)
            .addToBackStack(null)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
}