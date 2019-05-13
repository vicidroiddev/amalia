package com.vicidroid.amalia.sample.main.home

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.vicidroid.amalia.sample.R
import com.vicidroid.amalia.ui.BaseViewDelegate

class HomeViewDelegate(lifecycleOwner: LifecycleOwner, view: View) :
    BaseViewDelegate<HomeState, HomeEvent>(lifecycleOwner, view) {

    val message: TextView = findViewById(R.id.message)
    val image: ImageView = findViewById(R.id.image)
    val showToastBtn: MaterialButton = findViewById(R.id.saveBtn)

    init {
        showToastBtn.setOnClickListener { pushEvent(HomeEvent.RequestSave) }
        showToastBtn.visibility = View.VISIBLE
    }

    override fun renderViewState(state: HomeState) {
        when (state) {
            is HomeState.Loaded -> {
                message.text = state.data
                Glide.with(context).load(state.imageUrl)
                    .fitCenter()
                    .centerCrop()
                    .into(image)
            }
        }
    }
}

