package com.coolnexttech.docscan

import android.app.Application
import android.content.Context
import java.lang.ref.WeakReference

lateinit var appContext: WeakReference<Context>

class DocScan: Application() {

    override fun onCreate() {
        super.onCreate()

        appContext = WeakReference(this)
    }
}