package com.scg.hrst.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.scg.hrst.EduBaseActivity
import com.scg.hrst.R
import com.scg.hrst.databinding.ActivityLoginBinding
import com.scg.hrst.utils.Tools
import com.scg.hrst.utils.launchActivity
import com.scg.hrst.utils.onClick

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init(){

        Tools.setSystemBarColor(this, R.color.Edu_app_background)
        Tools.setSystemBarLight(this)

        binding.toolbar.tvTitle.text=getString(R.string.edu_title_login)
        binding.toolbar.ivBack.visibility= View.GONE
        binding.btnLogin.onClick { launchActivity<RecruiterDashboard>() }
        binding.edtPassword.transformationMethod = EduBaseActivity.BiggerDotTransformationEdu

    }
}