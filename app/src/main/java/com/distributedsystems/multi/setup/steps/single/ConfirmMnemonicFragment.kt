package com.distributedsystems.multi.setup.steps.single

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.distributedsystems.multi.R

class ConfirmMnemonicFragment : Fragment() {

    companion object {
        fun newInstance() : ConfirmMnemonicFragment = ConfirmMnemonicFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_confirm_mnemonic_phrase, container, false)
    }

}