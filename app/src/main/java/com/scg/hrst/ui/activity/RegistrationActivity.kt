package com.scg.hrst.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.scg.hrst.R
import com.scg.hrst.databinding.ActivityRegistrationBinding
import com.scg.hrst.utils.Tools
import com.scg.hrst.utils.launchActivity
import com.scg.hrst.utils.onClick

class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

    }

    private fun init(){

        Tools.setSystemBarColor(this, R.color.Edu_app_background)
        Tools.setSystemBarLight(this)

        binding.tvSkip.onClick { launchActivity<MobileSignUpActivity>() }

        binding.toolbar.tvTitle.text=getString(R.string.register_yourself_as)
        binding.toolbar.ivBack.visibility= View.GONE

        binding.individualReg.onClick { launchActivity<PersonRegistration>() }

        binding.comReg.onClick { launchActivity<RecruiterRegistration>() }

    }

}