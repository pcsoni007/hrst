package com.scg.hrst.ui.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.scg.hrst.R
import com.scg.hrst.databinding.ActivitySettingsBinding
import com.scg.hrst.utils.Tools
import com.scg.hrst.utils.launchActivity
import com.scg.hrst.utils.onClick

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init(){

        Tools.setSystemBarColor(this, R.color.Edu_app_background)
        Tools.setSystemBarLight(this)

        binding.toolbar.tvTitle.text=getString(R.string.edu_lbl_setting)
        binding.toolbar.ivBack.onClick { onBackPressed() }

//        binding.rlEmail.onClick { launchActivity<EduUpdateEmailActivity> {  } }
        binding.rlEditProfile.onClick { launchActivity<EditProfileActivity> {  } }
        binding.rlContactUs.onClick { launchActivity<ContactUsActivity> {  } }
//        binding.rlHelp.onClick { launchActivity<EduHelpActivity> {  } }
//        binding.rlPassword.onClick { launchActivity<PasswordActivity> {  } }
        binding.rlPrivacy.onClick {  val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(getString(R.string.edu_text_googlelink))
            startActivity(i) }
//        binding.tvLogout.onClick { launchActivity<EduSignInActivity> {  } }

    }
}