package com.scg.hrst.ui.activity

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.scg.hrst.R
import com.scg.hrst.databinding.ActivityEditProfielBinding
import com.scg.hrst.utils.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfielBinding
    private var uri: Uri? = null
    private lateinit var mToast: HrstCustomToast

    private var mobileNo : String? = null
    private var name : String? = null
    private var email : String? = null
    private var imgLink : String? = null
    private var currentDate : String? = null
    private var notProvided  = "Not Provided"

    private val TAG = "EditProfileActivity"

    private val mDocRef = FirebaseFirestore.getInstance()
    private val myStorRef = FirebaseStorage.getInstance().reference

    private lateinit var alertDialog: AlertDialog
    private var userImgName : String? = null
    private var userImgUri: Uri? = null
    private var userImgFile: File? = null

    private var userImgCode = 1111
    
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfielBinding.inflate(layoutInflater)
        setContentView(binding.root)


        init()
    }

    private fun init(){

        Tools.setSystemBarColor(this, R.color.Edu_app_background)
        Tools.setSystemBarLight(this)

        mobileNo = intent.getStringExtra("mobile_no").toString()
        Log.e(TAG, mobileNo.toString())

        if(mobileNo != "null"){

            binding.toolbar.tvTitle.text=getString(R.string.create_profile)
            binding.toolbar.ivBack.visibility = View.GONE

        } else{

            binding.toolbar.tvTitle.text=getString(R.string.edu_lbl_edit_profile)
            binding.toolbar.ivBack.onClick { onBackPressed() }

        }

        mToast = HrstCustomToast(this)

        binding.btnSaveProfile.onClick {
            if (CheckInternet.isNetworkAvailable(this@EditProfileActivity)) {

                checkInput()

            } else {
                CheckInternet.showDialogNoInternet(this@EditProfileActivity)
            }

//            showToast(getString(R.string.edu_toast_successfully_saved_profile))
//            launchActivity<HomepageActivity> {  }

        }

//        if (uri != null) binding.ivProfileImage.setImageURI(uri)


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


    private fun checkInput(){

        if(userImgUri == null){
            Snackbar.make(this, binding.root, "Please Upload you Profile Image", Snackbar.LENGTH_LONG).show()
            return
        }

        if(binding.firstName.text.isEmpty()){
            binding.firstName.error = "Enter First Name"
            binding.firstName.requestFocus()
            return
        }

        if(binding.lastName.text.isEmpty()){
            binding.lastName.error = "Enter Last Name"
            binding.lastName.requestFocus()
            return
        }

        name = "${binding.firstName.text} ${binding.lastName.text}"

        email = binding.emailId.text.toString()

        uploadImage()

    }

    private fun uploadDetails() {

//        val imgLink = uploadImage()
        currentDate = SimpleDateFormat("yyyy-MM-dd;HH:mm:ss.SSS").format(Date())


        val dataToSave = HashMap<String, Any>()
        dataToSave["mobile_no"] = mobileNo.toString()
        dataToSave["name"] = name.toString()
        dataToSave["email"] = email.toString()
        dataToSave["profile_pic"] = imgLink.toString()
        dataToSave["is_registered"] = "No"
        dataToSave["user_type"] = "Individual"
        dataToSave["first_login_date"] = currentDate.toString()
        dataToSave["registration_date"] = "Not Registered"
        dataToSave["address"] = notProvided
        dataToSave["dob"] = notProvided
        dataToSave["alt_mobile_no"] = notProvided
        dataToSave["aadhar_no"] = notProvided

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

    fun onClick(view: View) {

        when (view.id) {

            binding.ivEdit.id -> {
                ImagePicker.with(this).crop().start(userImgCode)
            }

            binding.ivProfileImage.id -> {
                ImagePicker.with(this).crop().start(userImgCode)
            }

        }
    }
}