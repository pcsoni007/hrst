package com.scg.hrst

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump

class EduApp : Application() {

    override fun onCreate() {
        super.onCreate()
        appInstance = this

        // Set Custom Font
        ViewPump.init(ViewPump.builder().addInterceptor(CalligraphyInterceptor(
                CalligraphyConfig.Builder().setDefaultFontPath(getString(R.string.edu_font_regular)).setFontAttrId(R.attr.fontPath).build())
        ).build())
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    companion object {
        private lateinit var appInstance: EduApp

        fun getAppInstance(): EduApp {
            return appInstance
        }
    }
}
