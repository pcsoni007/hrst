package com.scg.hrst.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.scg.hrst.R
import com.scg.hrst.ui.activity.EditProfileActivity
import com.scg.hrst.ui.activity.SettingsActivity
import com.scg.hrst.utils.getAppColor
import com.scg.hrst.utils.launchActivity
import com.scg.hrst.utils.onClick

class ProfileFragment : Fragment() {

//    private val mBadgesFragment = EduBadgesFragment()
//    private val mScoreFormat = EduScoreFragment()
    private lateinit var mView: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        mView = inflater.inflate(R.layout.fragment_profile, container, false)


        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageView>(R.id.ivSetting).onClick { requireActivity().launchActivity<SettingsActivity> {  } }

        view.findViewById<ImageView>(R.id.ivEdit).onClick { requireActivity().launchActivity<EditProfileActivity> {
            putExtra("mobile_no", "null")
        } }

    }

    /*private fun enable(aTextView: TextView) {
        disableAll()
        aTextView.setTextColor(requireActivity().getAppColor(R.color.Edu_textColorPrimary))
    }

    private fun disableAll() {
        disable(tvTab1)
        disable(tvTab2)
    }
    private fun disable(aTextView: TextView) {
        aTextView.setTextColor(requireActivity().getAppColor(R.color.Edu_textColorSecondary))

    }
    fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tvTab1 -> {
                if (!mBadgesFragment.isVisible) {
                    loadFragment(mBadgesFragment)
                }
                enable(tvTab1)
            }
            R.id.tvTab2 -> {
                if (!mScoreFormat.isVisible) {
                    loadFragment(mScoreFormat)
                }
                enable(tvTab2)
            }

        }
    }
    private fun loadFragment(fragment: Fragment?) {
        if (fragment != null) {
            val transaction = childFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_container, fragment).commit()
        }
    }*/

}