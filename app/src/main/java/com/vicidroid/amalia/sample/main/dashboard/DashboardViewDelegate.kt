package com.vicidroid.amalia.sample.main.dashboard

import android.view.View
import android.view.ViewStub
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.vicidroid.amalia.core.EphemeralState
import com.vicidroid.amalia.core.ViewState
import com.vicidroid.amalia.sample.R
import com.vicidroid.amalia.ui.BaseViewDelegate

class DashboardViewDelegate(lifecycleOwner: LifecycleOwner, view: View) :
    BaseViewDelegate(lifecycleOwner, view) {

    private var dialog: AlertDialog? = null
    private val message: TextView = findViewById(R.id.message)
    private val image: ImageView = findViewById(R.id.image)
    private val launchDialogButton: MaterialButton = findViewById(R.id.launchDialog)
    private val fragmentExample: MaterialCardView = findViewById(R.id.fragmentExampleCardView)

    init {
        launchDialogButton.setOnClickListener {
            pushEvent(DashboardEvent.RequestDialogViaEphemeralState())
        }

        fragmentExample.setOnClickListener {
            pushEvent(DashboardEvent.OpenFragmentExample(hostActivity()))
        }
    }

    override fun renderViewState(state: ViewState) {
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

    override fun onViewDetached() {
        dialog?.cancel()
    }

    override fun renderEphemeralState(state: EphemeralState) {
        when (state) {
            is DashboardState.EphemeralStateToLoadDialog -> {
                dialog = MaterialAlertDialogBuilder(context)
                    .setTitle("Ephemeral Dialog")
                    .setMessage("We expect the state that caused this dialog to show to be flushed and not persisted")
                    .setPositiveButton("Ok", null)
                    .show()

            }
        }
    }
}


