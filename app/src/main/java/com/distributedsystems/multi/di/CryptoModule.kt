package com.distributedsystems.multi.di

import com.distributedsystems.multi.crypto.WalletFactory
import dagger.Module
import dagger.Provides
import org.web3j.crypto.LinuxSecureRandom
import java.security.SecureRandom
import javax.inject.Singleton

@Module(includes = [AppModule::class])
class CryptoModule {

    init {
        LinuxSecureRandom()
    }

    @Provides
    @Singleton
    fun providesSecureRandom() : SecureRandom {
        return SecureRandom()
    }

    @Provides
    @Singleton
    fun providesWalletFactory(secureRandom: SecureRandom) : WalletFactory {
        return WalletFactory(secureRandom)
    }
}