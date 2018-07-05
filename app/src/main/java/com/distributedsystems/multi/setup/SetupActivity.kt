package com.distributedsystems.multi.setup

import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.distributedsystems.multi.MultiApp
import com.distributedsystems.multi.R
import com.distributedsystems.multi.common.*
import com.distributedsystems.multi.setup.steps.IntroFragment
import javax.inject.Inject

class SetupActivity : AppCompatActivity() {

    @Inject
    internal lateinit var viewModelFactory : GenericViewModelFactory<SetupViewModel>
    private lateinit var viewModel : SetupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        MultiApp.get().getComponent().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)
        viewModel = getViewModel(SetupViewModel::class.java, viewModelFactory)

        if(savedInstanceState == null) {
            replaceFragment(R.id.main_fragment_holder, IntroFragment.newInstance())
        }
    }

    override fun onBackPressed() {
        if(fragmentManager.backStackEntryCount > 0) {
            fragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }

    }

    fun addAndReplaceFragment(fragment: Fragment) {
        addAndReplaceFragment(R.id.main_fragment_holder, fragment)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if(event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if(v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if(!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                            .hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

}