package com.distributedsystems.multi.setup.steps.single

import android.app.AlertDialog
import android.graphics.Typeface
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.distributedsystems.multi.MainActivity
import com.distributedsystems.multi.MultiApp
import com.distributedsystems.multi.R
import com.distributedsystems.multi.common.*
import com.distributedsystems.multi.setup.SetupViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_confirm_mnemonic_phrase.*
import org.jetbrains.anko.startActivity
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ConfirmMnemonicFragment : Fragment() {

    companion object {
        fun newInstance() : ConfirmMnemonicFragment = ConfirmMnemonicFragment()
        private const val LOG_TAG = "ConfirmMnemonicFragment"
    }

    @Inject
    internal lateinit var viewModelFactory : GenericViewModelFactory<SetupViewModel>
    private lateinit var setupViewModel : SetupViewModel
    private lateinit var mnemonicWords : List<String>
    private var currentWord = 0
    private var previousWords : ArrayList<TextView> = arrayListOf()
    private var disposable = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_confirm_mnemonic_phrase, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        MultiApp.get().getComponent().inject(this)
        super.onViewCreated(view, savedInstanceState)
        setupViewModel = getViewModel(SetupViewModel::class.java, viewModelFactory)
        mnemonicWords = setupViewModel.getMnemonicWords()

        Log.d("MNEMONIC", mnemonicWords.toString())

        watchInputs()
        mnemonic_confirmation_field.requestFocus()
        getInputMethodManager()?.showSoftInput(mnemonic_confirmation_field, InputMethodManager.SHOW_IMPLICIT)
    }


    private fun watchInputs() {
        mnemonic_holder.setOnClickListener {
            mnemonic_confirmation_field.requestFocus()
            getInputMethodManager()?.showSoftInput(mnemonic_confirmation_field, InputMethodManager.SHOW_IMPLICIT)
        }

        mnemonic_confirmation_field.setOnClickListener {
            mnemonic_confirmation_field.setSelection(mnemonic_confirmation_field.text.length)
        }
        mnemonic_confirmation_field.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(s!!.endsWith(" ")) {
                    verifyWords(s.toString().substring(0, s.indexOf(" ")))
                } else if(currentWord == 11) {
                    verifyWords(s.toString())
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })
    }

    private fun verifyWords(word: String) {
        if(mnemonicWords[currentWord] == word) {
            mnemonic_confirmation_field.text.clear()
            insertWord(word)
            currentWord++
        } else {
            mnemonic_confirmation_field.startAnimation(shakeError())
            mnemonic_confirmation_field.error = "Incorrect"
        }
    }

    private fun showSuccessDialog() {
        getInputMethodManager()!!.hideSoftInputFromWindow(mnemonic_confirmation_field.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
        mnemonic_confirmation_field.visibility = View.GONE
        AlertDialog.Builder(context, R.style.AppTheme_AlertDialog)
                .setTitle("Congrats!")
                .setMessage("You're ready to start using Multi! Let's go.")
                .setPositiveButton("Go") { dialog, _ ->
                    dialog.dismiss()
                    progress_bar.visibility = View.VISIBLE
                    progress_bar_label.visibility = View.VISIBLE
                    disposable.add(setupViewModel.insertAndSetupUser()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .delay(1500, TimeUnit.MILLISECONDS)
                            .subscribe({ setupViewModel.saveWalletToDatabase()
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        progress_bar.visibility = View.GONE
                                        progress_bar_label.visibility = View.GONE
                                        context!!.startActivity<MainActivity>()
                                        activity!!.finish()
                                    }, { error -> Log.e(LOG_TAG, "Unable to save wallet to db", error) })
                            }, { error -> Log.e(LOG_TAG, "Unable to insert user to db", error) }))

                }.show()
    }

    private fun shakeError() : TranslateAnimation {
        val shake = TranslateAnimation(0f, 10f, 0f,0f)
        shake.duration = 500
        shake.interpolator = CycleInterpolator(3f)
        return shake
    }

    private fun insertWord(word: String) {
        val tv = TextView(context)
        tv.id = View.generateViewId()
        tv.text = word
        tv.setTextColor(getColor(R.color.successGreen))
        tv.textSize = 16f
        tv.typeface = Typeface.MONOSPACE

        mnemonic_holder.addView(tv)
        previousWords.add(tv)

        val tvLp : ConstraintLayout.LayoutParams
        val mcfLp : ConstraintLayout.LayoutParams

        when (currentWord % 3) {
            0 -> {
                tvLp =  (tv.layoutParams as ConstraintLayout.LayoutParams).apply {
                    startToStart = R.id.mnemonic_holder
                    marginStart = toDp(32)
                    topMargin = if(getRow() == 1) toDp(32) else toDp(20)
                    height = toDp(28)
                    when (getRow()) {
                        1 -> topToTop = R.id.mnemonic_holder
                        2 -> topToBottom = previousWords[0].id
                        3 -> topToBottom = previousWords[3].id
                        4 -> topToBottom = previousWords[6].id
                    }
                }

                mcfLp = (mnemonic_confirmation_field.layoutParams as ConstraintLayout.LayoutParams).apply {
                    startToStart = R.id.mnemonic_holder
                    endToEnd = R.id.mnemonic_holder
                    topMargin = if(getRow() == 1) toDp(32) else toDp(20)
                    marginStart = toDp(32)
                    marginEnd = toDp(32)
                    topToTop = -1
                    topToBottom = -1
                    when (getRow()) {
                        1 -> topToTop = R.id.mnemonic_holder
                        2 -> topToBottom = previousWords[1].id
                        3 -> topToBottom = previousWords[4].id
                        4 -> topToBottom = previousWords[7].id
                    }
                }
            }
            1 -> {
                tvLp =  (tv.layoutParams as ConstraintLayout.LayoutParams).apply {
                    height = toDp(28)
                    startToStart = R.id.mnemonic_holder
                    endToEnd = R.id.mnemonic_holder
                    topMargin = if(getRow() == 1) toDp(32) else toDp(20)
                    marginStart = toDp(32)
                    marginEnd = toDp(32)
                    when (getRow()) {
                        1 -> topToTop = R.id.mnemonic_holder
                        2 -> topToBottom = previousWords[1].id
                        3 -> topToBottom = previousWords[4].id
                        4 -> topToBottom = previousWords[7].id
                    }
                }

                mcfLp = (mnemonic_confirmation_field.layoutParams as ConstraintLayout.LayoutParams).apply {
                    startToStart = -1
                    startToEnd = tv.id
                    endToEnd = R.id.mnemonic_holder
                    topToTop = -1
                    topToBottom = -1
                    topMargin = if(getRow() == 1) toDp(32) else toDp(20)
                    marginStart = toDp(32)
                    marginEnd = toDp(32)
                    when (getRow()) {
                        1 -> topToTop = R.id.mnemonic_holder
                        2 -> topToBottom = previousWords[2].id
                        3 -> topToBottom = previousWords[5].id
                        4 -> topToBottom = previousWords[8].id
                    }
                }
            }
            2 -> {
                tvLp =  (tv.layoutParams as ConstraintLayout.LayoutParams).apply {
                    height = toDp(28)
                    endToEnd = R.id.mnemonic_holder
                    marginEnd = toDp(32)
                    topMargin = if(getRow() == 1) toDp(32) else toDp(20)
                    when (getRow()) {
                        1 -> topToTop = R.id.mnemonic_holder
                        2 -> topToBottom = previousWords[2].id
                        3 -> topToBottom = previousWords[5].id
                        4 -> topToBottom = previousWords[8].id
                    }
                }

                mcfLp = (mnemonic_confirmation_field.layoutParams as ConstraintLayout.LayoutParams).apply {
                    startToStart = R.id.mnemonic_holder
                    marginStart = toDp(32)
                    marginEnd = 0
                    topMargin = toDp(20)
                    endToEnd = -1
                    topToTop = -1
                    topToBottom = -1
                    when (getRow()) {
                        1 -> topToBottom = previousWords[0].id
                        2 -> topToBottom = previousWords[3].id
                        3 -> topToBottom = previousWords[6].id
                    }
                }
            }
            else -> {
                mcfLp = (mnemonic_confirmation_field.layoutParams as ConstraintLayout.LayoutParams)
                tvLp = (tv.layoutParams as ConstraintLayout.LayoutParams)
            }
        }

        tv.layoutParams = tvLp
        mnemonic_confirmation_field.layoutParams = mcfLp

        if(previousWords.size == 12) showSuccessDialog()
    }

    private fun getRow() : Int {
        return when {
            currentWord > 8 -> 4
            currentWord > 5 -> 3
            currentWord > 2 -> 2
            else -> 1
        }
    }

}