package com.distributedsystems.multi.transactions

import android.graphics.drawable.Drawable

data class TransactionDetailsListItem (
        var icon: Drawable? = null,
        var iconColor: Int? = -1,
        var iconBackground: Int? = -1,
        var title: String? =  "",
        var value: String? = ""
) {
    class Builder {
        private var icon: Drawable? = null
        private var iconColor: Int = -1
        private var iconBackground: Int = -1
        private var title: String = ""
        private var value: String = ""

        fun setIcon(drawable: Drawable) = apply { this.icon = drawable }
        fun setIconColor(color: Int) = apply { this.iconColor = color }
        fun setIconBackground(background: Int) = apply { this.iconBackground = background }
        fun setTitle(title: String) = apply { this.title = title }
        fun setVal(value: String) = apply { this.value = value }

        fun build() : TransactionDetailsListItem =
                TransactionDetailsListItem(icon, iconColor, iconBackground, title, value)
    }
}