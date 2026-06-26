package com.unfamiliardev.bbc

import android.app.Application
import android.content.Context
import com.unfamiliardev.bbc.util.LocaleHelper

class BBCApplication : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(base))
    }
}
