package com.distributedsystems.multi.setup.steps

import android.app.FragmentTransaction
import android.support.v4.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.distributedsystems.multi.R
import kotlinx.android.synthetic.main.fragment_setup_intro.*

class IntroFragment : Fragment() {

    companion object {
        fun newInstance() : IntroFragment = IntroFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_setup_intro, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        continue_btn.setOnClickListener {
            activity!!.supportFragmentManager!!.beginTransaction()
                    .replace(R.id.main_fragment_holder, SelectWalletFragment.newInstance())
                    .addToBackStack(null)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit()
        }
    }

}