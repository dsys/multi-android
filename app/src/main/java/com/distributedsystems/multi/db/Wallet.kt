package com.distributedsystems.multi.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "wallets")
data class Wallet (
        @PrimaryKey(autoGenerate = true) var id: Long?,
        @ColumnInfo(name = "private_key") var privateKey: String,
        @ColumnInfo(name = "public_key") var publicKey: String,
        @ColumnInfo(name = "eth_address") var ethAddress: String,
        @ColumnInfo(name = "name") var name: String
)
