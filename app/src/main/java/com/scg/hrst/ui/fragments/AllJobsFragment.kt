package com.scg.hrst.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iqonic.quiz.models.EduQuizModel
import com.scg.hrst.R
import com.scg.hrst.adapters.EduRecyclerViewAdapter
import com.scg.hrst.ui.activity.JobInformationActivity
import com.scg.hrst.utils.launchActivity
import com.scg.hrst.utils.loadImageFromResources

class AllJobsFragment : Fragment() {


    private val mGridAdapter =
        EduRecyclerViewAdapter<EduQuizModel>(R.layout.layout_item_quiz_grid,
            onBind = { view, item, position ->
                view.findViewById<ImageView>(R.id.ivJobImg).loadImageFromResources(requireActivity(), item.QuizImage!!)
                view.findViewById<TextView>(R.id.tvHeading).text = item.quizname
                view.findViewById<TextView>(R.id.tvTotalJobs).text = item.totalQuiz

                view.setOnClickListener {
                    handleClickEvent(item, position)
                }
            })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        
        return inflater.inflate(R.layout.fragment_all_jobs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        val recView = view.findViewById<RecyclerView>(R.id.rvJobs)

        recView.adapter = mGridAdapter
        recView.layoutManager = GridLayoutManager(context,2)
        mGridAdapter.clearItems()
        mGridAdapter.addItems(HomeFragment.getQuizData())

    }

    private fun handleClickEvent(item: EduQuizModel, position: Int) {
        requireActivity().launchActivity<JobInformationActivity> {  }
    }
}