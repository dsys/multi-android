package com.distributedsystems.multi.setup.steps.single

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.distributedsystems.multi.R
import com.distributedsystems.multi.common.getViewModel
import com.distributedsystems.multi.setup.SetupActivity
import com.distributedsystems.multi.setup.SetupViewModel
import kotlinx.android.synthetic.main.fragment_display_mnemonic_phrase.*

class DisplayMnemonicFragment : Fragment() {

    companion object {
        fun newInstance() : DisplayMnemonicFragment = DisplayMnemonicFragment()
    }

    private lateinit var setupViewModel : SetupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel = getViewModel(SetupViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_display_mnemonic_phrase, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        continue_btn.setOnClickListener {
            (this.activity as SetupActivity).addAndReplaceFragment(ConfirmMnemonicFragment.newInstance())
        }
    }
}