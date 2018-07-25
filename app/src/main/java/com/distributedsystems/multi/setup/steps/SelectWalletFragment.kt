package com.distributedsystems.multi.setup.steps

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.distributedsystems.multi.MultiApp
import com.distributedsystems.multi.R
import com.distributedsystems.multi.common.GenericViewModelFactory
import com.distributedsystems.multi.common.getViewModel
import com.distributedsystems.multi.setup.SetupViewModel
import kotlinx.android.synthetic.main.fragment_setup_select_wallet.*
import javax.inject.Inject

class SelectWalletFragment : Fragment() {

    @Inject
    internal lateinit var viewModelFactory : GenericViewModelFactory<SetupViewModel>
    private lateinit var setupViewModel : SetupViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        MultiApp.get().getComponent().inject(this)
        super.onCreate(savedInstanceState)
        setupViewModel = getViewModel(SetupViewModel::class.java, viewModelFactory)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_setup_select_wallet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        create_identity_option.setOnClickListener {
            setupViewModel.generateMnemonicAndKeyPair()
            findNavController().navigate(R.id.claimUsernameFragment)
        }
    }

}

