package com.distributedsystems.multi.setup.steps.single

import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.InputType
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.TextView
import com.distributedsystems.multi.MultiApp
import com.distributedsystems.multi.R
import com.distributedsystems.multi.common.GenericViewModelFactory
import com.distributedsystems.multi.common.getColor
import com.distributedsystems.multi.common.getInputMethodManager
import com.distributedsystems.multi.common.getViewModel
import com.distributedsystems.multi.setup.SetupActivity
import com.distributedsystems.multi.setup.SetupViewModel
import kotlinx.android.synthetic.main.fragment_display_mnemonic_phrase.*
import net.glxn.qrgen.android.QRCode
import javax.inject.Inject

class DisplayMnemonicFragment : Fragment() {

    companion object {
        fun newInstance() : DisplayMnemonicFragment = DisplayMnemonicFragment()
    }

    @Inject
    internal lateinit var viewModelFactory : GenericViewModelFactory<SetupViewModel>
    private lateinit var setupViewModel : SetupViewModel

    private var isFocused : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        MultiApp.get().getComponent().inject(this)
        super.onCreate(savedInstanceState)
        setupViewModel = getViewModel(SetupViewModel::class.java, viewModelFactory)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_display_mnemonic_phrase, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        populateQrCode()
        initWalletNameEditText()
        populateMnemonic()
        setOnContinue()
    }

    private fun populateQrCode() {
        val publicKey = setupViewModel.getPublicAddress()
        val qrBitmap = QRCode.from(publicKey).bitmap()
        qr_code.setImageBitmap(qrBitmap)
        wallet_address.text = resources.getString(R.string.wallet_address_subtext,
                publicKey.substring(publicKey.length-5 until publicKey.length))
    }

    private fun initWalletNameEditText() {
        edit_name_btn.setOnClickListener {
            isFocused = if(!isFocused) {
                wallet_name.requestFocus()
                true
            } else {
                wallet_name.clearFocus()
                qr_code.requestFocus()
                false
            }
        }

        wallet_name.apply {
            setOnKeyListener { v, keyCode, event ->
                if(event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    v.clearFocus()
                    true
                } else {
                    false
                }
            }
            setImeActionLabel("Done", KeyEvent.KEYCODE_ENTER)
            setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    wallet_name.isClickable = true
                    wallet_name.isLongClickable = true
                    wallet_name.inputType = InputType.TYPE_CLASS_TEXT
                    edit_name_btn.text = resources.getString(R.string.done)
                    getInputMethodManager()?.showSoftInput(wallet_name, InputMethodManager.SHOW_IMPLICIT)
                } else {
                    wallet_name.isClickable = false
                    wallet_name.isLongClickable = false
                    wallet_name.inputType = InputType.TYPE_NULL
                    edit_name_btn.text = resources.getString(R.string.edit)
                    edit_name_btn.requestFocus()
                    getInputMethodManager()?.hideSoftInputFromWindow(wallet_name.windowToken, 0)
                }
            }
        }
    }

    private fun populateMnemonic() {
        setupViewModel.getMnemonicWords().mapIndexed { index, word ->
            when (index) {
                in 0..2 -> mnemonic_line_one.addView(buildMnemonicWordView(word))
                in 3..5 -> mnemonic_line_two.addView(buildMnemonicWordView(word))
                in 6..8 -> mnemonic_line_three.addView(buildMnemonicWordView(word))
                in 9..11 -> mnemonic_line_four.addView(buildMnemonicWordView(word))
            }
        }
    }

    private fun setOnContinue() {
        continue_btn.setOnClickListener {
            setupViewModel.setWalletName(wallet_name.text.toString())
            (this.activity as SetupActivity).addAndReplaceFragment(ConfirmMnemonicFragment.newInstance())
        }
    }

    private fun buildMnemonicWordView(word: String) : TextView {
        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            setMargins(32, 8, 32, 8)
            weight = 1f
        }
        return TextView(context).apply {
            text = word
            textSize = 14f
            setTypeface(Typeface.MONOSPACE, Typeface.BOLD)
            setTextColor(getColor(R.color.darkGrey))
            layoutParams = lp
        }
    }
}