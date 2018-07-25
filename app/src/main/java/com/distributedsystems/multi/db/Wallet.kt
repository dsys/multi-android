package com.distributedsystems.multi.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(tableName = "wallets")
data class Wallet (
        @PrimaryKey(autoGenerate = true) var id: Long?,
        @ColumnInfo(name = "ens_domain") var ensDomain : String?,
        @ColumnInfo(name = "user_name") var userName: String?,
        @ColumnInfo(name = "inserted_at") var insertedAt: Date?,
        @ColumnInfo(name = "private_key") var privateKey: String,
        @ColumnInfo(name = "public_key") var publicKey: String,
        @ColumnInfo(name = "eth_address") var ethAddress: String
)
