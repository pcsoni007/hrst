package com.scg.hrst.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.scg.hrst.R
import com.scg.hrst.utils.getAppColor

class JobFragment : Fragment(), View.OnClickListener {

    private val mAllJobsFragment = AllJobsFragment()
    private val mAppliedJobFragment = AppliedJobsFragment()
    private var mView : View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_job, container, false)



        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        mView?.findViewById<TextView>(R.id.tvTab1)?.setOnClickListener(this)
        mView?.findViewById<TextView>(R.id.tvTab2)?.setOnClickListener(this)
        mView?.findViewById<TextView>(R.id.tvTab1)!!.text=getString(R.string.edu_title_all)
        mView?.findViewById<TextView>(R.id.tvTab2)!!.text=getString(R.string.edu_title_completed_quiz)
        loadFragment(mAllJobsFragment)

    }

    private fun enable(aTextView: TextView?) {

        disableAll()
        aTextView?.setTextColor(requireActivity().getAppColor(R.color.Edu_textColorPrimary))
    }

    private fun disableAll() {
        disable(mView?.findViewById(R.id.tvTab1))
        disable(mView?.findViewById(R.id.tvTab2))
    }

    private fun disable(aTextView: TextView?) {
        aTextView?.setTextColor(requireActivity().getAppColor(R.color.Edu_textColorSecondary))

    }

    override fun onClick(p0: View?) {
        when (p0?.id) {

            R.id.tvTab1 -> {
                if (!mAllJobsFragment.isVisible) {
                    loadFragment(mAllJobsFragment)
                }
                enable(mView?.findViewById(R.id.tvTab1))
            }

            R.id.tvTab2 -> {
                if (!mAppliedJobFragment.isVisible) {
                    loadFragment(mAppliedJobFragment)
                }
                enable(mView?.findViewById(R.id.tvTab2))
            }

        }
    }

    private fun loadFragment(fragment: Fragment?) {
        if (fragment != null) {
            val transaction = childFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_container, fragment).commit()
        }
    }

}