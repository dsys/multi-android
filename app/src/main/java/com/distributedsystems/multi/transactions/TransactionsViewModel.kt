package com.distributedsystems.multi.transactions

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.support.v4.content.ContextCompat
import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.Rx2Apollo
import com.distributedsystems.multi.MultiApp
import com.distributedsystems.multi.R
import com.distributedsystems.multi.TransactionFeedQuery
import com.distributedsystems.multi.common.Preferences
import com.distributedsystems.multi.db.Wallet
import com.distributedsystems.multi.db.WalletDao
import com.distributedsystems.multi.networking.scalars.EthereumAddressString
import com.distributedsystems.multi.type.ETHEREUM_NETWORK
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.defaultSharedPreferences
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class TransactionsViewModel @Inject constructor(
        private val apolloClient: ApolloClient,
        private val walletDb: WalletDao
) : ViewModel() {

    companion object {
        private val LOG_TAG = TransactionsViewModel::class.java.simpleName
    }

    private val simpleDateFormat = SimpleDateFormat("EEE, MM/dd/yy h:mm:ss a", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    private val transactions = MutableLiveData<List<TransactionFeedQuery.Transaction>>()
    private val transactionsError = MutableLiveData<String>()
    private val wallet = MutableLiveData<Wallet>()
    private var currentTransaction : TransactionFeedQuery.Transaction? = null
    private val compositeDisposable = CompositeDisposable()

    fun fetchTransactions(refresh: Boolean) {
        if(refresh || transactions.value == null) {
            val hash = wallet.value?.ethAddress!!
            val transactionFeedQuery = TransactionFeedQuery.builder()
                    .address(EthereumAddressString(hash))
                    .network(ETHEREUM_NETWORK.ROPSTEN)
                    .build()

            val apolloCall = apolloClient.query(transactionFeedQuery)
            compositeDisposable.add(
                    Rx2Apollo.from(apolloCall)
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        if (!it.hasErrors()) {
                            transactions.postValue(it.data()!!.ethereumAddress()!!.transactions()!!.reversed())
                        } else {
                            Log.e(LOG_TAG, it.errors().first().message())
                            transactionsError.postValue("Unable to load transactions")
                        }
                    }, {
                        Log.e(LOG_TAG, it.localizedMessage)
                        transactionsError.postValue("Unable to load transactions")
                    })
            )

        }
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

    fun getTransactionDetailsList() : List<TransactionDetailsListItem> {
        val ctx = MultiApp.get()
        val list : MutableList<TransactionDetailsListItem> = mutableListOf()

        if(currentTransaction != null) {
            val incomingTx = currentTransaction?.to()?.hex()?.value!! == wallet.value?.ethAddress!!
            val currentTxHash = currentTransaction?.hash()?.value!!
            val displayHash = if(incomingTx) {
                currentTransaction?.from()?.hex()?.value!!
            } else {
                currentTransaction?.to()?.hex()?.value!!
            }

            list.add(TransactionDetailsListItem.Builder().apply {
                if(incomingTx) {
                    setIcon(ContextCompat.getDrawable(ctx, R.drawable.ic_transaction_in)!!)
                    setIconBackground(R.color.bgTransactionIn)
                    setTitle(ctx.getString(R.string.incoming_transaction))
                } else {
                    setIcon(ContextCompat.getDrawable(ctx, R.drawable.ic_transaction_out)!!)
                    setIconBackground(R.color.bgTransactionOut)
                    setTitle(ctx.getString(R.string.outgoing_transaction))
                }
                setVal(currentTransaction?.value()?.display()!!)
            }.build())
            list.add(TransactionDetailsListItem.Builder()
                    .setIcon(ContextCompat.getDrawable(ctx, R.drawable.ic_person)!!)
                    .setIconColor(R.color.colorPrimaryDark)
                    .setIconBackground(R.color.colorPrimary)
                    .setTitle(ctx.getString(if(incomingTx) R.string.sender else R.string.recipient))
                    .setVal(ctx.getString(R.string.wallet_address_subtext,
                            displayHash.substring(displayHash.length - 20 until displayHash.length)).toUpperCase())
                    .build())
            list.add(TransactionDetailsListItem.Builder()
                    .setIcon(ContextCompat.getDrawable(ctx, R.drawable.ic_hash)!!)
                    .setIconColor(R.color.darkYellow)
                    .setIconBackground(R.color.pressedYellow)
                    .setTitle(ctx.getString(R.string.transaction_hash))
                    .setVal(ctx.getString(R.string.wallet_address_subtext,
                            currentTxHash.substring(currentTxHash.length - 20 until currentTxHash.length)).toUpperCase())
                    .build())
            list.add(TransactionDetailsListItem.Builder()
                    .setIcon(ContextCompat.getDrawable(ctx, R.drawable.ic_block)!!)
                    .setIconColor(R.color.darkGrey)
                    .setIconBackground(R.color.lightGrey)
                    .setTitle(ctx.getString(R.string.block_number))
                    .setVal(currentTransaction?.block()?.number().toString())
                    .build())
            list.add(TransactionDetailsListItem.Builder()
                    .setIcon(ContextCompat.getDrawable(ctx, R.drawable.ic_clock)!!)
                    .setIconColor(R.color.darkTeal)
                    .setIconBackground(R.color.paleTeal)
                    .setTitle(ctx.getString(R.string.timestamp))
                    .setVal(simpleDateFormat.format(currentTransaction?.block()?.timestamp()!!*1000))
                    .build())
            list.add(TransactionDetailsListItem.Builder()
                    .setIcon(ContextCompat.getDrawable(ctx, R.drawable.ic_gas)!!)
                    .setIconColor(R.color.darkRed)
                    .setIconBackground(R.color.lightRed)
                    .setTitle(ctx.getString(R.string.gas))
                    .setVal("${currentTransaction?.gasUsed()!!}/${currentTransaction?.gas()} @ ${currentTransaction?.gasPrice()?.display()}")
                    .build())
            list.add(TransactionDetailsListItem.Builder()
                    .setTitle(ctx.getString(R.string.gas_used))
                    .setVal(currentTransaction?.gasUsed()?.toString()!!)
                    .build())
            list.add(TransactionDetailsListItem.Builder()
                    .setTitle(ctx.getString(R.string.gas_limit))
                    .setVal(currentTransaction?.gas()?.toString()!!)
                    .build())
            list.add(TransactionDetailsListItem.Builder()
                    .setTitle(ctx.getString(R.string.gas_price))
                    .setVal(currentTransaction?.gasPrice()?.display()!!)
                    .build())
        }

        return list
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

    fun getTransactionsError() : MutableLiveData<String> {
        return transactionsError
    }

    fun clearDisposable() {
        compositeDisposable.clear()
    }

    override fun onCleared() {
        super.onCleared()
        clearDisposable()
    }
}