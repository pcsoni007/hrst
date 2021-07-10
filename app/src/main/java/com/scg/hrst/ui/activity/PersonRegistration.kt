package com.scg.hrst.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.text.BoringLayout
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.scg.hrst.EduBaseActivity
import com.scg.hrst.R
import com.scg.hrst.databinding.ActivityPersonRegistrationBinding
import com.scg.hrst.utils.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class PersonRegistration : AppCompatActivity() {

    private lateinit var binding: ActivityPersonRegistrationBinding
    private val workList = ArrayList<String>()
    private var mProgress = 0
    private var mSize = 3
    private lateinit var mToast: HrstCustomToast

    private var storedVerificationId : String? = null
    private lateinit var resendToken : PhoneAuthProvider.ForceResendingToken
    private lateinit var mAuth : FirebaseAuth
    private var isMobileVerified = false

    private var name : String? = null
    private var mobileNo : String? = null
    private var alt_mobile_no : String? = null
    private var address : String? = null
    private var aadharNo : String? = null
    private var email : String? = null
    private var personType : String? = null
    private var notProvided  = "Not Provided"


    private var imgLink : String? = null
    private var currentDate : String? = null

    private val TAG = "PersonRegistration"

    private val mDocRef = FirebaseFirestore.getInstance()
    private val myStorRef = FirebaseStorage.getInstance().reference

    private lateinit var alertDialog: AlertDialog
    private var userImgName : String? = null
    private var userImgUri: Uri? = null
    private var userImgFile: File? = null

    private var userImgCode = 1111


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPersonRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
        initComponents()
    }

    private fun init(){

        Tools.setSystemBarColor(this, R.color.Edu_app_background)
        Tools.setSystemBarLight(this)

        mToast = HrstCustomToast(this)

        disableAll()
        binding.sBar.max = mSize

        binding.toolbar.tvTitle.text=getString(R.string.job_seeker_registration)
        binding.toolbar.ivBack.visibility= View.GONE

    }

    @SuppressLint("ResourceAsColor")
    private fun initComponents(){

        binding.nextBtn.onClick {
            clickNext()
        }
        binding.prevBtn.onClick {
            clickPrev()
        }

        binding.dob.onClick {
            CheckInternet.dialogDatePickerDob(binding.dob, this@PersonRegistration , supportFragmentManager)
        }


        setMobileOtp()

        binding.aadharNo.addTextChangedListener(object : SeparatorTextWatcher(' ', 4) {
            override fun onAfterTextChanged(text: String) {
                binding.aadharNo.run{
                    binding.aadharNo.setText(text)
                    setSelection(text.length)
                }
            }
        }

        )

    }

    private fun setMobileOtp(){

        binding.mobileNo.addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {

                    if (s!!.length == 11) {

                        Log.e("MobielNo", "Mobile No have 10 characters")

                        hideKeyboard(this@PersonRegistration)

                        if (CheckInternet.isNetworkAvailable(this@PersonRegistration)) {
                            if (CheckInput.verifyMobileNo(binding.mobileNo.text.toString())) {
                                binding.mobileNo.error = CheckInput.errorMsg

                            } else {

                                if(!isMobileVerified){
                                    binding.mobileProg.visibility = View.VISIBLE
                                    mobileVerification(s.toString())
                                }
                            }
                        } else {
                            CheckInternet.showDialogNoInternet(this@PersonRegistration)
                        }
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
//                    Log.e("MobileVerification", "Mobile No Not Changed")
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                    Log.e("MobileVerification", "Mobile No Changed")
                }
            }
        )

        binding.otp.addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s!!.length == 6) {

                        Log.e("OTP", "Otp have 6 characters")
                        hideKeyboard(this@PersonRegistration)

                        if (CheckInternet.isNetworkAvailable(this@PersonRegistration)) {

                            if(storedVerificationId != null ){

                                val credential = PhoneAuthProvider.getCredential(
                                    storedVerificationId!!, s.toString())
                                binding.otpProg.visibility = View.VISIBLE
                                signInWithPhoneAuthCredential(credential)
                            }


                        } else {
                            CheckInternet.showDialogNoInternet(this@PersonRegistration)
                        }
                    }
                }

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    Log.e("MobileVerification", "Mobile No Not Changed")
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    Log.e("MobileVerification", "Mobile No Changed")
                }
            }
        )
    }


    private fun setWorkList(num : Int){

        val array = when(num){

            1 ->{
                resources.getStringArray(R.array.skill_list)
            }

            2 ->{
                resources.getStringArray(R.array.no_skill_list)

            }

            3 ->{
                resources.getStringArray(R.array.semi_skill_list)

            }
            else -> {
                resources.getStringArray(R.array.not_selected)
            }
        }

        binding.addWork.onClick {
            val builder = MaterialAlertDialogBuilder(this@PersonRegistration)
            builder.setTitle("Types of Work")
            builder.setSingleChoiceItems(
                array, -1
            ) { dialogInterface, i ->

                val chip = Chip(this@PersonRegistration)

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


    private fun clickNext() {

        when (mProgress) {

            0 -> {

                if(personType != null){

                    mProgress++
                    binding.sBar.progress = mProgress
                    binding.userTypeLyt.visibility = View.GONE
                    binding.prevBtn.visibility = View.VISIBLE
                    binding.workTypeLyt.visibility = View.VISIBLE

                }else{
                    Snackbar.make(this@PersonRegistration, binding.root, "Select Type of Job you can do.", Snackbar.LENGTH_LONG).show()
                }

            }

            1 -> {
                if(workList.size > 0){
                    mProgress++
                    binding.sBar.progress = mProgress
                    binding.workTypeLyt.visibility = View.GONE
                    binding.personalInfoLyt.visibility = View.VISIBLE
                }else{
                    Snackbar.make(this@PersonRegistration, binding.root, "Add Type of $personType Job you can do.", Snackbar.LENGTH_LONG).show()
                }

            }

            2 -> {

                if(checkInput()){
                    mProgress++
                    binding.sBar.progress = mProgress
                    binding.personalInfoLyt.visibility = View.GONE
                    binding.termsAndCond.visibility = View.VISIBLE
                }

            }

            3 -> {

                if(binding.termsAndCondCheck.isChecked){

                    uploadImage()
                }else{
                    Snackbar.make(this, binding.root, "Click on the Check Box to agree with our Terms and Conditions for Completing Registration Process", Snackbar.LENGTH_LONG).show()
                }

            }

        }

    }

    private fun clickPrev() {

        when (mProgress) {

            1 -> {
                mProgress--
                binding.sBar.progress = mProgress
                binding.workTypeLyt.visibility = View.GONE
                binding.userTypeLyt.visibility = View.VISIBLE
                binding.prevBtn.visibility = View.GONE
            }

            2 -> {
                mProgress--
                binding.sBar.progress = mProgress
                binding.personalInfoLyt.visibility = View.GONE
                binding.workTypeLyt.visibility = View.VISIBLE
            }

            3 -> {
                mProgress--
                binding.sBar.progress = mProgress
                binding.termsAndCond.visibility = View.GONE
                binding.personalInfoLyt.visibility = View.VISIBLE
            }

        }

    }

    private fun enable(aImageView: ImageView) {
        aImageView.visibility = View.VISIBLE
    }

    private fun disableAll() {
        binding.t1.visibility = View.GONE
        binding.t2.visibility = View.GONE
        binding.t3.visibility = View.GONE
    }

    private fun disable(aImageView: ImageView) {

        aImageView.visibility = View.GONE

    }

    fun onClick(view: View) {

        when(view.id){

            binding.skilled.id ->  {
                enable(binding.t1)//binding.t1.visibility = View.VISIBLE
                disable(binding.t2)
                disable(binding.t3)
                binding.chipGroup.removeAllViews()
                setWorkList(1)
                personType = "Skilled"
            }
            binding.unskilled.id ->  {
                enable(binding.t2)//binding.t2.visibility = View.VISIBLE
                disable(binding.t1)
                disable(binding.t3)
                binding.chipGroup.removeAllViews()
                setWorkList(2)
                personType = "Unskilled"

            }
            binding.semiSkilled.id ->  {
                enable(binding.t3)//binding.t3.visibility = View.VISIBLE
                disable(binding.t1)
                disable(binding.t2)
                binding.chipGroup.removeAllViews()
                setWorkList(3)
                personType = "Semi Skilled"
            }

            binding.ivEdit.id -> {
                ImagePicker.with(this).crop().start(userImgCode)
            }

            binding.ivProfileImage.id -> {
                ImagePicker.with(this).crop().start(userImgCode)
            }
        }


    }


    private fun checkInput(): Boolean{

        if(userImgUri == null){
            Snackbar.make(this, binding.root, "Please Upload you Profile Image", Snackbar.LENGTH_LONG).show()
            return false
        }

        if (CheckInput.verifyName(binding.personName.text.toString())) {
            binding.personName.error =  CheckInput.errorMsg +" Name"
            binding.personName.requestFocus()
            return false
        }

        if (CheckInput.verifyAddress(binding.address.text.toString())) {
            binding.address.error = CheckInput.errorMsg
            binding.address.requestFocus()
            return false
        }

        if(binding.dob.text.isEmpty()){
            binding.dob.error = "Select Date of Birth"
            binding.dob.requestFocus()
            return false
        }

        if (isMobileVerified) {

            Snackbar.make(this, binding.root, "Please Enter your Mobile Number and Verify Mobile Number", Snackbar.LENGTH_LONG).show()

            /*binding.mobileNo.error = CheckInput.errorMsg
            binding.mobileNo.requestFocus()
            return false*/
        }


        if(binding.altMobileNo.text.isNotEmpty()){
            if (CheckInput.verifyMobileNo(binding.altMobileNo.text.toString())) {
                binding.altMobileNo.error = CheckInput.errorMsg
                binding.altMobileNo.requestFocus()
                return false
            }
        }

        if(binding.emailId.text.isNotEmpty()){
            if (CheckInput.verifyEmail(binding.emailId.text.toString())) {
                binding.emailId.error = ""
                binding.emailId.requestFocus()
                return false
            }
        }

        if (CheckInput.verifyAadhar(binding.aadharNo.text.toString(), this)) {
            binding.aadharNo.error = CheckInput.errorMsg
            binding.aadharNo.requestFocus()
            return false
        }

        name = "${binding.personName.text}"

        email = binding.emailId.text.toString()

        mobileNo = binding.mobileNo.text.toString()

        return true

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            userImgCode -> if (resultCode == RESULT_OK) {
                userImgUri = data?.data
//                binding.userImgTxt.error = null

                userImgFile = File(RealPathUtil.getRealPath(this, userImgUri).toString())
                val uriString: String = userImgUri.toString()
                val myFile = File(uriString)//ImgUri?.toFile()

                if (userImgUri.toString().startsWith("content://")) {
                    var cursor: Cursor? = null
                    try {
                        cursor =
                            this.contentResolver?.query(userImgUri!!, null, null, null, null)
                        if (cursor != null && cursor.moveToFirst()) {
                            userImgName =
                                cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                        }
                    } finally {
                        cursor?.close()
                    }
                } else if (uriString.startsWith("file://")) {
                    userImgName = myFile.name
                }

                userImgUri = data?.data
                binding.ivProfileImage.setImageURI(userImgUri)
//                binding.userImgTxt.text = userImgName
                Log.e("UserImg", data?.data.toString())
                Log.e("UserImg", userImgFile!!.name.toString())
            }
        }

        super.onActivityResult(requestCode, resultCode, data)

    }



    private fun uploadDetails() {

//        val imgLink = uploadImage()
        currentDate = SimpleDateFormat("yyyy-MM-dd;HH:mm:ss.SSS").format(Date())

        val dataToSave = HashMap<String, Any>()
        dataToSave["mobile_no"] = mobileNo.toString()
        dataToSave["name"] = name.toString()
        dataToSave["email"] = email.toString()
        dataToSave["profile_pic"] = imgLink.toString()
        dataToSave["is_registered"] = "Yes"
        dataToSave["user_type"] = "Individual"
        dataToSave["first_login_date"] = currentDate.toString()
        dataToSave["registration_date"] = currentDate.toString()
        dataToSave["address"] = binding.address.text.toString()
        dataToSave["dob"] = binding.dob.text.toString()
        dataToSave["aadhar_no"] = binding.aadharNo.text.toString()
        dataToSave["alt_mobile_no"] = notProvided
        dataToSave["person_type"] = personType.toString()
        dataToSave["work_list"] = workList

        mDocRef.document("all_users/$mobileNo").set(dataToSave).addOnSuccessListener(
            OnSuccessListener {
                Log.e(TAG, "Data Has Been Saved")
                showToast(getString(R.string.edu_toast_successfully_saved_profile))
                alertDialog.dismiss()

                launchActivity<IndividualDashboard> {  }

            }).addOnFailureListener(OnFailureListener {
            Log.e(TAG, "Data Has Not Been Saved due to ${it.message}")
            showToast("Server Down!! Please Try again Later")
            alertDialog.dismiss()
        })

    }

    private fun uploadImage() {

        val progress = ProgressBar(this)
        progress.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )

        alertDialog =  MaterialAlertDialogBuilder(this)
            .setTitle("Uploading Details...")
            .setCancelable(false)
            .setView(progress)
            .show()


        myStorRef.child("all_users/$mobileNo/").child("profile_pic").putFile(userImgUri!!)
            .addOnSuccessListener {
                Log.e("UploadImg", "Finished")
//                Toast.makeText(this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                alertDialog.dismiss()
                Log.e("UploadImg", "Failed ${e.message}")
//                Toast.makeText(this, "Failed " + e.message, Toast.LENGTH_SHORT).show()
            }
            .addOnProgressListener { taskSnapshot ->
                val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot
                    .totalByteCount
                alertDialog.setMessage("Uploaded " + progress.toInt() + "%")

            }.addOnCompleteListener {
                myStorRef.child("all_users").child("$mobileNo").child("profile_pic").downloadUrl.addOnSuccessListener(OnSuccessListener {
                    Log.e("UploadedImg", it.toString())
                    imgLink = it.toString()
                    uploadDetails()
//                        alertDialog.dismiss()
                }).addOnFailureListener(OnFailureListener {

                    alertDialog.dismiss()
                    Snackbar.make(this, binding.root, "Server Down!! Please Try Again Later", Snackbar.LENGTH_LONG).show()

                })
            }
    }



    private fun showToast(aContent: String) {
        mToast.setCustomView(aContent)
        mToast.duration = Toast.LENGTH_SHORT
        mToast.show()
    }

    private fun mobileVerification(number: String) {

        mAuth = FirebaseAuth.getInstance()

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.

                binding.mobileProg.visibility = View.GONE

                Log.e("MobileVerification", "onVerificationCompleted:$credential")
                signInWithPhoneAuthCredential(credential)
            }

            @SuppressLint("ShowToast")
            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w("MobileVerification", "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    binding.mobileProg.visibility = View.GONE
                    Snackbar.make(this@PersonRegistration, binding.root, "Invalid Number", Snackbar.LENGTH_LONG).show()

                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    binding.mobileProg.visibility = View.GONE
                    Snackbar.make(this@PersonRegistration, binding.root, "Maximum OTP Verification Attempts Reached, Please try after some time"
                        , Snackbar.LENGTH_LONG).show()

                }

                // Show a message and update the UI
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.e("MobileVerification", "onCodeSent:$verificationId")

                Snackbar.make(this@PersonRegistration, binding.root, "OTP (One Time Password) has been sent to your Mobile Number Successfully", Snackbar.LENGTH_LONG).show()

                // Save verification ID and resending token so we can use them later

                Handler(Looper.getMainLooper()).postDelayed(Runnable {
                    if(!isMobileVerified) {
                        storedVerificationId = verificationId
                        binding.otp.text = null
                        binding.OtpLyt.visibility = View.VISIBLE
                        resendToken = token
                    }
                },10000)


                Handler(Looper.getMainLooper()).postDelayed(Runnable {
                    if(!isMobileVerified){

                        binding.mobileProg.visibility = View.GONE
                        binding.otpProg.visibility = View.GONE
                        Snackbar.make(this@PersonRegistration, binding.root,
                            "Validating Mobile Number Failed, Try Again!!", Snackbar.LENGTH_LONG).show()

                    }

                },60000)
            }

        }
        mobileNo = number

        Log.e("MobileVerification", "+91$number")

        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber("+91$number") // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

        binding.mobileProg.visibility = View.GONE

        mAuth.signInWithCredential(credential).addOnSuccessListener(OnSuccessListener {
            isMobileVerified = true
            Snackbar.make(this, binding.root, "Mobile Number Verified Successfully!!", Snackbar.LENGTH_LONG).show()

            binding.OtpLyt.visibility = View.GONE
            binding.otpProg.visibility = View.GONE


        }).addOnFailureListener(OnFailureListener {

            Log.e("SignInError", "${it.localizedMessage}   ${it.message}")

            Snackbar.make(this, binding.root, "OTP Not Valid!!", Snackbar.LENGTH_LONG).show()
            binding.otpProg.visibility = View.GONE

        })

    }


    fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }




}