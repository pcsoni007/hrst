package com.scg.hrst.ui.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.scg.hrst.R
import com.scg.hrst.data.models.BankDetails
import com.scg.hrst.databinding.ActivityAgentRegistrationBinding
import com.scg.hrst.utils.CheckInput
import com.scg.hrst.utils.CheckInternet
import com.scg.hrst.utils.Config
import com.scg.hrst.utils.SeparatorTextWatcher
import com.scg.hrst.viewmodels.AgentViewModel
import java.io.File
import java.util.*
import java.util.concurrent.Executors
import javax.activation.DataHandler
import javax.activation.DataSource
import javax.activation.FileDataSource
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

class AgentRegistration : AppCompatActivity() {
    private lateinit var binding : ActivityAgentRegistrationBinding
    private lateinit var  startForResult : ActivityResultLauncher<Intent>
    private val RC_SIGN_IN = 1110

    private var agentViewModel : AgentViewModel = AgentViewModel()

    private var cBankDetails : BankDetails? = null
    private val aadharPicCode = 1111

    private var aadharImgName : String? = null
    private var aadharImgUri: Uri? = null
    private var aadharImgFile: File? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgentRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initComponents()

    }



    private fun init(){


    }

    private fun initComponents(){


        binding.aadharNo.addTextChangedListener(object : SeparatorTextWatcher(' ', 4) {
            override fun onAfterTextChanged(text: String) {
                binding.aadharNo.run{
                    binding.aadharNo.setText(text)
                    setSelection(text.length)
                }
            }
        }

        )

        binding.ifscCode.addTextChangedListener(
            object : TextWatcher {

                override fun afterTextChanged(s: Editable?) {

                    if(s!!.length == 11){
                        Log.e("IFSC", "Ifsc have 11 characters")

                        if(CheckInternet.isNetworkAvailable(this@AgentRegistration)){
                            if(CheckInput.verifyIfscCode(binding.ifscCode.text.toString())){
                                binding.ifscCode.error = CheckInput.errorMsg
                            }else{
                                binding.ifscProgress.visibility = View.VISIBLE
                                getBankDetails(s.toString())
                            }
                        }
                        else{
                            CheckInternet.showDialogNoInternet(this@AgentRegistration)
                        }

                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                    Log.e("IFSC", "Ifsc Not Changed")
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    Log.e("IFSC", "Ifsc Changed")
                }
            }
        )

    }


    private fun getBankDetails(Ifsc : String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.ifscCode.setTextColor(getColor(R.color.grey_40))
            Snackbar.make(this, binding.root , "Please Wait we are fetching Bank Details for you.......", Snackbar.LENGTH_LONG)
                .setBackgroundTint(getColor( R.color.white))
                .setTextColor(getColor(R.color.black))
                .setMaxInlineActionWidth(200)
                .show()
        }
        else{
            Snackbar.make(this, binding.root, "Please Wait we are fetching Bank Details for you.......", Snackbar.LENGTH_LONG)
                .show()
        }

        agentViewModel.getBankDetails(Ifsc)


        agentViewModel.bankDetailsLiveData?.observe(this, Observer {
            Log.e("BankDetails", "Observing Data")

            if (it.BANK != "null") {
                Log.e("BankDetails", "$it")
//                Toast.makeText(this, "Bank", Toast.LENGTH_LONG).show()
//                alertDialog.dismiss()
                cBankDetails = it
                setBankDetails(cBankDetails!!)



            } else {
                Log.e("BankDetails", "Something went wrong  $it")

                binding.ifscCode.error = "Not a Valid IFSC Code"
                binding.ifscCode.requestFocus()

//                Snackbar.make(this,binding.root, "Please Enter a ", Snackbar.LENGTH_LONG).show()
//                alertDialog.dismiss()

            }
            binding.ifscCode.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding.ifscProgress.visibility = View.GONE


        })


    }

    private fun setBankDetails(bankDetails: BankDetails){

//        binding
        val bankName : String = bankDetails.BANK
        val branchName : String = bankName + " " + bankDetails.BRANCH + " " + bankDetails.CITY + " " + bankDetails.STATE
        binding.bankName.text = branchName

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            aadharPicCode -> {
                if(resultCode == Activity.RESULT_OK){
                    //Image Uri will not be null for RESULT_OK
                    val uri: Uri = data?.data!!

                    // Use Uri object instead of File to avoid storage permissions

                    val displayname = uri.pathSegments[(uri.pathSegments.size - 1)]
                    Log.e("ImageSelection", displayname)

                    aadharImgUri = uri
                    aadharImgName = displayname
                    aadharImgFile = File(aadharImgUri!!.path.toString())

                    binding.aadharImg.setImageURI(uri)
                    binding.aadharImgTxt.text = displayname
                    Log.e("ImageSelection", "${uri.path}")

                }
                else{
                    Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                    /*ImagePicker.RESULT_ERROR -> {*/
                    } /**/
                }


            else -> {
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }



    }


    fun onClick(view: View) {

        when(view.id){

            binding.aadharPicCard.id ->{

                ImagePicker.with(this)
                    .crop()
                    .start(aadharPicCode)

            }

            binding.submit.id -> {
                sendEmail()
            }
        }
    }



    private fun sendEmail() {

        val subject = "Email Attachment Testing"
        val message: String = """
            this mail is just for email attachment testing.
            Attached File Name is $aadharImgName
            Attached File Path is ${aadharImgFile?.absolutePath}
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

                mm.setRecipient(Message.RecipientType.TO, InternetAddress(Config.EMAIL))
                mm.subject = subject

//                mm.setText(message)

                val messageBodyPart: BodyPart = MimeBodyPart()
                messageBodyPart.setText(message)

                var multipart = MimeMultipart()

                val source1: DataSource = FileDataSource(aadharImgFile)
                messageBodyPart.dataHandler = DataHandler(source1)
                messageBodyPart.fileName = aadharImgName
                multipart.addBodyPart(messageBodyPart)
                mm.setContent(multipart)

                Transport.send(mm)

                Log.e("Mail", "Mail Sent")
                Snackbar.make(binding.root, "Request to Map Vehicle Sent Successfully.", Snackbar.LENGTH_SHORT).show()



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