package com.gayathri.videogallery

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
fun Long.getDateTime(): String? {
    try {
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val netDate = Date(this * 1000)
        return sdf.format(netDate)
    } catch (e: Exception) {
        return e.toString()
    }
}