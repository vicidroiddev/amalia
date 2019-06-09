package com.vicidroid.amalia.sample.utils

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.vicidroid.amalia.sample.main.MainActivity

fun <T : AppCompatActivity> AppCompatActivity.startActivityClazz(clz: Class<T>) {
    startActivity(
        Intent(this, clz)
    )
}