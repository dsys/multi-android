package com.distributedsystems.multi.di

import android.app.Application
import com.distributedsystems.multi.MultiApp
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidInjectionModule::class, AppModule::class, GraphModule::class])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application) : Builder

        fun appModule(appModule: AppModule) : Builder


        fun build() : AppComponent
    }

    fun inject(multiApp: MultiApp)

}