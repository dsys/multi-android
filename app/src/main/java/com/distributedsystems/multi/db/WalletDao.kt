package com.distributedsystems.multi.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy.REPLACE
import android.arch.persistence.room.Query
import io.reactivex.Flowable

@Dao
interface WalletDao {

    @Query("SELECT * FROM wallets")
    fun getAll() : Flowable<List<Wallet>>

    @Query("SELECT * FROM wallets WHERE id = :id")
    fun getWallet(id: Long) : Flowable<Wallet>

    @Insert(onConflict = REPLACE)
    fun insert(wallet: Wallet) : Long

}