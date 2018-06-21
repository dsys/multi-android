package com.distributedsystems.multi.transactions

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.distributedsystems.multi.R
import com.distributedsystems.multi.TransactionFeedQuery
import com.distributedsystems.multi.db.Wallet

class TransactionsListAdapter(
        private val currentWallet: Wallet,
        private val transactionsList : List<TransactionFeedQuery.Transaction>,
        private val context: Context
) : RecyclerView.Adapter<TransactionsListAdapter.TransactionHolder>() {

    private lateinit var childOnClickListener : OnTransactionClickedListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.transaction_list_item, parent, false)
        return TransactionHolder(v)
    }

    override fun getItemCount(): Int {
        return transactionsList.size
    }

    override fun onBindViewHolder(holder: TransactionHolder, position: Int) {
        val transaction = transactionsList[position]
        val outboundTransaction = transaction.from()!!.hex().toString() == "0x9f6e254cb81056ebedfd81ce0110ba61c1de7154" //currentWallet.ethAddress
        val hash: String
        val directionIcon: Drawable?
        val directionIconBg: Drawable?

        if (outboundTransaction) {
            directionIcon = ContextCompat.getDrawable(context, R.drawable.ic_transaction_out)
            directionIconBg = ContextCompat.getDrawable(context, R.drawable.bg_transaction_out)
            hash = transaction.to()!!.hex().toString()
        } else {
            directionIcon = ContextCompat.getDrawable(context, R.drawable.ic_transaction_in)
            directionIconBg = ContextCompat.getDrawable(context, R.drawable.bg_transaction_in)
            hash = transaction.from()!!.hex().toString()
        }

        holder.transactionValue.text = transaction.value()!!.display()
        holder.transactionDirection.apply {
            setImageDrawable(directionIcon)
            background = directionIconBg
        }

        holder.transactionHash.text = context.getString(R.string.wallet_address_subtext,
                hash.substring(hash.length - 8 until hash.length))

        if(transaction.status()!!) {
            holder.transactionStatus.text = context.getString(R.string.success)
        } else {
            holder.transactionStatus.text = context.getString(R.string.declined)
        }

        holder.view.setOnClickListener {
            childOnClickListener.onTransactionClicked(transaction, position)
        }

    }

    fun setChildOnClickListener(clickListener: OnTransactionClickedListener) {
        this.childOnClickListener = clickListener
    }

    class TransactionHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val transactionDirection : ImageView = view.findViewById(R.id.direction_icon)
        val transactionHash : TextView = view.findViewById(R.id.from_to_address)
        val transactionStatus : TextView = view.findViewById(R.id.transaction_status)
        val transactionValue : TextView = view.findViewById(R.id.transaction_value)
    }

    interface OnTransactionClickedListener {
        /**
         * Invoked when a child in this list view is clicked.
         */
        fun onTransactionClicked(transaction: TransactionFeedQuery.Transaction, position: Int)
    }
}