package com.distributedsystems.multi.transactions

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.Rx2Apollo
import com.distributedsystems.multi.MultiApp
import com.distributedsystems.multi.TransactionFeedQuery
import com.distributedsystems.multi.common.Preferences
import com.distributedsystems.multi.db.Wallet
import com.distributedsystems.multi.db.WalletDao
import com.distributedsystems.multi.networking.scalars.EthereumAddressString
import com.distributedsystems.multi.type.ETHEREUM_NETWORK
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.toast
import javax.inject.Inject

class TransactionsViewModel @Inject constructor(
        private val apolloClient: ApolloClient,
        private val walletDb: WalletDao
) : ViewModel() {

    companion object {
        private val LOG_TAG = TransactionsViewModel::class.java.simpleName
    }

    private val transactions = MutableLiveData<List<TransactionFeedQuery.Transaction>>()
    private val wallet = MutableLiveData<Wallet>()
    private var currentTransaction : TransactionFeedQuery.Transaction? = null
    private val compositeDisposable = CompositeDisposable()

    fun fetchTransactions() {
        val hash = "0x9f6e254cb81056ebedfd81ce0110ba61c1de7154" //wallet.value?.ethAddress!!
        val transactionFeedQuery = TransactionFeedQuery.builder()
                .address(EthereumAddressString(hash))
                .network(ETHEREUM_NETWORK.ROPSTEN)
                .build()

        val apolloCall = apolloClient.query(transactionFeedQuery)
        val disposable = Rx2Apollo.from(apolloCall)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    if (!it.hasErrors()) {
                        transactions.postValue(it.data()!!.ethereumAddress()!!.transactions()!!.reversed())
                    } else {
                        Log.e(LOG_TAG, it.errors().first().message())
                        MultiApp.get().toast("Unable to load transactions")
                    }
                }, {
                    Log.e(LOG_TAG, it.localizedMessage)
                    MultiApp.get().toast("Unable to load transactions")
                })

        compositeDisposable.add(disposable)
    }

    fun getDefaultWallet() {
        val defaultWalletId = MultiApp.get().defaultSharedPreferences
                .getLong(Preferences.PREF_DEFAULT_WALLET_ID, 0)
        val disposable = walletDb.getWallet(defaultWalletId)
                .subscribeOn(Schedulers.io())
                .subscribe({
                    wallet.postValue(it)
                }, {
                    MultiApp.get().toast("Unable to load wallet")
                })

        compositeDisposable.add(disposable)
    }

    fun getTransactions() : MutableLiveData<List<TransactionFeedQuery.Transaction>> {
        return transactions
    }

    fun getWallet() : MutableLiveData<Wallet> {
        return wallet
    }

    fun setCurrentTransaction(transaction: TransactionFeedQuery.Transaction?) {
        currentTransaction = transaction
    }

    fun getCurrentTransaction() : TransactionFeedQuery.Transaction? {
        return currentTransaction
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}