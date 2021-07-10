package com.scg.hrst.ui.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.scg.hrst.R
import com.scg.hrst.databinding.ActivityQuizSelectionBinding
import com.scg.hrst.ui.fragments.AllJobsFragment
import com.scg.hrst.ui.fragments.AllQuizFragment
import com.scg.hrst.utils.Tools
import com.scg.hrst.utils.onClick

class QuizSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizSelectionBinding
    private val mAllQuizFragment = AllQuizFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadFragment(mAllQuizFragment)
        binding.toolbar.tvTitle.text=getString(R.string.select_quiz)
        binding.toolbar.ivBack.onClick { onBackPressed() }


        Tools.setSystemBarColor(this, R.color.Edu_app_background)
        Tools.setSystemBarLight(this)

    }

    private fun loadFragment(fragment: Fragment?) {

        if (fragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_container, fragment).commit()
        }

    }
}