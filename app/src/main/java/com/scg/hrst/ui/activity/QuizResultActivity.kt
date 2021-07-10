package com.scg.hrst.ui.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.scg.hrst.R
import com.scg.hrst.databinding.ActivityQuizResultBinding
import com.scg.hrst.utils.getAppColor
import com.scg.hrst.utils.launchActivity
import com.scg.hrst.utils.onClick
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import java.util.*

class QuizResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val min = 30
        val max = 50
        val random = Random().nextInt(max - min + 1) + min
        binding.ivClose.onClick { onBackPressed() }
        binding.ivEmail.onClick {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(getString(R.string.edu_text_maillink))
            startActivity(i)
        }
        binding.ivFacebook.onClick {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(getString(R.string.edu_text_facebookLink))
            startActivity(i)
        }
        binding.ivTwitter.onClick {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(getString(R.string.edu_text_TwitterLink))
            startActivity(i)
        }
        val mFlagValue = intent.getStringExtra("Score")
        if (mFlagValue != null) {
            if (mFlagValue == "1") {
                if(random>=41)
                {
                    binding.progress.progress= 50F
                    binding.tvTotal.text= 50F.toString()
                }
                else
                {
                    binding.progress.progress= random.toFloat()
                    binding.tvTotal.text= "$random%"
                    binding.tvHeading.text="Don't Give Up!"
                    binding.tvSubHeading.text="Study more next time and get \n all the answer correct!"
                    binding.llSocial.visibility= View.GONE
                }
            }
        }
        else
        {
            if(random>=41)
            {
                rewardEffect()
                binding.progress.progress= 50F
                binding.tvTotal.text= 50F.toString()
            }
            else
            {
                binding.progress.progress= random.toFloat()
                binding.tvTotal.text= "$random%"
                binding.tvHeading.text="Don't Give Up!"
                binding.tvSubHeading.text="Study more next time and get \n all the answer correct!"
                binding.llSocial.visibility= View.GONE
                binding.btnRetry.visibility= View.VISIBLE
                binding.btnRetry.onClick {
                    launchActivity<QuizTestActivity> {  }
                }
            }
        }
    }

    private fun rewardEffect() {
        binding.viewKonfetti.build()
            .addColors(getAppColor(R.color.Edu_color_setting), getAppColor(R.color.Edu_colorAccent), getAppColor(R.color.Edu_red), getAppColor(R.color.Edu_textColorPrimary), getAppColor(R.color.Edu_textColorSecondary))
            .setDirection(0.0, 359.0)
            .setSpeed(1f, 5f)
            .setFadeOutEnabled(true)
            .addShapes(Shape.RECT, Shape.CIRCLE)
            .addSizes(Size(12), Size(16, 6f))
            .setPosition(binding.viewKonfetti.x + binding.viewKonfetti.width / 2, binding.viewKonfetti.y + binding.viewKonfetti.height / 3)
            .burst(700)
    }
}
