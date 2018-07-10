package com.distributedsystems.multi.setup.steps

import android.animation.ValueAnimator
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
import com.distributedsystems.multi.common.toDp
import com.distributedsystems.multi.setup.SetupViewModel
import kotlinx.android.synthetic.main.fragment_verify_phonenumber.*
import org.jetbrains.anko.toast
import javax.inject.Inject

class CheckPhoneNumberVerificationFragment : Fragment() {

    companion object {
        private val LOG_TAG = this::class.java.simpleName
    }

    @Inject
    internal lateinit var viewModelFactory : GenericViewModelFactory<SetupViewModel>
    private lateinit var setupViewModel : SetupViewModel
    private lateinit var valueAnimator : ValueAnimator

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_verify_phonenumber, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        MultiApp.get().getComponent().inject(this)
        super.onViewCreated(view, savedInstanceState)
        setupViewModel = getViewModel(SetupViewModel::class.java, viewModelFactory)
        observeSubmitPhoneNumberVerificationResult()
        setOnClickListeners()
    }

    override fun onResume() {
        super.onResume()
        if(setupViewModel.phoneNumberVerified) {
            valueAnimator.removeAllListeners()
            icon.setPadding(64.toDp(), 64.toDp(), 64.toDp(), 64.toDp())
            verification_input.visibility = View.INVISIBLE
            title.text = getString(R.string.phone_number_verified)
            step_description.text = getString(R.string.finish_setup_description)
            submit_btn.text = getString(R.string.btn_label_next)
            submit_btn.setOnClickListener {
                findNavController().navigate(R.id.linkQRCodeFragment)
            }
        }
    }

    private fun setOnClickListeners() {
        submit_btn.setOnClickListener {
            if(verification_input.text.length == 6) {
                startVerifyingAnimation()
                setupViewModel.checkPhoneNumberVerification(verification_input.text)
            }
        }
    }

    private fun observeSubmitPhoneNumberVerificationResult() {
        setupViewModel.checkPhoneNumberVerificationObservable.observe(this, Observer {
            if(it!!.hasErrors) {
                context?.toast(getString(R.string.phone_number_verification_error))
                Log.e(LOG_TAG, it.error)
            } else {
                valueAnimator.removeAllListeners()
                icon.setPadding(64.toDp(), 64.toDp(), 64.toDp(), 64.toDp())
                verification_input.visibility = View.INVISIBLE
                title.text = getString(R.string.phone_number_verified)
                step_description.text = getString(R.string.finish_setup_description)
                submit_btn.text = getString(R.string.btn_label_next)
                submit_btn.setOnClickListener {
                    findNavController().navigate(R.id.linkQRCodeFragment)
                }
                findNavController().navigate(R.id.linkQRCodeFragment)
            }
        })
    }

    private fun startVerifyingAnimation() = apply {
        valueAnimator = ValueAnimator.ofInt(64.toDp(), 48.toDp()).apply {
            duration = 750
            addUpdateListener {
                if (icon != null) {
                    icon.setPadding(
                            animatedValue.toString().toInt(),
                            animatedValue.toString().toInt(),
                            animatedValue.toString().toInt(),
                            animatedValue.toString().toInt()
                    )
                }
            }
            repeatMode = ValueAnimator.REVERSE
            repeatCount = ValueAnimator.INFINITE
        }
        valueAnimator.start()
    }
}