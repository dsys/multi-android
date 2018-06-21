package com.distributedsystems.multi

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.distributedsystems.multi.common.Preferences
import com.distributedsystems.multi.setup.SetupActivity
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.startActivity

class LandingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        MultiApp.get().getComponent().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_landing)

        if(needsSetup()) {
            startActivity<SetupActivity>()
        } else {
            startActivity<MainActivity>()
        }

        finish()
    }

    private fun needsSetup() : Boolean {
        val sharedPrefs = MultiApp.get().defaultSharedPreferences
        return !sharedPrefs.getBoolean(Preferences.PREF_SETUP_COMPLETE, false)
    }

}