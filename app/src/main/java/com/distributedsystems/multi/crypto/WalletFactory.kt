package com.distributedsystems.multi.crypto

import com.distributedsystems.multi.BuildConfig
import io.github.novacrypto.bip39.MnemonicGenerator
import io.github.novacrypto.bip39.Words
import io.github.novacrypto.bip39.wordlists.English
import org.web3j.crypto.ECKeyPair
import org.web3j.crypto.Hash.sha256
import org.web3j.crypto.MnemonicUtils
import java.security.SecureRandom
import javax.inject.Inject

class WalletFactory @Inject constructor(private val secureRandom: SecureRandom) {

    fun generateMnemonicPhrase() : String {
        val sb = StringBuilder()
        val entropy = ByteArray(Words.TWELVE.byteLength())
        secureRandom.nextBytes(entropy)
        MnemonicGenerator(English.INSTANCE)
                .createMnemonic(entropy, { sb.append(it) })
        return sb.toString()
    }

    fun generateKeyPair(mnemonic: String) : ECKeyPair {
        val seed = MnemonicUtils.generateSeed(mnemonic, BuildConfig.SEED_GEN_PATH)
        return ECKeyPair.create(sha256(seed))
    }
}