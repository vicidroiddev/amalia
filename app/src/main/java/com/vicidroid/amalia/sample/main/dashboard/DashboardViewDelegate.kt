package com.vicidroid.amalia.sample.main.dashboard

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.vicidroid.amalia.sample.R
import com.vicidroid.amalia.ui.BaseViewDelegate

class DashboardViewDelegate(lifecycleOwner: LifecycleOwner, view: View) :
    BaseViewDelegate<DashboardState, DashboardEvent>(lifecycleOwner, view) {

    val message: TextView = findViewById(R.id.message)
    val image: ImageView = findViewById(R.id.image)
    val fragmentExample: MaterialCardView = findViewById(R.id.fragmentExampleCardView)

    init {
        fragmentExample.setOnClickListener {
            pushEvent(DashboardEvent.OpenFragmentExample(hostActivity()))
        }
    }

    override fun renderViewState(state: DashboardState) {
        when (state) {
            is DashboardState.Loaded -> {
                message.text = state.data
                Glide.with(context).load(state.imageUrl)
                    .fitCenter()
                    .centerCrop()
                    .into(image)
            }
        }
    }
}


