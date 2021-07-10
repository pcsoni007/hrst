package com.scg.hrst.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DefaultItemAnimator
import com.iqonic.quiz.models.EduOnlineTestModel
import com.scg.hrst.R
import com.scg.hrst.adapters.EduRecyclerViewAdapter
import com.scg.hrst.databinding.ActivityQuizTestBinding
import com.scg.hrst.utils.getAppColor
import com.scg.hrst.utils.launchActivity
import com.scg.hrst.utils.onClick
import com.yuyakaido.android.cardstackview.*

class QuizTestActivity : AppCompatActivity() {

    private var mProgress = 0
    private var mSize = 0

    private val mOnlineTestAdapter =
        EduRecyclerViewAdapter<EduOnlineTestModel>(R.layout.item_quiz_test
        ) { view, item, position ->

            view.findViewById<TextView>(R.id.tvQuestion).text = item.question
            view.findViewById<TextView>(R.id.tvAns1).text = item.ans1
            view.findViewById<TextView>(R.id.tvAns2).text = item.ans2
            view.findViewById<TextView>(R.id.tvAns3).text = item.ans3
            view.findViewById<TextView>(R.id.tvAns4).text = item.ans4

            view.findViewById<LinearLayout>(R.id.llAns1).background = resources.getDrawable(R.drawable.edu_bg_rounded_border)
            view.findViewById<LinearLayout>(R.id.llAns2).background = resources.getDrawable(R.drawable.edu_bg_rounded_border)
            view.findViewById<LinearLayout>(R.id.llAns3).background = resources.getDrawable(R.drawable.edu_bg_rounded_border)
            view.findViewById<LinearLayout>(R.id.llAns4).background = resources.getDrawable(R.drawable.edu_bg_rounded_border)
            view.findViewById<TextView>(R.id.tvAns1).setTextColor(getAppColor(R.color.Edu_textColorSecondary))
            view.findViewById<TextView>(R.id.tvAns2).setTextColor(getAppColor(R.color.Edu_textColorSecondary))
            view.findViewById<TextView>(R.id.tvAns3).setTextColor(getAppColor(R.color.Edu_textColorSecondary))
            view.findViewById<TextView>(R.id.tvAns4).setTextColor(getAppColor(R.color.Edu_textColorSecondary))
            view.findViewById<TextView>(R.id.tvA).setTextColor(getAppColor(R.color.Edu_textColorSecondary))
            view.findViewById<TextView>(R.id.tvB).setTextColor(getAppColor(R.color.Edu_textColorSecondary))
            view.findViewById<TextView>(R.id.tvC).setTextColor(getAppColor(R.color.Edu_textColorSecondary))
            view.findViewById<TextView>(R.id.tvD).setTextColor(getAppColor(R.color.Edu_textColorSecondary))

            when (item.isSelected) {
                1 -> {
                    view.findViewById<LinearLayout>(R.id.llAns1).background =
                        resources.getDrawable(R.drawable.edu_bg_rounded_border_fill)
                    view.findViewById<TextView>(R.id.tvAns1).setTextColor(getAppColor(R.color.Edu_white))
                    view.findViewById<TextView>(R.id.tvA).setTextColor(getAppColor(R.color.Edu_white))
                }
                2 -> {
                    view.findViewById<LinearLayout>(R.id.llAns2).background =
                        resources.getDrawable(R.drawable.edu_bg_rounded_border_fill)
                    view.findViewById<TextView>(R.id.tvAns2).setTextColor(getAppColor(R.color.Edu_white))
                    view.findViewById<TextView>(R.id.tvB).setTextColor(getAppColor(R.color.Edu_white))
                }
                3 -> {
                    view.findViewById<LinearLayout>(R.id.llAns3).background =
                        resources.getDrawable(R.drawable.edu_bg_rounded_border_fill)
                    view.findViewById<TextView>(R.id.tvAns3).setTextColor(getAppColor(R.color.Edu_white))
                    view.findViewById<TextView>(R.id.tvC).setTextColor(getAppColor(R.color.Edu_white))
                }
                4 -> {
                    view.findViewById<LinearLayout>(R.id.llAns4).background =
                        resources.getDrawable(R.drawable.edu_bg_rounded_border_fill)
                    view.findViewById<TextView>(R.id.tvAns4).setTextColor(getAppColor(R.color.Edu_white))
                    view.findViewById<TextView>(R.id.tvD).setTextColor(getAppColor(R.color.Edu_white))
                }
            }
            view.findViewById<LinearLayout>(R.id.llAns1).onClick {
                item.isSelected = 1
                handleClickEvent(view, item, position)
            }
            view.findViewById<LinearLayout>(R.id.llAns2).onClick {
                item.isSelected = 2
                handleClickEvent(view, item, position)
            }
            view.findViewById<LinearLayout>(R.id.llAns3).onClick {
                item.isSelected = 3
                handleClickEvent(view, item, position)
            }
            view.findViewById<LinearLayout>(R.id.llAns4).onClick {
                item.isSelected = 4
                handleClickEvent(view, item, position)
            }
        }

    private fun handleClickEvent(view: View, item: EduOnlineTestModel, position: Int) {
        mOnlineTestAdapter.notifyItemChanged(position)
    }

    private val manager by lazy { CardStackLayoutManager(this) }

    private lateinit var binding : ActivityQuizTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityQuizTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mSize = getData().size
        binding.ivClose.onClick { onBackPressed() }

        initialize()

        binding.btnLogin.setOnClickListener {
            val setting = SwipeAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(AccelerateInterpolator())
                .build()
            manager.setSwipeAnimationSetting(setting)
            binding.cardStackView.swipe()

//            binding.cardStackView.visi


            mProgress++
            binding.sBar.progress = mProgress
            if (mSize == mProgress) {
                launchActivity<QuizResultActivity> { }
            }
        }
        binding.sBar.max = getData().size

    }

    private fun initialize() {

        manager.setStackFrom(StackFrom.Top)
        manager.setVisibleCount(3)
        manager.setTranslationInterval(12.0f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)
        manager.setCanScrollHorizontal(false)
        manager.setCanScrollVertical(false)
        manager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual)
        manager.setOverlayInterpolator(LinearInterpolator())
        binding.cardStackView.layoutManager = manager
        binding.cardStackView.adapter = mOnlineTestAdapter
        mOnlineTestAdapter.addItems(getData())
        binding.cardStackView.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }

//        binding.cardStackView.

    }

    companion object{
        fun getData(): ArrayList<EduOnlineTestModel> {
            val list = ArrayList<EduOnlineTestModel>()
            val model1 = EduOnlineTestModel()
            model1.question = "How many basic steps are there in scientific method?"
            model1.ans1 = "Eight Steps"
            model1.ans2 = "Ten Steps"
            model1.ans3 = "Two Steps"
            model1.ans4 = "One Steps"

            val model2 = EduOnlineTestModel()
            model2.question = "Which blood vessels have the smallest diameter??"
            model2.ans1 = "Capillaries"
            model2.ans2 = "Arterioles"
            model2.ans3 = "Venules"
            model2.ans4 = "Lymphatic"

            val model3 = EduOnlineTestModel()
            model3.question = "The substrate of photo-respiration is"
            model3.ans1 = "Phruvic acid"
            model3.ans2 = "Glucose"
            model3.ans3 = "Fructose"
            model3.ans4 = "Glycolate"

            val model4 = EduOnlineTestModel()
            model4.question = "Which one of these animal is jawless?"
            model4.ans1 = "Shark"
            model4.ans2 = "Myxine"
            model4.ans3 = "Trygon"
            model4.ans4 = "Sphyrna"

            val model5 = EduOnlineTestModel()
            model5.question = "How many basic steps are there in scientific method?"
            model5.ans1 = "Eight Steps"
            model5.ans2 = "Ten Steps"
            model5.ans3 = "One Steps"
            model5.ans4 = "Three Steps"

            list.add(model1)
            list.add(model2)
            list.add(model3)
            list.add(model4)
            list.add(model5)

            return list
        }

    }
}