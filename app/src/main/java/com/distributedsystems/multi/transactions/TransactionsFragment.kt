package com.distributedsystems.multi.transactions

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.arch.lifecycle.Observer
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.navigation.fragment.findNavController
import com.distributedsystems.multi.MultiApp
import com.distributedsystems.multi.R
import com.distributedsystems.multi.TransactionFeedQuery
import com.distributedsystems.multi.common.GenericViewModelFactory
import com.distributedsystems.multi.common.getColor
import com.distributedsystems.multi.common.getViewModel
import com.distributedsystems.multi.common.toDp
import com.distributedsystems.multi.db.Wallet
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.component_card.*
import kotlinx.android.synthetic.main.fragment_transactions.*
import net.glxn.qrgen.android.QRCode
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class TransactionsFragment : Fragment() {

    companion object {
        fun newInstance() : TransactionsFragment = TransactionsFragment()
        private val LOG_TAG = TransactionsFragment::class.java.simpleName
    }

    @Inject
    internal lateinit var viewModelFactory : GenericViewModelFactory<TransactionsViewModel>
    private lateinit var viewModel : TransactionsViewModel
    private var wallet : Wallet? = null
    private var disposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_transactions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        MultiApp.get().getComponent().inject(this)
        super.onViewCreated(view, savedInstanceState)
        viewModel = getViewModel(TransactionsViewModel::class.java, viewModelFactory)

        transactions_list.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
        renderWalletDetails()
        setOnAddressLongPress()
        setAppBarStateChangeListener()
        observeTransactions()
        renderWalletDetails()
        setOnSwipeRefreshListener()
    }


    override fun onStop() {
        super.onStop()
        disposable.clear()
    }

    private fun setAppBarStateChangeListener() {
        appbar.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            val rotationOffsetDivider = appBarLayout.totalScrollRange/45f
            val expansionOffsetDivider = appBarLayout.totalScrollRange/16f

            wallet_holder.rotationX = (verticalOffset/rotationOffsetDivider)

            val lp = list_holder.layoutParams as CoordinatorLayout.LayoutParams
            val titleLp = list_title.layoutParams as LinearLayout.LayoutParams
            val gutter = Math.round(16 - (Math.abs(verticalOffset)/expansionOffsetDivider))
            list_holder.layoutParams = lp.apply {
                    marginStart = toDp(gutter)
                    marginEnd = toDp(gutter)
            }
            list_title.layoutParams = titleLp.apply {
                marginStart = toDp(gutter)
                marginEnd = toDp(gutter)
            }

            list_title.setPadding(toDp(16-gutter), toDp(16), toDp(16-gutter), toDp(16))

            if(Math.abs(verticalOffset) >= appBarLayout.totalScrollRange) {
                list_holder.setBackgroundColor(getColor(R.color.white))
                list_title.elevation = toDp(8).toFloat()
            } else {
                list_holder.background = ContextCompat.getDrawable(context!!, R.drawable.transactions_list_holder_background)
                list_title.elevation = 0f
            }
        }
    }

    private fun renderWalletDetails() {
        viewModel.getDefaultWallet()
        viewModel.getWallet().observe(this, Observer<Wallet> {
            wallet = it
            val publicKey = wallet!!.ethAddress
            val qrBitmap = QRCode.from(publicKey).bitmap()
            issue_date.text = resources.getString(R.string.wallet_issue_date,
                    formatIssuedDate(wallet!!.insertedAt!!))
            wallet_name.text = wallet!!.name
            eth_address.text = resources.getString(R.string.wallet_address_subtext,
                    publicKey.substring(publicKey.length - 5 until publicKey.length))
            qr_code.setImageBitmap(qrBitmap)
            viewModel.fetchTransactions()
        })
    }

    private fun observeTransactions() {
        viewModel.getTransactions().observe(this, Observer<List<TransactionFeedQuery.Transaction>> {
            if(loading_spinner != null) loading_spinner.visibility = View.GONE
            if(swipeRefreshLayout.isRefreshing) swipeRefreshLayout.isRefreshing = false

            if(it!!.isNotEmpty()) {
                empty_list_label.visibility = View.GONE
                swipeRefreshLayout.visibility = View.VISIBLE
                transactions_list.visibility = View.VISIBLE
                transactions_list.adapter = TransactionsListAdapter(wallet!!, it, context!!)
                setOnTransactionClickedListener()
            } else {
                empty_list_label.visibility = View.VISIBLE
                transactions_list.visibility = View.GONE
            }
        })
    }

    private fun setOnTransactionClickedListener() {
        (transactions_list.adapter as TransactionsListAdapter).setChildOnClickListener(object: TransactionsListAdapter.OnTransactionClickedListener {
            override fun onTransactionClicked(transaction: TransactionFeedQuery.Transaction, position: Int) {
                viewModel.setCurrentTransaction(transaction)
                findNavController().navigate(R.id.transactionDetailsFragment)
            }
        })
    }

    private fun setOnSwipeRefreshListener() {
        swipeRefreshLayout.setOnRefreshListener {
            if(viewModel.getWallet().value != null) {
                viewModel.fetchTransactions()
            } else {
                viewModel.getDefaultWallet()
            }
        }
    }

    private fun formatIssuedDate(date: Date) : String {
        val sdf = SimpleDateFormat("MM/YY", Locale.getDefault())
        return sdf.format(date)
    }

    private fun setOnAddressLongPress() {
        eth_address.setOnLongClickListener {
            val clip = ClipData.newPlainText("Ethereum Address", wallet?.ethAddress)
            val clipboardManager = context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboardManager.primaryClip = clip
            context?.toast("Copied Address to Clipboard")
            true
        }
    }
}