package com.vicidroid.amalia.sample.main.home

import android.view.View
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import com.vicidroid.amalia.sample.R
import com.vicidroid.amalia.ui.BaseViewDelegate

class HomeViewDelegate(lifecycleOwner: LifecycleOwner, view: View) :
    BaseViewDelegate<HomeState, HomeEvent>(lifecycleOwner, view) {

    val message: TextView = findViewById(R.id.message)

    override fun renderViewState(state: HomeState) {
        when (state) {
            is HomeState.Loaded -> message.text = state.data
        }
    }
}