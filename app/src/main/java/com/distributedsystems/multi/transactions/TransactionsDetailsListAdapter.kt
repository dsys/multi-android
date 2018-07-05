package com.distributedsystems.multi.transactions

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.distributedsystems.multi.R

class TransactionsDetailsListAdapter (
        private val transactionsDetailList: List<TransactionDetailsListItem>,
        private val context: Context
) : RecyclerView.Adapter<TransactionsDetailsListAdapter.TransactionDetailHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionsDetailsListAdapter.TransactionDetailHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.transaction_detail_list_item, parent, false)
        return TransactionDetailHolder(v)
    }

    override fun getItemCount(): Int = transactionsDetailList.size

    override fun onBindViewHolder(holder: TransactionDetailHolder, position: Int) {
        if(transactionsDetailList[position].icon != null) {
            if (transactionsDetailList[position].iconColor!! > 0)
                holder.txDetailIcon.imageTintList = ContextCompat.getColorStateList(context, transactionsDetailList[position].iconColor!!)
            holder.txDetailIcon.background = ContextCompat.getDrawable(context, R.drawable.bg_circle)
            holder.txDetailIcon.setImageDrawable(transactionsDetailList[position].icon)
            holder.txDetailIcon.backgroundTintList = ContextCompat.getColorStateList(context, transactionsDetailList[position].iconBackground!!)
        }
        holder.txDetailTitle.text = transactionsDetailList[position].title
        holder.txDetailValue.text = transactionsDetailList[position].value
    }

    class TransactionDetailHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val txDetailIcon : ImageView = view.findViewById(R.id.listItemIcon)
        val txDetailTitle : TextView = view.findViewById(R.id.listItemTitle)
        val txDetailValue : TextView = view.findViewById(R.id.listItemValue)
    }

}
