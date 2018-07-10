package com.distributedsystems.multi.setup.steps

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
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
import kotlinx.android.synthetic.main.fragment_pick_username.*
import org.jetbrains.anko.toast
import javax.inject.Inject

class ClaimUsernameFragment : Fragment() {

    companion object {
        val LOG_TAG = this::class.java.simpleName
    }

    @Inject
    internal lateinit var viewModelFactory : GenericViewModelFactory<SetupViewModel>
    private lateinit var setupViewModel : SetupViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_pick_username, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        MultiApp.get().getComponent().inject(this)
        super.onViewCreated(view, savedInstanceState)
        setupViewModel = getViewModel(SetupViewModel::class.java, viewModelFactory)
        watchUsername()
        setOnContinueClickListener()
    }

    override fun onResume() {
        super.onResume()
        username_status.visibility = View.INVISIBLE
        username_status_loader.visibility = View.INVISIBLE
    }

    private fun setOnContinueClickListener() {
        next_btn.setOnClickListener {
            if(validateName()) {
                findNavController().navigate(R.id.startPhoneNumberVerificationFragment)
            }
        }
    }

    private fun watchUsername() {
        setupViewModel.usernameAvailabilityObservable.observe(this, Observer {
            username_status_loader.visibility = View.INVISIBLE
            if(it!!.hasErrors) {
                context?.toast(getString(R.string.unable_to_validate_username_error))
                Log.e(LOG_TAG, it.error)
            } else {
                if (it.data!!) {
                    next_btn.isEnabled = true
                    username_status.visibility = View.VISIBLE
                } else {
                    username.error = getString(R.string.username_taken_error)
                }
            }
        })
        username.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(validateUsername()) {
                    username_status_loader.visibility = View.VISIBLE
                    setupViewModel.checkUsernameAvailability(s.toString())
                }
            }
        })
    }

    private fun validateName() : Boolean {
        val nameValue = full_name.text.toString()
        var isValid = false
        if(!"([a-zA-Z]{3,30}\\s*)+".toRegex().matches(nameValue)) {
            full_name.error = getString(R.string.invalid_name_error)
        } else {
            setupViewModel.setFullName(nameValue)
            isValid = true
        }
        return isValid
    }

    private fun validateUsername() : Boolean {
        val usernameValue = username.text.toString()
        var isValid = false

        if(!"(^[a-z0-9_-]{3,15}\$)".toRegex().matches(usernameValue)) {
            if(usernameValue.length < 3) {
                username.error = getString(R.string.username_too_short_error)
            } else {
                username.error = getString(R.string.invalid_username_error)
            }
        } else {
            if(!isValid) isValid = true
            setupViewModel.setUsername(usernameValue)
        }
        return isValid
    }
}