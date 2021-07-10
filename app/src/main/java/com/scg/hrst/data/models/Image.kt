package com.scg.hrst.data.models;

import android.graphics.drawable.Drawable;

data class Image (

    val image: Int,
    val imageDrw: Drawable,
    val name: String,
    val brief: String,
    val counter: Integer? = null
)
