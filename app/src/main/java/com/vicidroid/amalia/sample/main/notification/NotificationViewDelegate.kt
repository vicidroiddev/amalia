package com.vicidroid.amalia.sample.main.notification

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.vicidroid.amalia.sample.R
import com.vicidroid.amalia.ui.BaseViewDelegate

class NavigationViewDelegate(lifecycleOwner: LifecycleOwner, view: View) :
    BaseViewDelegate<NavigationState, NavigationEvent>(lifecycleOwner, view) {

    val message: TextView = findViewById(R.id.message)
    val image: ImageView = findViewById(R.id.image)

    override fun renderViewState(state: NavigationState) {
        when (state) {
            is NavigationState.Loaded -> {
                message.text = state.data
                Glide.with(context).load(state.imageUrl)
                    .fitCenter()
                    .centerCrop()
                    .into(image)
            }
        }
    }
}