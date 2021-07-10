package com.scg.hrst.utils

import android.content.Context
import android.util.Patterns
import com.scg.hrst.R

public object CheckInput {

    var errorMsg : String? = null
    val nameRegEx = "([a-zA-Z\\s]{3,30})".toRegex()
//    val bankNameRegEx = "([a-zA-Z\\s]{3,30})".toRegex()
    val addressRegEx = "^\\w+(?:(?:,\\s\\w+)+|(?:\\s\\w+)+)$".toRegex()  //"([\\w\\s,]{3,100})".toRegex()
    val mobileRegEx = "([6-9][0-9]{9})".toRegex()
    val aadharRegEx = "([1-9][0-9]{3}[ ][0-9]{4}[ ][0-9]{4})".toRegex()
//    val acNoRegEx = "([1-9][0-9]{3}[ ][0-9]{4}[ ][0-9]{4})".toRegex()
    val panRegEx = "([A-Z]{3}[ABCFGHLJPTF]{1}[A-Z]{1}[0-9]{4}[A-Z]{1})".toRegex()
    val ifscRegEx = "([A-Z]{4}0([A-Z]{6}|[0-9]{6}))".toRegex()
    val gstRegEx = "([0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[Z]{1}[0-9a-zA-Z]{1})".toRegex()
    val dlRegEx = "^(([A-Z]{2}[0-9]{2})( )|([A-Z]{2}-[0-9]{2}))((19|20)[0-9][0-9])[0-9]{7}$".toRegex()
    val vehicleNoRegEx = "^[A-Z]{2}[0-9]{1,2}[A-Z]{1,2}[0-9]{4}\$".toRegex()


    public  fun verifyName(name : String): Boolean {
        if(name.isBlank()) {
            errorMsg = " Enter "
            return true
        }
        else{
            if(name.toString().length < 3){
                errorMsg = " should have at least 3 Characters"
                return true

            }
            if(!name.toString().matches(nameRegEx)){
                errorMsg = " is not valid, Cannot contain any special character execept Space(\" \")"
                return true

            }
        }
        return false
    }

    public  fun verifyAddress(address : String): Boolean {
        if(address.isBlank()) {
            errorMsg = "Address Field cannot be Empty"
            return true
        }
        else{
            if(address.toString().length < 3){
                errorMsg = "Address should have at least 3 Characters"
                return true

            }
            if(!address.toString().matches(addressRegEx)){
                errorMsg = "Address is not valid, Address field cannot contain any special characters except Space(\" \") and Comma(\',\')"
                return true

            }
        }
        return false
    }



    fun verifyEmail(Email : String): Boolean{

        if (Email.isBlank()) {
            errorMsg = "Enter Email ID"
            return true
        } else {
            if (!Email.toString().matches((Patterns.EMAIL_ADDRESS).toRegex())) {
                errorMsg = "Enter a Valid Email ID"
                return true
            }
        }
        return false
    }

    fun verifyAadhar(Aadhar : String, context: Context): Boolean{

        if (Aadhar.isBlank()) {
            errorMsg = "Aadhar No field cannot be Empty"
            return true
        } else {

            when(Aadhar){
                in context.resources.getStringArray(R.array.invalid_aadhar) ->{
                    errorMsg = "Enter a Valid Aadhar No"
                    return true
                }
            }

            if (!Aadhar.toString().matches(aadharRegEx) ) {
                errorMsg = "Enter a Valid Aadhar No in XXXX XXXX XXXX Format"
                return true
            }
        }
        return false
    }

    fun verifyPan(Pan: String): Boolean {
        if (Pan.isBlank()) {
            errorMsg = "Pan field cannot be Empty"
            return true
        } else {
            if (!Pan.toString().matches(panRegEx)) {
                errorMsg = "PAN is not Valid"
                return true
            }
        }
        return false
    }


    fun verifyMobileNo(MobileNumber: String) : Boolean{
        if (MobileNumber.isBlank()) {
            errorMsg = "Mobile No field cannot be Empty"
            return true
        } else {
            if (!MobileNumber.toString().matches(mobileRegEx)) {
                errorMsg = "Mobile No is not Valid"
                return true

            }
        }
        return false
    }

    fun verifyIfscCode(Ifsc: String): Boolean {
        if (Ifsc.isBlank()) {
            errorMsg = "IFSC Code field cannot be Empty"
            return true
        } else {
            if (!Ifsc.toString().matches(ifscRegEx) ) {
                errorMsg = "Enter a Valid IFSC Code"
                return true
            }
        }
        return false

    }

    fun verifyAccountNumber(AccountNumber: String): Boolean {
        if (AccountNumber.isBlank()) {
            errorMsg = "Account No field cannot be Empty"
            return true
        } else {
            if (AccountNumber.length < 9 || AccountNumber.subSequence(0,0) == "0") {
                errorMsg = "Account No is not Valid"
                return true
            }
        }
        return false

    }


    fun verifyVehicleNo(vehicleNo : String) : Boolean{
        if (vehicleNo.isBlank()) {
            errorMsg = "Vehicle Registration Number field cannot be Empty"
            return true
        } else {

            if (!vehicleNo.toString().matches(vehicleNoRegEx)) {
                errorMsg = "Please Enter a valid Vehicle Number "//
                return true
            }

        }

        return false
    }

    fun verifyLoadingCap(load : String) : Boolean{
        if (load.isBlank()) {
            errorMsg = "Vehicle Loading Capacity field cannot be Empty"
            return true
        } else {

            if (load.toInt() < 1 || load.toInt() > 58) {
                errorMsg = "Vehicle Loading Capacity is not Valid"
                return true
            }

        }

        return false
    }


    fun verifyDrivingLicence(dl : String) : Boolean{
        if (dl.isBlank()) {
            errorMsg = "Driving Licence Number field cannot be Empty"
            return true
        } else {

            if (!dl.toString().matches(dlRegEx)) {
                errorMsg = "Driving Licence Number is not Valid"
                return true
            }

        }

        return false
    }

    fun verifyGst(gst : String, pan : String): Boolean{
        if (gst.isBlank()) {
            errorMsg = "GST Number field cannot be Empty"
            return true
        } else {

            if (!gst.toString().matches(gstRegEx)) {
                errorMsg = "GST Number is not Valid"
                return true
            }
            if(gst.substring(2,12) != pan){
                errorMsg = "GST Number does not verify with PAN "
                return true
            }
        }

        return false
    }

    fun verifyChassis(chassisNo: String): Boolean {
        val st = false

        return st
    }

}
