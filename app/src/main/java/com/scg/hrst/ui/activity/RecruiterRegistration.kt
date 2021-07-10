package com.scg.hrst.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
import com.scg.hrst.R
import com.scg.hrst.databinding.ActivityRecruiterRegistrationBinding
import com.scg.hrst.utils.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import kotlin.collections.ArrayList
import kotlin.random.Random

class RecruiterRegistration : AppCompatActivity() {

    private lateinit var binding: ActivityRecruiterRegistrationBinding

    private val workList = ArrayList<String>()
    private var mProgress = 0
    private var mSize = 4
    private lateinit var mToast: HrstCustomToast

    private var storedVerificationId : String? = null
    private lateinit var resendToken : PhoneAuthProvider.ForceResendingToken
    private lateinit var mAuth : FirebaseAuth
    private var isMobileVerified = false

    private var otp : String? = null
    private var name : String? = null
    private var contactPersonName : String? = null
    private var mobileNo : String? = null
    private var altMobileNo : String? = null
    private var email : String? = null

    private var address : String? = null
    private var aadharNo : String? = null
    private var recruiterType : String? = null
    private var notProvided  = "Not Provided"

    private var imgLink : String? = null
    private var currentDate : String? = null

    private val TAG = "RecruiterRegistration"

    private val mDocRef = FirebaseFirestore.getInstance()
    private val myStorRef = FirebaseStorage.getInstance().reference

    private lateinit var alertDialog: AlertDialog
    private var recruiterImgName : String? = null
    private var recruiterImgUri: Uri? = null
    private var recruiterImgFile: File? = null

    private var recruiterImgCode = 1111


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecruiterRegistrationBinding.inflate(layoutInflater)
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

        binding.toolbar.tvTitle.text=getString(R.string.recruiter_registration)
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
        binding.skip.onClick {
            launchActivity<MobileSignUpActivity> {  }
        }

        setWorkList()

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

                        hideKeyboard(this@RecruiterRegistration)

                        if (CheckInternet.isNetworkAvailable(this@RecruiterRegistration)) {
                            if (CheckInput.verifyMobileNo(binding.mobileNo.text.toString())) {
                                binding.mobileNo.error = CheckInput.errorMsg

                            } else {

                                if(!isMobileVerified){
                                    binding.mobileProg.visibility = View.VISIBLE
                                    mobileVerification(s.toString())
                                }
                            }
                        } else {
                            CheckInternet.showDialogNoInternet(this@RecruiterRegistration)
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
                        hideKeyboard(this@RecruiterRegistration)

                        if (CheckInternet.isNetworkAvailable(this@RecruiterRegistration)) {

                            if(storedVerificationId != null ){

                                val credential = PhoneAuthProvider.getCredential(
                                    storedVerificationId!!, s.toString())
                                binding.otpProg.visibility = View.VISIBLE
                                signInWithPhoneAuthCredential(credential)
                            }


                        } else {
                            CheckInternet.showDialogNoInternet(this@RecruiterRegistration)
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

    private fun setWorkList(){

        val array = resources.getStringArray(R.array.skill_list)

        binding.addWork.onClick {
            val builder = MaterialAlertDialogBuilder(this@RecruiterRegistration)
            builder.setTitle("Types of Work")
            builder.setSingleChoiceItems(
                array, -1
            ) { dialogInterface, i ->

                val chip = Chip(this@RecruiterRegistration)

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
                if(recruiterType != null){

                    mProgress++
                    binding.sBar.progress = mProgress
                    binding.recruiterType.visibility = View.GONE
                    binding.prevBtn.visibility = View.VISIBLE
                    binding.companyInfoLyt.visibility = View.VISIBLE

                }else{
                    Snackbar.make(this , binding.root, "Please Select which are you ?", Snackbar.LENGTH_LONG).show()
                }

            }

            1 -> {
                if(checkRecDetailsInput()){
                    mProgress++
                    binding.sBar.progress = mProgress
                    binding.companyInfoLyt.visibility = View.GONE
                    binding.prevBtn.visibility = View.VISIBLE
                    binding.contactDetailsLyt.visibility = View.VISIBLE
                }
            }

            2 -> {

                if(checkContactDetailsInput()){
                    mProgress++
                    binding.sBar.progress = mProgress
                    binding.contactDetailsLyt.visibility = View.GONE
                    binding.workTypeLyt.visibility = View.VISIBLE
                }


            }

            3 -> {
                if(workList.size > 0){
                    mProgress++
                    binding.sBar.progress = mProgress
                    binding.workTypeLyt.visibility = View.GONE
                    binding.termsAndCond.visibility = View.VISIBLE
                }else{
                    Snackbar.make(this, binding.root, "Add Type of Work for which you need People.", Snackbar.LENGTH_LONG).show()
                }
            }

            4 -> {

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
                binding.companyInfoLyt.visibility = View.GONE
                binding.recruiterType.visibility = View.VISIBLE
                binding.prevBtn.visibility = View.GONE
            }

            2 -> {

                mProgress--
                binding.sBar.progress = mProgress
                binding.contactDetailsLyt.visibility = View.GONE
                binding.companyInfoLyt.visibility = View.VISIBLE
            }

            3 -> {

                mProgress--
                binding.sBar.progress = mProgress
                binding.workTypeLyt.visibility = View.GONE
                binding.contactDetailsLyt.visibility = View.VISIBLE
            }

            4 -> {

                mProgress--
                binding.sBar.progress = mProgress
                binding.termsAndCond.visibility = View.GONE
                binding.workTypeLyt.visibility = View.VISIBLE

            }

        }

    }

    private fun enable(aImageView: ImageView) {
        aImageView.visibility = View.VISIBLE
    }

    private fun disableAll() {
        binding.t1.visibility = View.GONE
        binding.t2.visibility = View.GONE
    }

    private fun disable(aImageView: ImageView) {

        aImageView.visibility = View.GONE
    }

    fun onClick(view: View) {

        when(view.id){

            binding.companyCard.id ->  {

                enable(binding.t1)
                disable(binding.t2)
                recruiterType = "Company"

                binding.contPersonName.visibility = View.VISIBLE
                binding.comDetailsTxt.text = resources.getString(R.string.company_details)
                binding.comContDetailsTxt.text = resources.getString(R.string.company_contact_details)
                binding.typeWorkTxt.text = resources.getString(R.string.type_of_work_company_do)

            }

            binding.individualCard.id ->  {

                enable(binding.t2)
                disable(binding.t1)

                recruiterType = "Individual Recruiter"

                binding.contPersonName.visibility = View.GONE
                binding.comDetailsTxt.text = resources.getString(R.string.recruiter_details)
                binding.comContDetailsTxt.text = resources.getString(R.string.recruiter_contact_details)
                binding.typeWorkTxt.text = resources.getString(R.string.recruiter_work_details)

            }

            binding.ivEdit.id -> {
                ImagePicker.with(this).crop().start(recruiterImgCode)
            }

            binding.ivProfileImage.id -> {
                ImagePicker.with(this).crop().start(recruiterImgCode)
            }
        }

    }


    private fun checkRecDetailsInput(): Boolean{

        if(recruiterImgUri == null){
            Snackbar.make(this, binding.root, "Please Upload you Profile Image", Snackbar.LENGTH_LONG).show()
            return false
        }

        if (CheckInput.verifyName(binding.name.text.toString())) {
            binding.name.error =  CheckInput.errorMsg +" Name"
            binding.name.requestFocus()
            return false
        }

        if(recruiterType == "Company"){

            if (CheckInput.verifyName(binding.contPersonName.text.toString())) {
                binding.contPersonName.error =  CheckInput.errorMsg +" Contact Person Name"
                binding.contPersonName.requestFocus()
                return false
            }

        }

        if (CheckInput.verifyAddress(binding.address.text.toString())) {
            binding.address.error = CheckInput.errorMsg
            binding.address.requestFocus()
            return false
        }


        if (CheckInput.verifyPan(binding.pan.text.toString())) {
            binding.pan.error = CheckInput.errorMsg
            binding.pan.requestFocus()
            return false
        }

        if(recruiterType == "Company"){

            if (CheckInput.verifyGst(binding.gst.text.toString(), binding.pan.text.toString())) {
                binding.gst.error = CheckInput.errorMsg
                binding.gst.requestFocus()
                return false
            }
        }

        if (CheckInput.verifyAadhar(binding.aadharNo.text.toString(), this)) {
            binding.aadharNo.error = CheckInput.errorMsg
            binding.aadharNo.requestFocus()
            return false
        }

//        getDetails()

        return true

    }

    private fun checkContactDetailsInput(): Boolean{

        if (isMobileVerified) {

            Snackbar.make(this, binding.root, "Please Enter your Mobile Number and Verify Mobile Number", Snackbar.LENGTH_LONG).show()

        }


        if(binding.altMobileNo.text.isNotEmpty()){
            if (CheckInput.verifyMobileNo(binding.altMobileNo.text.toString())) {
                binding.altMobileNo.error = CheckInput.errorMsg
                binding.altMobileNo.requestFocus()
                return false
            }
        }

        if (CheckInput.verifyEmail(binding.emailId.text.toString())) {
            binding.emailId.error = CheckInput.errorMsg
            binding.emailId.requestFocus()
            return false
        }


        if(binding.website.text.isNotEmpty()){
            if (!Patterns.WEB_URL.matcher(binding.website.text.toString()).matches()) {
                binding.website.error = "Enter a Valid Web Site Address"
                binding.website.requestFocus()
                return false
            }
        }

        mobileNo = binding.mobileNo.text.toString()

        return true
    }



    private fun uploadDetails() {

//        val imgLink = uploadImage()
        currentDate = SimpleDateFormat("yyyy-MM-dd;HH:mm:ss.SSS").format(Date())

        val dataToSave = HashMap<String, Any>()
        dataToSave["mobile_no"] = binding.mobileNo.text.toString()
        dataToSave["alt_mobile_no"] = binding.altMobileNo.text.toString()
        dataToSave["name"] = binding.name.text.toString()
        dataToSave["email"] = binding.emailId.text.toString()
        dataToSave["profile_pic"] = imgLink.toString()
        dataToSave["is_registered"] = "Yes"
        dataToSave["user_type"] = "$recruiterType"
        dataToSave["first_login_date"] = currentDate.toString()
        dataToSave["registration_date"] = currentDate.toString()
        dataToSave["address"] = binding.address.text.toString()
        dataToSave["contact_person_name"] = binding.contPersonName.text.toString()
        dataToSave["pan"] = binding.pan.text.toString()
        dataToSave["gst"] = binding.gst.text.toString()
        dataToSave["aadhar_no"] = binding.aadharNo.text.toString()
        dataToSave["website"] = binding.website.text.toString()
        dataToSave["work_list"] = workList

        mDocRef.document("all_users/$mobileNo").set(dataToSave).addOnSuccessListener(
            OnSuccessListener {
                Log.e(TAG, "Data Has Been Saved")
                showToast(getString(R.string.edu_toast_successfully_saved_profile))
                alertDialog.dismiss()

                launchActivity<RecruiterDashboard> {  }

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


        myStorRef.child("all_users/$mobileNo/").child("profile_pic").putFile(recruiterImgUri!!)
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            recruiterImgCode -> if (resultCode == RESULT_OK) {
                recruiterImgUri = data?.data
//                binding.recruiterImgTxt.error = null

                recruiterImgFile = File(RealPathUtil.getRealPath(this, recruiterImgUri).toString())
                val uriString: String = recruiterImgUri.toString()
                val myFile = File(uriString)//ImgUri?.toFile()

                if (recruiterImgUri.toString().startsWith("content://")) {
                    var cursor: Cursor? = null
                    try {
                        cursor =
                            this.contentResolver?.query(recruiterImgUri!!, null, null, null, null)
                        if (cursor != null && cursor.moveToFirst()) {
                            recruiterImgName =
                                cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                        }
                    } finally {
                        cursor?.close()
                    }
                } else if (uriString.startsWith("file://")) {
                    recruiterImgName = myFile.name
                }

                recruiterImgUri = data?.data
                binding.ivProfileImage.setImageURI(recruiterImgUri)
//                binding.recruiterImgTxt.text = recruiterImgName
                Log.e("recruiterImg", data?.data.toString())
                Log.e("recruiterImg", recruiterImgFile!!.name.toString())
            }
        }

        super.onActivityResult(requestCode, resultCode, data)

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
                    Snackbar.make(this@RecruiterRegistration, binding.root, "Invalid Number", Snackbar.LENGTH_LONG).show()

                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    binding.mobileProg.visibility = View.GONE
                    Snackbar.make(this@RecruiterRegistration, binding.root, "Maximum OTP Verification Attempts Reached, Please try after some time"
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

                Snackbar.make(this@RecruiterRegistration, binding.root, "OTP (One Time Password) has been sent to your Mobile Number Successfully", Snackbar.LENGTH_LONG).show()

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
                        Snackbar.make(this@RecruiterRegistration, binding.root,
                            "Validating Mobile Number Failed, Try Again!!", Snackbar.LENGTH_LONG).show()

                    }

                },60000)


            }

        }

        val phoneNo = binding.mobileNo.text.toString()
        Log.e("MobileVerification", "+91$phoneNo")

        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber("+91$phoneNo") // Phone number to verify
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

    fun sendEmail() {

        otp = Random.nextInt(from = 100000, until = 999999).toString()
        val subject = "HRST Email Verification"
        val message: String = """
            Dear $contactPersonName,
            One Time Password to verify your Email ID is $otp
        """.trimIndent()

        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executor.execute {
            //Creating properties
            val props = Properties()

            props["mail.smtp.host"] = "sh028.webhostingservices.com"
            props["mail.smtp.socketFactory.port"] = "465"
            props["mail.smtp.socketFactory.class"] = "javax.net.ssl.SSLSocketFactory"
            props["mail.smtp.auth"] = "true"
            props["mail.smtp.socketFactory.fallback"] = "false"
            props["mail.smtp.port"] = "465"
            props.setProperty("mail.smtp.quitwait", "false")


            //Creating a new session
            val session = Session.getDefaultInstance(props, object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    Log.e("Mail", "Getting Mail ID")
                    return PasswordAuthentication(Config.EMAIL, Config.PASSWORD)
                }
            })
            try {
                //Creating MimeMessage object
                val mm = MimeMessage(session)
                Log.e("Mail", "Mail in Progress")

                mm.setFrom(InternetAddress(Config.EMAIL))

                mm.setRecipient(Message.RecipientType.TO, InternetAddress("soniprakhar9@gmail.com"))
                mm.subject = subject

                mm.setText(message)
                Transport.send(mm)

                Log.e("Mail", "Mail Sent to Truck Owner")



            } catch (e: MessagingException) {
                e.printStackTrace()
                Log.e("Mail", "${e.printStackTrace()}")
                Log.e("Mail", "${e.message}")
            }
            handler.post {

            }
        }

    }


}