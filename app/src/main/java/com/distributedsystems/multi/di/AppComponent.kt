package com.distributedsystems.multi.di

import android.app.Application
import com.distributedsystems.multi.LandingActivity
import com.distributedsystems.multi.MainActivity
import com.distributedsystems.multi.MultiApp
import com.distributedsystems.multi.profile.ProfileFragment
import com.distributedsystems.multi.setup.SetupActivity
import com.distributedsystems.multi.setup.steps.*
import com.distributedsystems.multi.transactions.SendTransactionFragment
import com.distributedsystems.multi.transactions.TransactionDetailsFragment
import com.distributedsystems.multi.transactions.TransactionsFragment
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component (modules = [
    AndroidInjectionModule::class,
    AppModule::class,
    GraphModule::class,
    CryptoModule::class
]) interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application) : Builder

        fun appModule(appModule: AppModule) : Builder

        fun build() : AppComponent
    }

    fun inject(multiApp: MultiApp)

    fun inject(landingActivity: LandingActivity)

    fun inject(setupActivity: SetupActivity)
    fun inject(selectWalletFragment: SelectWalletFragment)
    fun inject(claimUsernameFragment: ClaimUsernameFragment)
    fun inject(startPhoneNumberVerificationFragment: StartPhoneNumberVerificationFragment)
    fun inject(checkPhoneNumberVerificationFragment: CheckPhoneNumberVerificationFragment)
    fun inject(linkQRCodeFragment: LinkQRCodeFragment)
    fun inject(passphraseFragment: SetPassphraseFragment)

    fun inject(mainActivity: MainActivity)
    fun inject(profileFragment: ProfileFragment)

    fun inject(transactionsFragment: TransactionsFragment)
    fun inject(transactionDetailsFragment: TransactionDetailsFragment)
    fun inject(sendTransactionFragment: SendTransactionFragment)
}