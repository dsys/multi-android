package com.distributedsystems.multi.setup

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.Rx2Apollo
import com.distributedsystems.multi.*
import com.distributedsystems.multi.common.DataWrapper
import com.distributedsystems.multi.common.Preferences
import com.distributedsystems.multi.crypto.WalletFactory
import com.distributedsystems.multi.db.Wallet
import com.distributedsystems.multi.db.WalletDao
import com.distributedsystems.multi.networking.scalars.EthereumAddressString
import com.distributedsystems.multi.type.ETHEREUM_NETWORK
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.defaultSharedPreferences
import org.spongycastle.jcajce.provider.digest.SHA3
import org.spongycastle.util.encoders.Hex
import org.web3j.crypto.ECKeyPair
import org.web3j.crypto.Keys
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SetupViewModel @Inject constructor(
        private val apolloClient: ApolloClient,
        private val walletFactory: WalletFactory,
        private val walletDb: WalletDao
) : ViewModel() {

    companion object {
        val LOG_TAG = SetupViewModel::class.java.simpleName
    }

    private var setupModel : SetupModel = SetupModel()
    private val compositeDisposable = CompositeDisposable()

    private lateinit var phoneNumber: String
    private lateinit var phoneNumberVerificationCode: String
    val phoneNumberVerificationObservable = MutableLiveData<DataWrapper<StartPhoneNumberVerificationMutation.StartPhoneNumberVerification>>()
    val checkPhoneNumberVerificationObservable = MutableLiveData<DataWrapper<CheckPhoneNumberVerificationMutation.CheckPhoneNumberVerification>>()
    var phoneNumberVerified : Boolean = false

    val usernameAvailabilityObservable = MutableLiveData<DataWrapper<Boolean>>()

    val setupCompleteObservable = MutableLiveData<DataWrapper<Boolean>>()

    fun generateMnemonicAndKeyPair() {
        setupModel.mnemonicPassphrase = walletFactory.generateMnemonicPhrase()
        setupModel.ecKeyPair = walletFactory.generateKeyPair(setupModel.mnemonicPassphrase)
        setupModel.managerAddresses.add(getPublicAddress())
    }

    fun setFullName(fullName: String) {
        setupModel.fullName = fullName
    }

    fun setUsername(username: String) {
        setupModel.username = "$username.multiapp.eth"
    }

    private fun getPublicAddress() : EthereumAddressString = EthereumAddressString("0x${Keys.getAddress(setupModel.ecKeyPair)}")
    private fun getKeyPair() : ECKeyPair? = setupModel.ecKeyPair

    private fun insertAndSetupUser(): Completable {
        return Completable.fromAction {
            MultiApp.get().defaultSharedPreferences
                    .edit()
                    .putBoolean(Preferences.PREF_SETUP_COMPLETE, true)
                    .apply()
        }
    }

    private fun saveWalletToDatabase() : Completable {
        return Completable.fromAction {
            val keyPair = getKeyPair()
            val publicAddress = getPublicAddress()
            val wallet = Wallet(null,
                    setupModel.username,
                    setupModel.fullName,
                    Date(),
                    keyPair!!.publicKey.toString(16),
                    keyPair.privateKey.toString(16),
                    publicAddress.toString())

            val id = walletDb.insert(wallet)

            MultiApp.get().defaultSharedPreferences
                    .edit()
                    .putLong(Preferences.PREF_DEFAULT_WALLET_ID, id)
                    .apply()
        }
    }

    fun checkUsernameAvailability(username: String) {
        compositeDisposable.clear()
        val usernameAvailabilityMutation = CheckUsernameAvailableMutation.builder()
                .username("$username.multiapp.eth")
                .build()

        val apolloCall = apolloClient.mutate(usernameAvailabilityMutation)

        compositeDisposable.add(
                Rx2Apollo.from(apolloCall)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            if(it.hasErrors()) {
                                usernameAvailabilityObservable.postValue(DataWrapper(
                                        null,
                                        true,
                                        it.errors().first().message()
                                ))
                            }
                            usernameAvailabilityObservable.postValue(DataWrapper(it.data()?.checkUsernameAvailable()?.ok()))
                        },{
                            usernameAvailabilityObservable.postValue(DataWrapper(
                                    null,
                                    true,
                                    it.localizedMessage)
                            )
                        })
        )

    }

    fun submitPhoneNumberForVerification(phoneNumber: String) {
        compositeDisposable.clear()
        this.phoneNumber = phoneNumber

        val phoneNumberVerificationMutation = StartPhoneNumberVerificationMutation.builder()
                .phoneNumber(phoneNumber)
                .build()

        val apolloCall = apolloClient.mutate(phoneNumberVerificationMutation)

        compositeDisposable.add(
                Rx2Apollo.from(apolloCall)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            if(it.hasErrors() || !it.data()?.startPhoneNumberVerification()?.ok()!!) {
                                phoneNumberVerificationObservable.postValue(DataWrapper(
                                        null,
                                        true,
                                        it.errors().first().message()
                                ))
                            } else {
                                phoneNumberVerificationObservable.postValue(DataWrapper(it.data()?.startPhoneNumberVerification()))
                            }
                        },{
                            phoneNumberVerificationObservable.postValue(DataWrapper(
                                    null,
                                    true,
                                    it.localizedMessage
                            ))
                        })
        )
    }
    fun checkPhoneNumberVerification(verificationCode: String) {
        compositeDisposable.clear()
        this.phoneNumberVerificationCode = verificationCode

        val checkPhoneNumberVerificationMutation = CheckPhoneNumberVerificationMutation.builder()
                .phoneNumber(this.phoneNumber)
                .verificationCode(this.phoneNumberVerificationCode)
                .build()

        val apolloCall = apolloClient.mutate(checkPhoneNumberVerificationMutation)

        compositeDisposable.add(
            Rx2Apollo.from(apolloCall)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        when {
                            it.hasErrors() -> checkPhoneNumberVerificationObservable.postValue(DataWrapper(
                                    null,
                                    true,
                                    it.errors().first().message()
                            ))
                            !it.data()?.checkPhoneNumberVerification()?.ok()!! -> checkPhoneNumberVerificationObservable.postValue(DataWrapper(
                                    null,
                                    true,
                                    it.data()?.checkPhoneNumberVerification()?.message()
                            ))
                            else -> {
                                checkPhoneNumberVerificationObservable.postValue(DataWrapper(it.data()?.checkPhoneNumberVerification()))
                                setupModel.phoneNumberToken = it.data()?.checkPhoneNumberVerification()?.phoneNumberToken()!!
                            }
                        }
                    }, {
                        Log.e(LOG_TAG, it.message)
                        checkPhoneNumberVerificationObservable.postValue(DataWrapper(
                                null,
                                true,
                                it.localizedMessage
                        ))
                    })
        )
    }

    fun createIdentityContract(passphrase: String) {
        compositeDisposable.clear()
        generateAndSavePassphraseHash(passphrase)
        val createIdentityContractMutation = CreateIdentityContractMutation.builder()
                .network(ETHEREUM_NETWORK.ROPSTEN)
                .username(setupModel.username)
                .phoneNumberToken(setupModel.phoneNumberToken)
                .managerAddresses(setupModel.managerAddresses)
                .socialRecoveryAddresses(setupModel.socialRecoveryAddresses)
                .passphraseRecoveryHash(setupModel.secretPassphrase)
                .build()

        val apolloCall = apolloClient.mutate(createIdentityContractMutation)

        compositeDisposable.add(
                Rx2Apollo.from(apolloCall)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            when {
                                it.hasErrors() -> { }
                                !it.data()?.createIdentityContract()?.ok()!! -> { }
                                else -> finishSetup()
                            }
                        },{
                            it.printStackTrace()
                            Log.e(LOG_TAG, it.message)
                        })
        )
    }

    private fun generateAndSavePassphraseHash(passphrase: String) {
        val sha3 = SHA3.Digest512.getInstance("SHA")
        val digest = sha3.digest(passphrase.toByteArray())
        setupModel.secretPassphrase = Hex.toHexString(digest)
    }

    private fun finishSetup() {
        compositeDisposable.add(
                insertAndSetupUser()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .delay(1500, TimeUnit.MILLISECONDS)
                        .subscribe({
                            saveWalletToDatabase()
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        setupCompleteObservable.postValue(DataWrapper(
                                                true,
                                                false,
                                                null
                                        ))
                                    },{
                                        setupCompleteObservable.postValue(DataWrapper(
                                                null,
                                                true,
                                                it.localizedMessage
                                        ))
                                    })
                        }, {
                            setupCompleteObservable.postValue(DataWrapper(
                                    null,
                                    true,
                                    it.localizedMessage
                            ))
                        })
        )
    }
}