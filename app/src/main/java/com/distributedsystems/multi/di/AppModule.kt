package com.distributedsystems.multi.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.distributedsystems.multi.db.MultiDatabase
import com.distributedsystems.multi.db.WalletDao
import dagger.Module
import dagger.Provides
import org.jetbrains.anko.defaultSharedPreferences
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
        return context.defaultSharedPreferences
    }

    @Provides
    @Singleton
    fun providesWalletDatabase(context: Context) : WalletDao {
        return MultiDatabase.getInstance(context).walletDao()
    }

}