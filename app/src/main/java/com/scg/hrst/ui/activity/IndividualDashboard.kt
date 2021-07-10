package com.scg.hrst.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.iqonic.quiz.models.EduQuizModel
import com.scg.hrst.EduBaseActivity
import com.scg.hrst.R
import com.scg.hrst.databinding.ActivityIndividualDashboardBinding
import com.scg.hrst.ui.fragments.HomeFragment
import com.scg.hrst.ui.fragments.JobFragment
import com.scg.hrst.ui.fragments.ProfileFragment
import com.scg.hrst.utils.Tools
import com.scg.hrst.utils.getAppColor

class IndividualDashboard : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityIndividualDashboardBinding

    private val mHomeFragment = HomeFragment()
    private val mJobsFragment = JobFragment()
    private val mProfileFormat = ProfileFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIndividualDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init(){

        Tools.setSystemBarColor(this, R.color.Edu_app_background)
        Tools.setSystemBarLight(this)

        binding.bottom.llHome.setOnClickListener(this)
        binding.bottom.llQuiz.setOnClickListener(this)
        binding.bottom.llProfile.setOnClickListener(this)
        loadFragment(mHomeFragment)


    }


    private fun enable(aImageView: ImageView, aTextView: TextView) {
        disableAll()
        aImageView.setColorFilter(
            ContextCompat.getColor(this,
                R.color.Edu_colorPrimary
            ))
        aTextView.setTextColor(getAppColor(R.color.Edu_colorPrimary))
    }

    private fun disableAll() {
        disable(binding.bottom.ivHome, binding.bottom.tvHome)
        disable(binding.bottom.ivJob, binding.bottom.tvJob)
        disable(binding.bottom.ivProfile, binding.bottom.tvProfile)
    }

    private fun disable(aImageView: ImageView, aTextView: TextView) {
        aImageView.background = null
        aImageView.setColorFilter(
            ContextCompat.getColor(this,
            R.color.Edu_textColorPrimary
        ))
        aTextView.setTextColor(getAppColor(R.color.Edu_textColorPrimary))

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.llHome -> {
                if (!mHomeFragment.isVisible) {
                    loadFragment(mHomeFragment)
                }
                enable(binding.bottom.ivHome, binding.bottom.tvHome)
            }
            R.id.llQuiz -> {
                if (!mJobsFragment.isVisible) {
                    loadFragment(mJobsFragment)
                }
                enable(binding.bottom.ivJob, binding.bottom.tvJob)
            }
            R.id.llProfile -> {
                if (!mProfileFormat.isVisible) {
                    loadFragment(mProfileFormat)
                }
                enable(binding.bottom.ivProfile, binding.bottom.tvProfile)
            }

        }
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