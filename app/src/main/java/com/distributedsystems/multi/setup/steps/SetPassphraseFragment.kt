package com.distributedsystems.multi.setup.steps

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.distributedsystems.multi.MainActivity
import com.distributedsystems.multi.MultiApp
import com.distributedsystems.multi.R
import com.distributedsystems.multi.common.GenericViewModelFactory
import com.distributedsystems.multi.common.getViewModel
import com.distributedsystems.multi.setup.SetupViewModel
import kotlinx.android.synthetic.main.fragment_set_passphrase.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import javax.inject.Inject

class SetPassphraseFragment : Fragment() {

    @Inject
    internal lateinit var viewModelFactory : GenericViewModelFactory<SetupViewModel>
    private lateinit var setupViewModel : SetupViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_set_passphrase, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        MultiApp.get().getComponent().inject(this)
        super.onViewCreated(view, savedInstanceState)
        setupViewModel = getViewModel(SetupViewModel::class.java, viewModelFactory)
        next_btn.setOnClickListener {
            validatePassphrase()
        }
    }

    private fun validatePassphrase() {
        when {
            passphrase.text.isNullOrEmpty() ->  passphrase.error = getString(R.string.invalid_passphrase_error)

            confirm_passphrase.text.isNullOrEmpty() -> confirm_passphrase.error = getString(R.string.invalid_passphrase_error)
            else -> {
                if(passphrase.text.toString() == confirm_passphrase.text.toString()) {
                    setupViewModel.createIdentityContract(passphrase.text.toString())
                    observeSetupCompleteStatus()
                    next_btn.visibility = View.INVISIBLE
                    passphrase.visibility = View.INVISIBLE
                    confirm_passphrase.visibility = View.INVISIBLE
                    loading_spinner.visibility = View.VISIBLE
                    title.text = getString(R.string.creating_account)
                    step_description.text = getString(R.string.creating_account_description)
                } else {
                    context?.toast(getString(R.string.differing_passphrase_error))
                }
            }
        }
    }

    private fun observeSetupCompleteStatus() {
        setupViewModel.setupCompleteObservable.observe(this, Observer {
            when(it?.hasErrors) {
                true -> {
                    context?.toast(it.error!!)
                }
                false -> {
                    context?.startActivity<MainActivity>()
                    activity!!.finish()
                }
            }
        })
    }

}