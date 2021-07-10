package com.scg.hrst.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.firestore.FirebaseFirestore
import com.scg.hrst.R
import com.scg.hrst.databinding.ActivityMobileSignUpBinding
import com.scg.hrst.utils.*
import java.util.concurrent.TimeUnit

class MobileSignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMobileSignUpBinding
    private var isMobileVerified = false
    private var storedVerificationId : String? = null
    private lateinit var resendToken : PhoneAuthProvider.ForceResendingToken
    private lateinit var mAuth : FirebaseAuth
    private val mDocRef = FirebaseFirestore.getInstance()

    private var mobileNo : String? = null
    val TAG = "MobileSignUp"



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMobileSignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.ivBack.visibility = View.GONE
        binding.toolbar.tvTitle.text = getString(R.string.lets_get_started)

        /*binding.btnMobile.onClick {
            launchActivity<EduVerificationActivity> {  }
        }*/

        init()

    }

    private fun init(){

        Tools.setSystemBarColor(this, R.color.Edu_app_background)
        Tools.setSystemBarLight(this)



       /* binding.btnMobile.onClick {

//            binding.mobileNoLyt.visibility = View.GONE
//            binding.OtpLyt.visibility = View.VISIBLE
            launchActivity<EditProfileActivity> {  }

        }*/

        setMobileOtp()



    }

    private fun initComponents(){

    }

    private fun setMobileOtp(){

        binding.mobileNo.addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {

                    if (s!!.length == 10) {

                        Log.e("MobileNo", "Mobile No have 10 characters")

                        hideKeyboard(this@MobileSignUpActivity)

                        if (CheckInternet.isNetworkAvailable(this@MobileSignUpActivity)) {
                            if (CheckInput.verifyMobileNo(binding.mobileNo.text.toString())) {
                                binding.mobileNo.error = CheckInput.errorMsg

                            } else {

                                mDocRef.collection("all_users").document("${binding.mobileNo.text}").get().addOnSuccessListener(OnSuccessListener {
                                    if(it.exists()){
                                        Log.e(TAG, "Mobile number Already Signed Up as ${it["name"]}")
                                        if(it["user_type"].toString() == "Individual"){

                                            launchActivity<IndividualDashboard> {}

                                        }else{

                                            launchActivity<RecruiterDashboard> {}

                                        }

                                    }else{
                                        Log.e(TAG, "Mobile number not Registered  ${it.data}")
                                        launchActivity<EditProfileActivity> {
                                            putExtra("mobile_no", binding.mobileNo.text.toString())
                                        }
                                    }
                                }).addOnFailureListener(OnFailureListener {
                                    Log.e(TAG, "Something went wrong ${it.message}")

                                })



                                /*if(!isMobileVerified){
                                    binding.mobileProg.visibility = View.VISIBLE
                                    mobileVerification(s.toString())
                                }*/
                            }
                        } else {
                            CheckInternet.showDialogNoInternet(this@MobileSignUpActivity)
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

        binding.PinView.addTextChangedListener(
            object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (s!!.length == 6) {

                        Log.e("OTP", "Otp have 6 characters")
                        hideKeyboard(this@MobileSignUpActivity)

                        if (CheckInternet.isNetworkAvailable(this@MobileSignUpActivity)) {

                            if(storedVerificationId != null ){

                                val credential = PhoneAuthProvider.getCredential(
                                    storedVerificationId!!, s.toString())
                                binding.otpProg.visibility = View.VISIBLE
                                signInWithPhoneAuthCredential(credential)
                            }


                        } else {
                            CheckInternet.showDialogNoInternet(this@MobileSignUpActivity)
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
                    Snackbar.make(this@MobileSignUpActivity, binding.root, "Invalid Number", Snackbar.LENGTH_LONG).show()

                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    binding.mobileProg.visibility = View.GONE
                    Snackbar.make(this@MobileSignUpActivity, binding.root, "Maximum OTP Verification Attempts Reached, Please try after some time"
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

                Snackbar.make(this@MobileSignUpActivity, binding.root, "OTP (One Time Password) has been sent to your Moblie Successfully", Snackbar.LENGTH_LONG).show()

                // Save verification ID and resending token so we can use them later

                Handler(Looper.getMainLooper()).postDelayed(Runnable {
                    if(!isMobileVerified) {
                        storedVerificationId = verificationId
                        binding.PinView.text = null
                        binding.titleTxt.text = resources.getString(R.string.enter_otp)
                        binding.toolbar.tvTitle.text = resources.getString(R.string.verification)
                        binding.mobileNoLyt.visibility = View.GONE
                        binding.OtpLyt.visibility = View.VISIBLE
                        resendToken = token
                    }
                },10000)


                Handler(Looper.getMainLooper()).postDelayed(Runnable {
                    if(!isMobileVerified){

                        binding.mobileProg.visibility = View.GONE
//                        binding.mobileOtplyt.visibility = View.GONE
                        Snackbar.make(this@MobileSignUpActivity, binding.root,
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

            launchActivity<EditProfileActivity> {
                putExtra("mobile_no", binding.mobileNo.text.toString())
            }

//            binding.mobileOtplyt.visibility = View.GONE
//            binding.otpProg.visibility = View.GONE

        }).addOnFailureListener(OnFailureListener {

            Log.e("SignInError", "${it.localizedMessage}   ${it.message}")

            Snackbar.make(this, binding.root, "OTP Not Valid!!", Snackbar.LENGTH_LONG).show()
//            binding.otpProg.visibility = View.GONE

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