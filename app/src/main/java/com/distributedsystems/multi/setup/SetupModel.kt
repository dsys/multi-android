package com.distributedsystems.multi.setup

import java.util.*

data class SetupModel(
    var fullName: String? = "",
    var username : String? = "",
    var phoneNumber : String? = "",
    var country : String? = "",
    var multiDevice : Boolean? = true,
    var mnemonicPassphrase: Array<String>? = arrayOf()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SetupModel

        if (fullName != other.fullName) return false
        if (username != other.username) return false
        if (phoneNumber != other.phoneNumber) return false
        if (country != other.country) return false
        if (multiDevice != other.multiDevice) return false
        if (!Arrays.equals(mnemonicPassphrase, other.mnemonicPassphrase)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fullName?.hashCode() ?: 0
        result = 31 * result + (username?.hashCode() ?: 0)
        result = 31 * result + (phoneNumber?.hashCode() ?: 0)
        result = 31 * result + (country?.hashCode() ?: 0)
        result = 31 * result + (multiDevice?.hashCode() ?: 0)
        result = 31 * result + (mnemonicPassphrase?.let { Arrays.hashCode(it) } ?: 0)
        return result
    }
}
