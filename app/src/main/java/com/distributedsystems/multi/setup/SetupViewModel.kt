package com.distributedsystems.multi.setup

import android.arch.lifecycle.ViewModel
import android.os.Handler
import com.distributedsystems.multi.MultiApp
import com.distributedsystems.multi.common.Preferences
import com.distributedsystems.multi.crypto.WalletFactory
import com.distributedsystems.multi.db.Wallet
import com.distributedsystems.multi.db.WalletDao
import io.reactivex.Completable
import org.jetbrains.anko.defaultSharedPreferences
import org.web3j.crypto.ECKeyPair
import org.web3j.crypto.Keys
import javax.inject.Inject

class SetupViewModel @Inject constructor(
        private val walletFactory: WalletFactory,
        private val walletDb: WalletDao
) : ViewModel() {

    companion object {
        val LOG_TAG = SetupViewModel::class.java.simpleName
    }

    private var setupModel : SetupModel = SetupModel()

    fun setWalletOption(multi: Boolean) {
        setupModel.multiDevice = multi
    }

    fun generateMnemonicAndKeyPair() {
        setupModel.mnemonicPassphrase = walletFactory.generateMnemonicPhrase()
        setupModel.ecKeyPair = walletFactory.generateKeyPair(setupModel.mnemonicPassphrase)
    }

    fun getMnemonicWords() : List<String> = setupModel.mnemonicPassphrase.split(" ")

    fun setWalletName(name: String) {
        setupModel.walletName = name
    }

    private fun getWalletName() : String = setupModel.walletName

    fun getPublicAddress() : String = Keys.getAddress(setupModel.ecKeyPair)
    private fun getKeyPair() : ECKeyPair? = setupModel.ecKeyPair

    fun insertAndSetupUser(): Completable {
        return Completable.fromAction {
            MultiApp.get().defaultSharedPreferences
                    .edit()
                    .putBoolean(Preferences.PREF_SETUP_COMPLETE, true)
                    .apply()
        }
    }

    fun saveWalletToDatabase() : Completable {
        return Completable.fromAction {
            val keyPair = getKeyPair()
            val publicAddress = getPublicAddress()
            val walletName = getWalletName()
            val wallet = Wallet(null, keyPair!!.publicKey.toString(16), keyPair.privateKey.toString(16), publicAddress, walletName)
            walletDb.insert(wallet)
        }
    }
}