package com.vicidroid.amalia.sample.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vicidroid.amalia.ext.presenterProvider
import com.vicidroid.amalia.ext.viewDelegateProvider
import com.vicidroid.amalia.sample.BaseActivity
import com.vicidroid.amalia.sample.R

class MainActivity : BaseActivity() {

    private val mainPresenter by presenterProvider {
        MainPresenter()
    }

    private val viewDelegate by viewDelegateProvider {
        MainViewDelegate(this, window.decorView.rootView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainPresenter.bind(viewDelegate)
    }
}
