package com.distributedsystems.multi.setup

import com.distributedsystems.multi.networking.scalars.EthereumAddressString
import org.web3j.crypto.ECKeyPair

data class SetupModel(
        var fullName : String = "",
        var username : String = "",
        var phoneNumberToken : String = "",
        var hashedPhoneNumber : String = "",
        var country : String = "",
        var mnemonicPassphrase : String = "",
        var managerAddresses : MutableList<EthereumAddressString> = mutableListOf(),
        var socialRecoveryAddresses : MutableList<EthereumAddressString> = mutableListOf(),
        var secretPassphrase : String = "",
        var ecKeyPair : ECKeyPair? = null
)
