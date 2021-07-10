package com.scg.hrst.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iqonic.quiz.models.EduQuizModel
import com.scg.hrst.R
import com.scg.hrst.adapters.EduRecyclerViewAdapter
import com.scg.hrst.databinding.ActivityViewAllJobsBinding
import com.scg.hrst.ui.fragments.HomeFragment
import com.scg.hrst.utils.*

class ViewAllJobsActivity : AppCompatActivity() {


    private lateinit var binding: ActivityViewAllJobsBinding

//    private val jobImg = view.findViewById<ImageView>(R.id.ivQuiz)

    private val mGridAdapter =
        EduRecyclerViewAdapter<EduQuizModel>(R.layout.layout_item_quiz_grid,
            onBind = { view, item, position ->
                view.findViewById<ImageView>(R.id.ivJobImg).loadImageFromResources(this,item.QuizImage!!)
                view.findViewById<TextView>(R.id.tvHeading).text = item.quizname
                view.findViewById<TextView>(R.id.tvTotalJobs).text = item.totalQuiz

                view.setOnClickListener {
                    handleClickEvent(item, position)
                }
            })

    private val mListAdapter =
        EduRecyclerViewAdapter<EduQuizModel>(R.layout.layout_item_quiz_list,
            onBind = { view, item, position ->
                view.findViewById<ImageView>(R.id.ivJobImg).loadImageFromResources(this,item.QuizImage!!)
                view.findViewById<TextView>(R.id.tvHeading).text = item.quizname
                view.findViewById<TextView>(R.id.tvTotalJobs).text = item.totalQuiz
                view.setOnClickListener {
                    handleClickEvent(item, position)
                }
            })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewAllJobsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()


        binding.toolbar.tvTitle.text=getString(R.string.new_jobs)
        binding.toolbar.ivBack.onClick { onBackPressed() }
        setListener()
        binding.rvJobs.adapter = mGridAdapter
        binding.rvJobs.layoutManager = GridLayoutManager(this ,2)
        mGridAdapter.addItems(HomeFragment.getQuizData())

    }

    private fun init(){

        Tools.setSystemBarColor(this, R.color.Edu_app_background)
        Tools.setSystemBarLight(this)

    }


    private fun handleClickEvent(item: EduQuizModel, position: Int) {
        launchActivity<JobInformationActivity> {  }
    }
    private fun setListener()
    {
        binding.ivGrid.onClick {
            setColorFilter(ContextCompat.getColor(context, R.color.design_default_color_primary))
            binding.ivList.setColorFilter(getAppColor(R.color.Edu_textColorSecondary))
            binding.rvJobs.adapter = mGridAdapter
            binding.rvJobs.layoutManager = GridLayoutManager(this@ViewAllJobsActivity,2)
            mGridAdapter.clearItems()
            mGridAdapter.addItems(HomeFragment.getQuizData())

        }

        binding.ivList.onClick {
            setColorFilter(ContextCompat.getColor(context, R.color.design_default_color_primary))
            binding.ivGrid.setColorFilter(getAppColor(R.color.Edu_textColorSecondary))
            binding.rvJobs.adapter = mListAdapter
            binding.rvJobs.layoutManager = LinearLayoutManager(this@ViewAllJobsActivity, RecyclerView.VERTICAL, false)
            mListAdapter.clearItems()
            mListAdapter.addItems(HomeFragment.getQuizData())
        }
    }
}