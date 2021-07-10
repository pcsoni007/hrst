package com.scg.hrst.ui.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.scg.hrst.R
import com.scg.hrst.databinding.ActivityJobPostingBinding
import com.scg.hrst.utils.CheckInternet
import com.scg.hrst.utils.HrstCustomToast
import com.scg.hrst.utils.Tools
import com.scg.hrst.utils.onClick
import com.scg.hrst.viewmodels.CommonViewModel

class JobPostingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJobPostingBinding

    private val workList = ArrayList<String>()

    private var jobRole : String? = null
    private var jobDescription : String? = null
    private var jobLocation : String? = null
    private var workingHours : String? = null
    private var typeofJob : String? = null
    private var shift : String? = null
    private var minEducation : String? = null
    private var jobPay : String? = null
    private var noOfOpening : String?  = null
    private var lastDate : String?  = null
    private var jobPostDate : String?  = null
    private var minSalary : String?  = null
    private var maxSalary : String?  = null
    private var minExp : String?  = null
    private var maxExp : String?  = null

    private var jobWorkList : String?  = null
    private var jobAddedBy : String?  = null


    private var imgLink : String? = null
    private var currentDate : String? = null
    private var districtList = ArrayList<String>()


    private var selectedState : String? = null

    private var commonViewModel  = CommonViewModel()

    private lateinit var mToast: HrstCustomToast

    private val TAG = "JobPosting"

    private val mDocRef = FirebaseFirestore.getInstance()
    private val myStorRef = FirebaseStorage.getInstance().reference

    private lateinit var alertDialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityJobPostingBinding.inflate(layoutInflater)
        setContentView(binding.root)


        init()
    }

    private fun init(){

        Tools.setSystemBarColor(this, R.color.Edu_app_background)
        Tools.setSystemBarLight(this)

        mToast = HrstCustomToast(this)
        binding.toolbar.tvTitle.text=getString(R.string.post_job)

        setWorkList()

//        disableAll()
//        binding.sBar.max = mSize
//        binding.toolbar.ivBack.visibility= View.GONE



//        binding.commission.text =

    }

    fun onClick(view: View) {

        when(view.id){
            binding.workHours.id -> {

                workingHours = showCustomDialog("Working Hours", resources.getStringArray(R.array.working_hours), binding.workHours)
//                binding.workHours.setText(workingHours)

            }

            binding.jobType.id -> {
                typeofJob = showCustomDialog("Type of Job", resources.getStringArray(R.array.job_type), binding.jobType)
//                binding.jobType.setText(typeofJob)

            }

            binding.shift.id -> {
                shift = showCustomDialog("Shift", resources.getStringArray(R.array.shift), binding.shift)
//                binding.shift.setText(shift)

            }

            binding.minEducation.id -> {
                shift = showCustomDialog("Minimum Education", resources.getStringArray(R.array.min_education), binding.minEducation)
//                binding.shift.setText(shift)

            }

            binding.lastDate.id -> {
                CheckInternet.dialogDatePickerLastDate(binding.lastDate, this , supportFragmentManager)
                Log.e("lastDate", binding.lastDate.text.toString())
            }

            binding.jobLocation.id -> {
                addJobLocation()
            }
        }
    }

    private fun setWorkList(){

        val array = resources.getStringArray(R.array.semi_skill_list)

        binding.addWork.onClick {
            val builder = MaterialAlertDialogBuilder(this@JobPostingActivity)
            builder.setTitle("Types of Work")
            builder.setSingleChoiceItems(
                array, -1
            ) { dialogInterface, i ->

                val chip = Chip(this@JobPostingActivity)

                if(!workList.contains(array[i])){

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        chip.closeIcon = resources.getDrawable(R.drawable.edu_ic_close, theme)
                    }
                    chip.isCloseIconVisible = true
                    chip.closeIconEndPadding = 2.0F
                    chip.closeIconStartPadding = 2.0F
                    chip.closeIconSize = 40F
                    chip.setTextAppearanceResource(R.style.ChipStyle)
                    chip.setChipBackgroundColorResource(R.color.white) // = resources.getColor()
                    chip.setOnCloseIconClickListener {
                        workList.remove(array[i])
                        binding.chipGroup.removeView(chip)
                    }

                    workList.add(array[i])
                    chip.text = array[i]

                    chip.setCloseIconResource(R.drawable.edu_ic_close)
                    binding.chipGroup.addView(chip)
                }

                dialogInterface.dismiss()
            }
            builder.show()

        }

    }


    private fun showCustomDialog(title : String, list : Array<String>, edt: EditText): String{

        var selectedItem = ""
        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle(title)
        builder.setSingleChoiceItems(
            list, -1
        ) { dialogInterface, i ->

            selectedItem = list[i]
            edt.setText(list[i])

            dialogInterface.dismiss()

        }
        builder.show()

        return selectedItem
    }


    @SuppressLint("SetTextI18n")
    private fun addJobLocation() {

        val dialog = CheckInternet.showAddJobLocationDialog(this)
        dialog.show()

        val state = dialog.findViewById<View>(R.id.stDiag) as EditText
        val district = dialog.findViewById<View>(R.id.distDiag) as EditText
        val areaName = dialog.findViewById<View>(R.id.areaName) as EditText

        (dialog.findViewById<EditText>(R.id.stDiag) as EditText).setOnClickListener {

            districtList = ArrayList<String>()

            district.setText("")
            (dialog.findViewById<ProgressBar>(R.id.distDiagPrg)).visibility = View.GONE
            showStateDialog(dialog)

        }

        (dialog.findViewById<EditText>(R.id.distDiag) as EditText).setOnClickListener {

            if(state.text.isNullOrBlank()){
                state.error = "Select State First"
                dialog.findViewById<EditText>(R.id.stDiag).requestFocus()
                return@setOnClickListener
            }

            showDistrictDialog(dialog, selectedState.toString())

        }

        (dialog.findViewById<View>(R.id.bt_close) as ImageButton).setOnClickListener { dialog.dismiss() }

        (dialog.findViewById<View>(R.id.bt_save) as Button).setOnClickListener {


            if(state.text.isNullOrBlank()){
                state.error  = "Select States"
                state.requestFocus()
                return@setOnClickListener
            }

            if(district.text.isNullOrBlank()){
                district.error = "Select District"
                district.requestFocus()
                return@setOnClickListener
            }

            if(areaName.text.isNullOrBlank()){
                areaName.error = "Enter Area Name"
                areaName.requestFocus()
                return@setOnClickListener
            }

            val selectedPlace = """
                State : ${state.text}
                District : ${district.text}
                Area/City : ${areaName.text}
            """.trimIndent()

            Log.e("JobLocation", selectedPlace.toString())
//            binding.shopAreaTxt.visibility = View.VISIBLE

//            serviceArea = "${state.text};${district.text};${areaName.text}"

            if(binding.jobLocation.text.isEmpty()){

                binding.jobLocation.setText(areaName.text.toString())

            }else{

                binding.jobLocation.append(", ${areaName.text}")

            }
            dialog.dismiss()

        }


    }

    private fun showStateDialog(dialog: Dialog) {


        val adapter = ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice, resources.getStringArray(R.array.stateList))
        val builder = MaterialAlertDialogBuilder(this, R.style.MatAlertBoxLight)
        builder.setTitle("Select State")

        builder.setSingleChoiceItems(
            adapter, -1
        ) { dialogInterface, i ->
            selectedState = adapter.getItem(i).toString()
            (dialog.findViewById<EditText>(R.id.stDiag) as EditText).setText(selectedState)

            if (CheckInternet.isNetworkAvailable(this)) {
                dialogInterface.dismiss()
                commonViewModel.getDistrictList(selectedState!!)

            } else {
                dialogInterface.dismiss()
                CheckInternet.showDialogNoInternet(this)
            }

        }
        (dialog.findViewById<ProgressBar>(R.id.stDiagPrg)).visibility = View.GONE

        builder.show()


    }

    private fun showDistrictDialog(dialog: Dialog, state: String) {


        if (CheckInternet.isNetworkAvailable(this)) {
            if(districtList.isEmpty()){
                (dialog.findViewById<ProgressBar>(R.id.distDiagPrg)).visibility = View.VISIBLE
            }
            else{
                Snackbar.make(this, binding.root, "Please Wait While we are getting List of States",
                    Snackbar.LENGTH_LONG).show()
            }
        } else {
            CheckInternet.showDialogNoInternet(this)
        }

        commonViewModel.districtListLiveData?.observe(this, Observer {
            Log.e("GettingDistrict", "Observing Data")

            if (it != null) {
                Log.e("GettingDistrict", "$it")

                districtList = it

                val adapter = ArrayAdapter<String>(this, android.R.layout.select_dialog_item, districtList)
                val builder = MaterialAlertDialogBuilder(this, R.style.MatAlertBoxLight)
                builder.setTitle("Select District")

                builder.setSingleChoiceItems(
                    adapter, -1
                ) { dialogInterface, i ->
                    (dialog.findViewById<EditText>(R.id.distDiag) as EditText).setText(adapter.getItem(i))

                    if (CheckInternet.isNetworkAvailable(this)) {
                        dialogInterface.dismiss()
//                        getFuelPrice(selectedState.toString(), adapter.getItem(i).toString(), dialog)
                    } else {
                        dialogInterface.dismiss()
                        CheckInternet.showDialogNoInternet(this)
                    }

                }
                (dialog.findViewById<ProgressBar>(R.id.distDiagPrg)).visibility = View.GONE
                builder.show()
            } else {

                Log.e("GettingDistrict", "Something went wrong while Getting District List ")

                Toast.makeText(this, "Something Went Wrong Click Again or Try Again Later", Toast.LENGTH_LONG).show()

            }

        })
    }

}