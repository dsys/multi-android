package com.distributedsystems.multi.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query

@Dao
interface WalletDao {

    @Query("SELECT * FROM wallets")
    fun getAll() : List<Wallet>

    @Insert(onConflict = REPLACE)
    fun insert(wallet: Wallet)

}