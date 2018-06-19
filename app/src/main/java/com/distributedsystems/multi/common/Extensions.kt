package com.distributedsystems.multi.common

import android.arch.lifecycle.*
import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.TypedValue
import android.view.inputmethod.InputMethodManager

internal fun <T: ViewModel> Fragment.getViewModel(modelClass: Class<T>, viewModelFactory: ViewModelProvider.Factory) : T {
    return ViewModelProviders.of(this.activity!!, viewModelFactory).get(modelClass)
}

internal fun <T: ViewModel> AppCompatActivity.getViewModel(modelClass: Class<T>, viewModelFactory: ViewModelProvider.Factory) : T {
    return ViewModelProviders.of(this, viewModelFactory).get(modelClass)
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

internal fun Fragment.getColor(id: Int) : Int = ContextCompat.getColor(this.context!!, id)

internal fun Fragment.getInputMethodManager() : InputMethodManager? =
        activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

internal fun Fragment.toDp(dp: Int) : Int {
    val r = resources
    return Math.round(TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            r.displayMetrics
    ))

}
