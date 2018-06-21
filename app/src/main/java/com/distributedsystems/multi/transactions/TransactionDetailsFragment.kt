package com.distributedsystems.multi.transactions

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.distributedsystems.multi.MultiApp
import com.distributedsystems.multi.R
import com.distributedsystems.multi.TransactionFeedQuery
import com.distributedsystems.multi.common.GenericViewModelFactory
import com.distributedsystems.multi.common.getViewModel
import javax.inject.Inject

class TransactionDetailsFragment : Fragment() {

    @Inject
    internal lateinit var viewModelFactory : GenericViewModelFactory<TransactionsViewModel>
    private lateinit var viewModel : TransactionsViewModel

    private var transaction : TransactionFeedQuery.Transaction? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_transaction_detail_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        MultiApp.get().getComponent().inject(this)
        super.onViewCreated(view, savedInstanceState)
        viewModel = getViewModel(TransactionsViewModel::class.java, viewModelFactory)
        transaction = viewModel.getCurrentTransaction()
    }

    override fun onDetach() {
        super.onDetach()
        viewModel.setCurrentTransaction(null)
    }

}