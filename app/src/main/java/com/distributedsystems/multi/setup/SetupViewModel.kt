package com.distributedsystems.multi.setup

import android.arch.lifecycle.ViewModel

class SetupViewModel : ViewModel() {

    private var setupModel : SetupModel = SetupModel()

    fun setWalletOption(multi: Boolean) {
        setupModel.multiDevice = multi
    }

    fun generateMnemonicPhrase() {

    }
}