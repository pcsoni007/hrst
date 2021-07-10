
package com.scg.hrst.ui.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.balysv.materialripple.MaterialRippleLayout
import com.iqonic.quiz.models.EduQuizModel
import com.scg.hrst.R
import com.scg.hrst.adapters.EduRecyclerViewAdapter
import com.scg.hrst.data.models.Image
import com.scg.hrst.ui.activity.*
import com.scg.hrst.utils.Tools
import com.scg.hrst.utils.launchActivity
import com.scg.hrst.utils.loadImageFromResources
import com.scg.hrst.utils.onClick

class HomeFragment : Fragment() {

    private var mView : View? = null

    private val parent_view: View? = null
    private var viewPager: ViewPager? = null
    private var layout_dots: LinearLayout? = null
    private var adapterImageSlider: AdapterImageSlider? =
        null
    private var runnable: Runnable? = null
    private val handler = Handler()

    private val array_image_place = intArrayOf(
        R.drawable.image_12,
        R.drawable.image_13,
        R.drawable.image_14,
        R.drawable.image_15,
        R.drawable.image_16
    )

    private val array_title_place = arrayOf(
        "Dui fringilla ornare finibus, orci odio",
        "Mauris sagittis non elit quis fermentum",
        "Mauris ultricies augue sit amet est sollicitudin",
        "Suspendisse ornare est ac auctor pulvinar",
        "Vivamus laoreet aliquam ipsum eget pretium"
    )

    private val array_brief_place = arrayOf(
        "Foggy Hill",
        "The Backpacker",
        "River Forest",
        "Mist Mountain",
        "Side Park"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_home, container, false)


        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        val recView = view.findViewById<RecyclerView>(R.id.rvJobs)
        val mJobAdapter =
            EduRecyclerViewAdapter<EduQuizModel>(R.layout.layout_item_new_job,
                onBind = { view, item, position ->

                    view.findViewById<ImageView>(R.id.ivJobImg).loadImageFromResources(requireActivity(),item.QuizImage!!)
                    view.findViewById<TextView>(R.id.tvHeading).text = item.quizname
                    view.findViewById<TextView>(R.id.tvTotalJobs).text = item.totalQuiz
                    view.setOnClickListener {
                        handleClickEvent(item, position)
                    }
                })

        recView.adapter = mJobAdapter
        recView.layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        mJobAdapter.addItems(getQuizData())

        initComponent()

//        ivSearch.onClick { requireActivity().launchActivity<EduSearchActivity> { } }
        mView?.findViewById<TextView>(R.id.tvViewAll)?.onClick {

            requireActivity().launchActivity<ViewAllJobsActivity> { }

        }

        mView?.findViewById<CardView>(R.id.registrationBtn)?.onClick {

            requireActivity().launchActivity<RegistrationActivity> { }

        }

        mView?.findViewById<CardView >(R.id.quizBtn)?.onClick {

//            loadFragment(mHomeFragment)

            requireActivity().launchActivity<QuizSelectionActivity> { }

        }

    }

    private fun handleClickEvent(item: EduQuizModel, position: Int) {
        requireActivity().launchActivity<JobInformationActivity> { }
    }


    private fun initComponent() {

        layout_dots = mView?.findViewById<LinearLayout>(R.id.layout_dots)
        viewPager = mView?.findViewById<ViewPager>(R.id.pager)

        adapterImageSlider =
            AdapterImageSlider(
                requireActivity(),
                java.util.ArrayList<Image>()
            )

        val items: MutableList<Image> = java.util.ArrayList<Image>()

        for (i in array_image_place.indices) run {
            var obj = Image( array_image_place[i], resources.getDrawable(array_image_place[i]),
                array_title_place[i],
                array_brief_place[i]
            )

            items.add(obj)
        }

        adapterImageSlider!!.setItems(items)
        viewPager!!.adapter = adapterImageSlider

        // displaying selected image first
        viewPager!!.currentItem = 0
        addBottomDots(layout_dots!!, adapterImageSlider!!.count, 0)
        (mView!!.findViewById<View>(R.id.title) as TextView).setText(items[0].name)
        (mView!!.findViewById<View>(R.id.brief) as TextView).setText(items[0].brief)
        viewPager!!.addOnPageChangeListener(object : OnPageChangeListener {

            override fun onPageScrolled(
                pos: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(pos: Int) {
                (mView!!.findViewById<View>(R.id.title) as TextView).text = items[pos].name
                (mView!!.findViewById<View>(R.id.brief) as TextView).text = items[pos].brief
                addBottomDots(layout_dots!!, adapterImageSlider!!.count, pos)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
        startAutoSlider(adapterImageSlider!!.count)
    }

    private fun addBottomDots(layout_dots: LinearLayout, size: Int, current: Int) {
        val dots = arrayOfNulls<ImageView>(size)
        layout_dots.removeAllViews()
        for (i in dots.indices) {
            dots[i] = ImageView(context)
            val width_height = 15
            val params =
                LinearLayout.LayoutParams(ViewGroup.LayoutParams(width_height, width_height))
            params.setMargins(10, 10, 10, 10)
            dots[i]!!.layoutParams = params
            dots[i]!!.setImageResource(R.drawable.shape_circle_outline)
            layout_dots.addView(dots[i])
        }
        if (dots.isNotEmpty()) {
            dots[current]!!.setImageResource(R.drawable.shape_circle)
        }
    }

    private fun startAutoSlider(count: Int) {
        runnable = Runnable {
            var pos = viewPager!!.currentItem
            pos += 1
            if (pos >= count) pos = 0
            viewPager!!.currentItem = pos
            handler.postDelayed(runnable!!, 3000)
        }
        handler.postDelayed(runnable!!, 3000)
    }

    private class AdapterImageSlider(
        private val act: Activity,
        items: List<Image>
    ) :
        PagerAdapter() {
        private var items: List<Image>
        private var onItemClickListener: OnItemClickListener? = null

        private interface OnItemClickListener {
            fun onItemClick(view: View?, obj: Image?)
        }

        fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
            this.onItemClickListener = onItemClickListener
        }

        override fun getCount(): Int {
            return items.size
        }

        fun getItem(pos: Int): Image {
            return items[pos]
        }

        fun setItems(items: List<Image>) {
            this.items = items
            notifyDataSetChanged()
        }

        override fun isViewFromObject(view: View, `object`: Any): Boolean {
            return view === `object` as RelativeLayout
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val o: Image = items[position]
            val inflater = act.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val v: View = inflater.inflate(R.layout.item_slider_image, container, false)
            val image = v.findViewById<View>(R.id.image) as ImageView
            val lyt_parent = v.findViewById<View>(R.id.lyt_parent) as MaterialRippleLayout
            Tools.displayImageOriginal(act, image, o.image)
            lyt_parent.setOnClickListener { v ->
                if (onItemClickListener != null) {
                    onItemClickListener!!.onItemClick(v, o)
                }
            }
            (container as ViewPager).addView(v)
            return v
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            (container as ViewPager).removeView(`object` as RelativeLayout)
        }

        // constructor
        init {
            this.items = items
        }
    }

    override fun onDestroy() {
        if (runnable != null) handler.removeCallbacks(runnable!!)
        super.onDestroy()
    }


    companion object {

        fun getQuizData(): List<EduQuizModel> {

            val list = ArrayList<EduQuizModel>()

            val model1 = EduQuizModel()
            model1.quizname = "Biology & The \nScientific Method"
            model1.totalQuiz = "15 Quiz"
            model1.QuizImage = R.drawable.edu_ic_study1

            val model2 = EduQuizModel()
            model2.quizname = "Geography Basics \n Methods"
            model2.totalQuiz = "5 Quiz"
            model2.QuizImage = R.drawable.edu_ic_study2

            val model3 = EduQuizModel()
            model3.quizname = "Java Basics \nOOPs Concept"
            model3.totalQuiz = "10 Quiz"
            model3.QuizImage = R.drawable.edu_ic_computer

            val model4 = EduQuizModel()
            model4.quizname = "Art and\nPainting Basic"
            model4.totalQuiz = "10 Quiz"
            model4.QuizImage = R.drawable.edu_ic_art

            val model5 = EduQuizModel()
            model5.quizname = "Communication Basic"
            model5.totalQuiz = "10 Quiz"
            model5.QuizImage = R.drawable.edu_ic_communication

            val model6 = EduQuizModel()
            model6.quizname = "Investment and \nTypes"
            model6.totalQuiz = "10 Quiz"
            model6.QuizImage = R.drawable.edu_ic_course2

            list.add(model1)
            list.add(model3)
            list.add(model6)
            list.add(model4)
            list.add(model5)
            list.add(model2)

            return list
        }
    }
}