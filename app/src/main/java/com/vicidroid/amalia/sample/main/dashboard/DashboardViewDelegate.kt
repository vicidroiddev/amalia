package com.vicidroid.amalia.sample.main.dashboard

import android.view.View
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import com.vicidroid.amalia.sample.R
import com.vicidroid.amalia.ui.BaseViewDelegate

class DashboardViewDelegate(lifecycleOwner: LifecycleOwner, view: View) :
    BaseViewDelegate<DashboardState, DashboardEvent>(lifecycleOwner, view) {

    val message: TextView = findViewById(R.id.message)

    override fun renderViewState(state: DashboardState) {
        when (state) {
            is DashboardState.Loaded -> message.text = state.data
        }
    }
}


