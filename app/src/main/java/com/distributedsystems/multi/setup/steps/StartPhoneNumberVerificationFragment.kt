package com.distributedsystems.multi.setup.steps

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.distributedsystems.multi.MultiApp
import com.distributedsystems.multi.R
import com.distributedsystems.multi.common.GenericViewModelFactory
import com.distributedsystems.multi.common.getViewModel
import com.distributedsystems.multi.setup.SetupViewModel
import kotlinx.android.synthetic.main.fragment_enter_phonenumber.*
import org.jetbrains.anko.toast
import javax.inject.Inject

class StartPhoneNumberVerificationFragment : Fragment() {

    companion object {
        private val LOG_TAG = this::class.java.simpleName
    }

    @Inject
    internal lateinit var viewModelFactory : GenericViewModelFactory<SetupViewModel>
    private lateinit var setupViewModel : SetupViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_enter_phonenumber, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        MultiApp.get().getComponent().inject(this)
        super.onViewCreated(view, savedInstanceState)
        setupViewModel = getViewModel(SetupViewModel::class.java, viewModelFactory)
        observeSubmitPhoneNumberVerificationResult()
        setupPhoneNumberPicker()
        setOnClickListeners()
    }

    private fun setupPhoneNumberPicker() {
        country_code_picker.registerCarrierNumberEditText(phone_number)
    }

    private fun setOnClickListeners() {
        next_btn.setOnClickListener {
            if(country_code_picker.isValidFullNumber) {
                val phoneNumber = country_code_picker.fullNumber
                setupViewModel.submitPhoneNumberForVerification(phoneNumber)
            } else {
                phone_number.error = getString(R.string.invalid_phone_number_error)
            }
        }
    }

    private fun observeSubmitPhoneNumberVerificationResult() {
        setupViewModel.phoneNumberVerificationObservable.observe(this, Observer {
            if(it!!.hasErrors) {
                context?.toast(getString(R.string.phone_number_verification_error))
                Log.e(LOG_TAG, it.error)
            } else {
                findNavController().navigate(R.id.checkPhoneNumberVerificationFragment)
            }
        })
    }
}