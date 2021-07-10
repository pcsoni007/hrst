package com.scg.hrst.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.scg.hrst.R
import com.scg.hrst.databinding.ActivityWalkThroughBinding
import com.scg.hrst.utils.*

class WalkThroughActivity : AppCompatActivity() {

    internal var mHeading =
        arrayOf("All Important Health tips", "Meditation is usefull for health", "Jogging is good for health")
    internal var mSubHeading = arrayOf(
        "Lorem Ipsum is simply dummy text of the printing and typesetting industry.This is simply text ",
        "Lorem Ipsum is simply dummy text of the printing and typesetting industry.This is simply text  ",
        "Lorem Ipsum is simply dummy text of the printing and typesetting industry.This is simply text  "
    )

    private lateinit var binding: ActivityWalkThroughBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalkThroughBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Tools.setSystemBarLight(this)

        makeTransaprant()
//        title=getString(R.string.theme2_lbl_walkthrough)
//        setTheme2Toolbar(toolbar)
        val adapter = WalkAdapter()
        binding.theme2Viewpager.adapter = adapter
        binding.dots.attachViewPager(binding.theme2Viewpager)
        binding.dots.setDotDrawable(R.drawable.theme2_bg_color_primary, R.drawable.theme2_bg_black_dot)
        binding.theme2Viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                binding.tvHeading.text = mHeading[position]
                binding.tvSubHeading.text = mSubHeading[position]
            }

        })
        binding.tvHeading.text = mHeading[0]
        binding.tvSubHeading.text = mSubHeading[0]

        binding.btnNext.onClick {
            launchActivity<RegistrationActivity> {  }
        }
    }

    internal inner class WalkAdapter : PagerAdapter() {

        private val mImg =
            intArrayOf(
                R.drawable.theme2_walk_1,
                R.drawable.theme2_walk_2,
                R.drawable.theme2_walk_3
            )

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as View)
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = LayoutInflater.from(container.context)
                .inflate(R.layout.theme2_walk, container, false)
            view.findViewById<ImageView>(R.id.imgWalk).loadImageFromResources(applicationContext,mImg[position])

            container.addView(view)
            return view
        }

        override fun getCount(): Int {
            return mImg.size
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object` as View
        }
    }


}
