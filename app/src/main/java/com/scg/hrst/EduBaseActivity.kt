package com.scg.hrst

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.scg.hrst.utils.HrstCustomToast
import com.scg.hrst.utils.changeToolbarFont
import io.github.inflationx.viewpump.ViewPumpContextWrapper

open class EduBaseActivity : AppCompatActivity()

{
    lateinit var mToast: HrstCustomToast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStatusBar(this)
        mToast = HrstCustomToast(this)


    }
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }
    fun setToolbar(mToolbar: Toolbar) {
        setSupportActionBar(mToolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        mToolbar.setNavigationIcon(R.drawable.edu_ic_arrow_back)
        mToolbar.setNavigationOnClickListener { onBackPressed() }
        mToolbar.changeToolbarFont()
    }
    fun showToast(aContent: String) {
        mToast.setCustomView(aContent)
        mToast.duration = Toast.LENGTH_SHORT
        mToast.show()
    }
    fun setStatusBar(activity: Activity) {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> {
                val window = activity.window
                var flags = activity.window.decorView.systemUiVisibility
                flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                activity.window.decorView.systemUiVisibility = flags
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = ContextCompat.getColor(this, R.color.Edu_app_background)
            }
            else -> {
                window.statusBarColor =  ContextCompat.getColor(this,R.color.Edu_colorPrimary)
            }
        }
    }
    object BiggerDotTransformationEdu : PasswordTransformationMethod() {

        override fun getTransformation(source: CharSequence, view: View): CharSequence {
            return PasswordCharSequence(super.getTransformation(source, view))
        }

        private class PasswordCharSequence(
            val transformation: CharSequence
        ) : CharSequence by transformation {
            override fun get(index: Int): Char = if (transformation[index] == DOT) {
                BIGGER_DOT
            } else {
                transformation[index]
            }
        }

        private const val DOT = '\u2022'
        private const val BIGGER_DOT = '●'
    }

    fun loadFragment(fragment: Fragment?) {
        if (fragment != null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.frame_container, fragment)
                .commit()
        }
    }

}
