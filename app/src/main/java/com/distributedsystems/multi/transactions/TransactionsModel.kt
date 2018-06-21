package com.distributedsystems.multi.transactions

import com.distributedsystems.multi.TransactionFeedQuery
import com.distributedsystems.multi.db.Wallet

data class TransactionsModel(
        var currentWallet: Wallet? = null,
        val transactions : Map<Long, List<TransactionFeedQuery.Transaction>>? = emptyMap()
)