package com.scg.hrst.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.scg.hrst.R
import com.scg.hrst.databinding.ActivityJobInformationBinding
import com.scg.hrst.databinding.ActivityViewAllJobsBinding
import com.scg.hrst.utils.Tools

class JobInformationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJobInformationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobInformationBinding.inflate(layoutInflater)
        setContentView(binding.root)


        init()
    }


    private fun init(){

        Tools.setSystemBarColor(this, R.color.Edu_app_background)
        Tools.setSystemBarLight(this)

        binding.toolbar.tvTitle.text=getString(R.string.job_information)
//        binding.toolbar.ivBack.visibility= View.GONE

    }

}