package com.distributedsystems.multi.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = [Wallet::class], version = 1, exportSchema = false)
abstract class MultiDatabase : RoomDatabase() {

    abstract fun walletDao() : WalletDao

    companion object {
        @Volatile private var INSTANCE : MultiDatabase? = null

        fun getInstance(context: Context) : MultiDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext,
                        MultiDatabase::class.java, "Wallets.db")
                        .build()
    }

}