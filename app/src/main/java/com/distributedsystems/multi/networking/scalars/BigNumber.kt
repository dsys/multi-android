package com.distributedsystems.multi.networking.scalars

import java.math.BigDecimal

class BigNumber(val value: BigDecimal) {

    override fun toString(): String {
        return value.toString()
    }

}