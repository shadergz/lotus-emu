package com.emulotus

import android.content.SharedPreferences
import android.content.res.Resources.Theme

object RuntimeProperties {

    var appThemeSwitcher: (() -> Unit)? = null
    var appThemeFetcher: (() -> Theme)? = null
    var appPreferences: (() -> SharedPreferences)? = null

    fun switchAppTheme() {
        appThemeSwitcher.let {
            it?.invoke()
        }
    }

    fun appTheme(): Theme? {
        appThemeFetcher.let {
            return it?.invoke()
        }
    }
    fun appPreferences(): SharedPreferences? {
        appPreferences.let {
            return it?.invoke()
        }
    }
}
