package com.scg.hrst.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleObserver
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.snackbar.Snackbar
import com.scg.hrst.R
import kotlin.math.roundToInt

fun Context.getAppColor(color: Int): Int {
    return ContextCompat.getColor(this, color)
}

fun View.applyBackgroundTintTheme2(color: Int) {
    (background as GradientDrawable).setColor(color)
    (background as GradientDrawable).setStroke(0, 0)
    background.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY)
}

fun AppCompatActivity.makeTransaprant() {
    window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
}
inline fun <T : View> T.onClick(crossinline func: T.() -> Unit) = setOnClickListener { func() }

fun View.setStrokedBackground(
    backgroundColor: Int,
    strokeColor: Int = 0,
    alpha: Float = 1.0f,
    strokeWidth: Int = 3
) {
    val drawable = background as GradientDrawable
    drawable.setStroke(strokeWidth, strokeColor)
    drawable.setColor(adjustAlpha(backgroundColor, alpha))
}

fun adjustAlpha(color: Int, factor: Float): Int {
    val alpha = (Color.alpha(color) * factor).roundToInt()
    val red = Color.red(color)
    val green = Color.green(color)
    val blue = Color.blue(color)
    return Color.argb(alpha, red, green, blue)
}


infix fun ViewGroup.inflate(layoutRes: Int): View =
    LayoutInflater.from(context).inflate(layoutRes, this, false)

fun Context.dialNumber(number: String) =
    startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number")))


fun Toolbar.changeToolbarFont() {
    for (i in 0 until childCount) {
        val view = getChildAt(i)
        if (view is TextView && view.text == title) {
            view.typeface = view.context.theme3fontBold()
            break
        }
    }
}

private class PermissionObserver : LifecycleObserver {
    var onResumeCallback: (() -> Unit)? = null

}

fun Snackbar.setTextColor(color: Int): Snackbar {
    val tv = view.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
    tv.setTextColor(color)
    return this
}

fun FragmentActivity.requestPermissions(
    permissions: Array<String>,
    onResult: (isGranted: Boolean) -> Unit
) {
    if (isPermissionGranted(permissions)) {
        onResult(true)
        return
    }
    val observer = PermissionObserver()
    observer.onResumeCallback = {
        lifecycle.removeObserver(observer)
        onResult(isPermissionGranted(permissions))
    }
    lifecycle.addObserver(observer)
    ActivityCompat.requestPermissions(this, permissions, 100)
}


fun Activity.showPermissionAlert(view: View) {
    val snackBar = Snackbar.make(
        view,
        getString(R.string.edu_error_permission_required),
        Snackbar.LENGTH_INDEFINITE
    )
    val sbView = snackBar.view
    snackBar.setAction("Enable") {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
        snackBar.dismiss()
    }
    sbView.setBackgroundColor(
        ContextCompat.getColor(
            this,
            R.color.Edu_color_setting
        )
    );snackBar.setTextColor(Color.WHITE);snackBar.show()
}

fun Context.isPermissionGranted(permissions: Array<String>): Boolean {
    permissions.forEach {
        if (ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED)
            return false
    }
    return true
}

fun Context.theme3fontBold(): Typeface? {
    return Typeface.createFromAsset(assets, getString(R.string.edu_font_bold))
}

inline fun <reified T : Any> newIntent(context: Context): Intent = Intent(context, T::class.java)

inline fun <reified T : Any> Activity.launchActivity(
    requestCode: Int = -1,
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}
) {
    val intent = newIntent<T>(this)
    intent.init()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        startActivityForResult(intent, requestCode, options)
    } else {
        startActivityForResult(intent, requestCode)
    }
}

fun ImageView.loadImageFromResources(context: Context,aImageUrl: Int) {
    Glide.with(context)
        .load(aImageUrl)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .into(this)
}
