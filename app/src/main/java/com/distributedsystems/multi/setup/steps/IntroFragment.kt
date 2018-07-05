package com.distributedsystems.multi.setup.steps

import android.app.FragmentTransaction
import android.content.Context
import android.support.v4.app.Fragment
import android.os.Bundle
import android.support.v4.app.SupportActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.distributedsystems.multi.MultiApp
import com.distributedsystems.multi.R
import com.distributedsystems.multi.common.GenericViewModelFactory
import com.distributedsystems.multi.common.getViewModel
import com.distributedsystems.multi.setup.SetupActivity
import com.distributedsystems.multi.setup.SetupViewModel
import kotlinx.android.synthetic.main.fragment_setup_intro.*
import javax.inject.Inject

class IntroFragment : Fragment() {

    companion object {
        fun newInstance() : IntroFragment = IntroFragment()
    }

    @Inject
    internal lateinit var viewModelFactory : GenericViewModelFactory<SetupViewModel>
    private lateinit var viewModel : SetupViewModel

    override fun onAttach(context: Context?) {
        MultiApp.get().getComponent().inject(this)
        super.onAttach(context)
        viewModel = getViewModel(SetupViewModel::class.java, viewModelFactory)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_setup_intro, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        continue_btn.setOnClickListener {
            viewModel.generateMnemonicAndKeyPair()
            (activity as SetupActivity).addAndReplaceFragment(SelectWalletFragment.newInstance())
        }
    }

}