package com.distributedsystems.multi

import android.app.Activity
import android.app.Application
import com.distributedsystems.multi.di.AppComponent
import com.distributedsystems.multi.di.AppModule
import com.distributedsystems.multi.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

class MultiApp : Application(), HasActivityInjector {

    companion object {
        private var me : MultiApp? = null
        fun get() : MultiApp = me!!
    }

    private lateinit var component: AppComponent

    @Inject lateinit var activityDispatchingAndroidInjector:
            DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()
        me = this

        component = DaggerAppComponent.builder()
                .application(this)
                .appModule(AppModule(this))
                .build()
                .apply {
                    inject(this@MultiApp)
                }

    }

    fun getComponent() : AppComponent {
        return component
    }

    override fun activityInjector(): AndroidInjector<Activity>? =
            activityDispatchingAndroidInjector


}