package com.distributedsystems.multi.setup.steps

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.distributedsystems.multi.R
import com.distributedsystems.multi.common.getViewModel
import com.distributedsystems.multi.setup.SetupActivity
import com.distributedsystems.multi.setup.SetupViewModel
import com.distributedsystems.multi.setup.steps.single.DisplayMnemonicFragment
import kotlinx.android.synthetic.main.fragment_setup_select_wallet.*

class SelectWalletFragment : Fragment() {

    companion object {
        fun newInstance() : SelectWalletFragment = SelectWalletFragment()
    }

    private lateinit var setupViewModel : SetupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel = getViewModel(SetupViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_setup_select_wallet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        single_device_option.setOnClickListener {
            setupViewModel.setWalletOption(false)
            setupViewModel.generateMnemonicPhrase()
            (this.activity as SetupActivity).addAndReplaceFragment(DisplayMnemonicFragment.newInstance())
        }
    }

}

