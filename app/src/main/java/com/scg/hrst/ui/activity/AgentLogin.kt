package com.scg.hrst.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.ktx.Firebase
import com.scg.hrst.R
import com.scg.hrst.databinding.ActivityAgentLoginBinding
import java.util.concurrent.TimeUnit

class AgentLogin : AppCompatActivity() {

    private lateinit var binding : ActivityAgentLoginBinding
    private lateinit var  startForResult : ActivityResultLauncher<Intent>
    private val RC_SIGN_IN = 1110
    private lateinit var  mAuth :FirebaseAuth
    private var  currentUser : FirebaseUser? = null

    private var storedVerificationId : String? = null
    private lateinit var resendToken : PhoneAuthProvider.ForceResendingToken
    private var OTP : String? = null
    private var isMobileVerified = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgentLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

    }


    private fun init() {

    }

    private fun initComponents() {

    }

    fun onClick(view: View) {

        when(view.id) {
/*
            binding.signIn.id ->{
                signIn()
            }

            binding.createAccount.id ->{

                binding.loginlyt.visibility = View.GONE
                binding.aclyt.visibility = View.VISIBLE

            }
*/

            binding.sendOtp.id -> {
                mobileVerification(binding.mobile.text.toString())
                hideKeyboard(this)
            }

            binding.verifyOtp.id -> {
                val credential = PhoneAuthProvider.getCredential(
                    storedVerificationId!!, binding.otp.text.toString())
                signInWithPhoneAuthCredential(credential)
                hideKeyboard(this)

            }
        }

    }

/*
    private fun signIn() {

        val progress = ProgressBar(this)
        progress.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        )

        var alertDialog = MaterialAlertDialogBuilder(this) //, R.style.ThemeOverlay_App_MaterialAlertDialog
            .setTitle("Signing In")
            .setView(progress)
            .setMessage("Please Wait while we are verifying Email and Password...")
            .setCancelable(false)
            .show()

        mAuth.signInWithEmailAndPassword(binding.email.text.toString(), binding.password.text.toString())
            .addOnSuccessListener(OnSuccessListener {
                if(it.user != null){
                    currentUser = it.user
                    alertDialog.dismiss()
                    Log.e("FirebaseLogin", "Login Successful with Email ${currentUser?.email}  ${currentUser?.phoneNumber}  ${currentUser?.displayName}")
                    Log.e("FirebaseLogin", "Email Verified ${currentUser?.isEmailVerified}")
                }else{
                    Log.e("FirebaseLogin", "Something wrong happened ")
                }
            })
            .addOnFailureListener(OnFailureListener {

                alertDialog.dismiss()
                val resultMsg = it.localizedMessage
                if(resultMsg.contains("invalid", false)){
                    Log.e("FirebaseLogin", "Invalid password")

                }else{
                    Log.e("FirebaseLogin", "User Not Found")

                }
            })
    }
*/


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

//                binding.mobileProg.visibility = View.GONE

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
//                    binding.mobileProg.visibility = View.GONE
                    Snackbar.make(this@AgentLogin, binding.root, "Invalid Number", Snackbar.LENGTH_LONG).show()

                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
//                    binding.mobileProg.visibility = View.GONE
                    Snackbar.make(this@AgentLogin, binding.root, "Maximum OTP Verification Attempts Reached, Please try after some time"
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

                Snackbar.make(this@AgentLogin, binding.root, "OTP (One Time Password) has been sent to your Moblie Successfully", Snackbar.LENGTH_LONG).show()

                // Save verification ID and resending token so we can use them later

                Handler(Looper.getMainLooper()).postDelayed(Runnable {
                    storedVerificationId = verificationId
//                    binding.otp.text = null
                    binding.verifyOtp.visibility = View.VISIBLE
                    binding.otp.visibility = View.VISIBLE
                    resendToken = token

                },12000)


                Handler(Looper.getMainLooper()).postDelayed(Runnable {
                    if(!isMobileVerified){

//                        binding.mobileProg.visibility = View.GONE
//                        binding.mobileOtplyt.visibility = View.GONE
                        Snackbar.make(this@AgentLogin , binding.root,
                            "Validating Mobile Number Failed, Try Again!!", Snackbar.LENGTH_LONG).show()

                    }

                },60000)

            }

        }

//        val phoneNo = binding.mobile.text.toString()
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

//        binding.mobileProg.visibility = View.GONE

        mAuth.signInWithCredential(credential).addOnSuccessListener(OnSuccessListener {
            isMobileVerified = true
            Snackbar.make(this, binding.root, "Mobile No Verified Successfully!!", Snackbar.LENGTH_LONG).show()
            val intent = Intent(this, AgentRegistration::class.java)
            startActivity(intent)

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
