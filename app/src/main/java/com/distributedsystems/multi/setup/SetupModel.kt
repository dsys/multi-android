package com.distributedsystems.multi.setup

import org.web3j.crypto.ECKeyPair

data class SetupModel(
    var fullName : String = "",
    var username : String = "",
    var phoneNumber : String = "",
    var country : String = "",
    var mnemonicPassphrase : String = "",
    var secretPassphrase : String = "",
    var ecKeyPair : ECKeyPair? = null
)
