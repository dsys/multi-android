package com.distributedsystems.multi.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule(private val app: Application) {

    companion object {
        const val PREF_WALLET = "prefs_wallet"
    }

    @Provides
    @Singleton
    fun provideContext() : Context = app

    @Provides
    @Singleton
    fun providesWalletSharedPreferences(context: Context) : SharedPreferences {
        return context.getSharedPreferences(PREF_WALLET, Context.MODE_PRIVATE)
    }
}