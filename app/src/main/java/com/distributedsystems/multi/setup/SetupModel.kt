package com.distributedsystems.multi.setup

import org.web3j.crypto.ECKeyPair

data class SetupModel(
    var fullName : String = "",
    var username : String = "",
    var phoneNumber : String = "",
    var country : String = "",
    var multiDevice : Boolean = true,
    var walletName : String = "My Wallet",
    var mnemonicPassphrase : String = "",
    var ecKeyPair : ECKeyPair? = null
)
