package com.distributedsystems.multi.di

import android.app.Application
import com.distributedsystems.multi.LandingActivity
import com.distributedsystems.multi.MainActivity
import com.distributedsystems.multi.MultiApp
import com.distributedsystems.multi.setup.SetupActivity
import com.distributedsystems.multi.setup.steps.IntroFragment
import com.distributedsystems.multi.setup.steps.SelectWalletFragment
import com.distributedsystems.multi.setup.steps.single.ConfirmMnemonicFragment
import com.distributedsystems.multi.setup.steps.single.DisplayMnemonicFragment
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
    fun inject(introFragment: IntroFragment)
    fun inject(selectWalletFragment: SelectWalletFragment)
    fun inject(displayMnemonicFragment: DisplayMnemonicFragment)
    fun inject(confirmMnemonicFragment: ConfirmMnemonicFragment)

    fun inject(mainActivity: MainActivity)
}