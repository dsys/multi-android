package com.distributedsystems.multi.transactions

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.distributedsystems.multi.MultiApp
import com.distributedsystems.multi.R
import com.distributedsystems.multi.TransactionFeedQuery
import com.distributedsystems.multi.common.GenericViewModelFactory
import com.distributedsystems.multi.common.getViewModel
import kotlinx.android.synthetic.main.fragment_transaction_detail_view.*
import javax.inject.Inject

class TransactionDetailsFragment : Fragment() {

    @Inject
    internal lateinit var viewModelFactory : GenericViewModelFactory<TransactionsViewModel>
    private lateinit var viewModel : TransactionsViewModel

    private val navOptions = NavOptions.Builder()
            .setExitAnim(R.anim.slide_out_bottom)
            .setPopUpTo(R.id.transactionsFragment, true)
            .build()

    private var transaction : TransactionFeedQuery.Transaction? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_transaction_detail_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        MultiApp.get().getComponent().inject(this)
        super.onViewCreated(view, savedInstanceState)
        viewModel = getViewModel(TransactionsViewModel::class.java, viewModelFactory)
        transaction = viewModel.getCurrentTransaction()
        val txDetailList = viewModel.getTransactionDetailsList()
        detailsListView.layoutManager = LinearLayoutManager(context, LinearLayout.VERTICAL, false)
        detailsListView.adapter = TransactionsDetailsListAdapter(txDetailList, context!!)
        setToolbarNavOnClick()
    }

    override fun onDetach() {
        super.onDetach()
        viewModel.setCurrentTransaction(null)
    }

    private fun setToolbarNavOnClick() {
        toolbar.setNavigationOnClickListener {
            findNavController().navigate(R.id.transactionsFragment, null, navOptions)
        }
    }

}