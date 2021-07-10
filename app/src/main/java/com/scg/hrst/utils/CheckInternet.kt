package com.scg.hrst.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.fragment.app.FragmentManager
import com.scg.hrst.R
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import java.util.*

object CheckInternet {

    fun showDialogNoInternet(context: Context) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
        dialog.setContentView(R.layout.no_internet)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)
        dialog.show()

        dialog.findViewById<View>(R.id.btnRetry).setOnClickListener {

            dialog.findViewById<View>(R.id.lnLy).visibility = View.GONE
            dialog.findViewById<View>(R.id.prgCard).visibility = View.VISIBLE

            Log.e("NoInternetDialog", "prgCard")
            if(isNetworkAvailable(context)){
                dialog.dismiss()
            }
            else{
                Log.e("NoInternetDialog", "No Internet Card")
            }
        }

    }


    fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) return false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                connectivityManager.activeNetwork
            } else {
                TODO("VERSION.SDK_INT < M")
            }
            if (activeNetworkInfo != null && connectivityManager.isActiveNetworkMetered) {
                return true
            }
        }
        return false
    }

     fun showSuccesDialog(context: Context) : Dialog {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
        dialog.setContentView(R.layout.dialog_success)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        /*dialog.show()
        dialog.findViewById<View>(R.id.bt_action).setOnClickListener {
            val intent = Intent(context, TranageHome::class.java)
            dialog.dismiss()
            context.startActivity(intent)
        }*/
        return dialog
    }

    fun showAddJobLocationDialog(context: Context) : Dialog {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
        dialog.setContentView(R.layout.dialog_job_location)
        dialog.setCancelable(true)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialog.window!!.attributes = lp
        return dialog
    }


    fun dialogDatePickerDob(tv: EditText, context: Context, fm : FragmentManager) {
        val cur_calender = Calendar.getInstance()
        val datePicker = DatePickerDialog.newInstance(
            { view, year, monthOfYear, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar[Calendar.YEAR] = year
                calendar[Calendar.MONTH] = monthOfYear
                calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                val date_ship_millis = calendar.timeInMillis
                val date = Date(date_ship_millis)
                val selDate = Tools.getFormattedDateSimple(date_ship_millis)
                tv.setText(selDate)
            },
            cur_calender[Calendar.YEAR],
            cur_calender[Calendar.MONTH],
            cur_calender[Calendar.DAY_OF_MONTH]
        )
        val min_date = Calendar.getInstance()
        min_date.set(1960,1,1)

        val max_date = Calendar.getInstance()
        val year = max_date.get(Calendar.YEAR) - 18
        val month = max_date.get(Calendar.MONTH)
        val day = max_date.get(Calendar.DAY_OF_MONTH)
        max_date.set(year, month, day )
//        min_date[1960]
        //set dark light
        datePicker.isThemeDark = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            datePicker.accentColor = context.resources.getColor(R.color.black, context.theme)
        }
        datePicker.maxDate = max_date
        datePicker.minDate = min_date
        datePicker.show(fm, "DatePicker")
    }

    fun dialogDatePickerLastDate(tv: EditText, context: Context, fm : FragmentManager) {
        val cur_calender = Calendar.getInstance()
        val datePicker = DatePickerDialog.newInstance(
            { view, year, monthOfYear, dayOfMonth ->
                val calendar = Calendar.getInstance()
                calendar[Calendar.YEAR] = year
                calendar[Calendar.MONTH] = monthOfYear
                calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
                val date_ship_millis = calendar.timeInMillis
                val date = Date(date_ship_millis)
                val selDate = Tools.getFormattedDateSimple(date_ship_millis)
                tv.setText(selDate)
            },
            cur_calender[Calendar.YEAR],
            cur_calender[Calendar.MONTH],
            cur_calender[Calendar.DAY_OF_MONTH]
        )

        val max_date = Calendar.getInstance()
        val year = max_date.get(Calendar.YEAR) + 20
        val month = max_date.get(Calendar.MONTH)
        val day = max_date.get(Calendar.DAY_OF_MONTH)
        max_date.set(year, month, day )
        //set dark light
        datePicker.isThemeDark = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            datePicker.accentColor = context.resources.getColor(R.color.black, context.theme)
        }
        datePicker.minDate = cur_calender
        datePicker.maxDate = max_date

        datePicker.show(fm, "DatePicker")
    }
}