package com.vicidroid.amalia.sample.launch

import android.os.Bundle
import android.view.View
import com.vicidroid.amalia.sample.BaseActivity
import com.vicidroid.amalia.sample.R
import com.vicidroid.amalia.sample.main.MainActivity
import com.vicidroid.amalia.sample.utils.startActivityClazz
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LaunchActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

//        fullscreenContent.systemUiVisibility =
//            View.SYSTEM_UI_FLAG_LOW_PROFILE or
//                    View.SYSTEM_UI_FLAG_FULLSCREEN or
//                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
//                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
//                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
//                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION


    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        launch {
            delay(1000)

            startActivityClazz(MainActivity::class.java)
            finishAfterTransition()
        }
    }
}
