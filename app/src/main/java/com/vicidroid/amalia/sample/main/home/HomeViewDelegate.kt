package com.vicidroid.amalia.sample.main.home

import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.vicidroid.amalia.core.viewdiff.ViewDiffProvider
import com.vicidroid.amalia.sample.R
import com.vicidroid.amalia.ui.BaseViewDelegate

class HomeViewDelegate(lifecycleOwner: LifecycleOwner, view: View) :
    BaseViewDelegate<HomeState, HomeEvent>(lifecycleOwner, view),
    ViewDiffProvider {

    val message: TextView = findViewById(R.id.message)
    val image: ImageView = findViewById(R.id.image)
    val showToastBtn: MaterialButton = findViewById(R.id.saveBtn)
    val openBtn: MaterialButton = findViewById(R.id.openBtn)
    val firstName: TextInputEditText = findViewById(R.id.firstNameEdit)
    val lastName: TextInputEditText = findViewById(R.id.lastNameEdit)
    val middleName: TextInputEditText = findViewById(R.id.middleNameEdit)
    val hasNickName: MaterialCheckBox = findViewById(R.id.hasNickNameCheckbox)
    val homePageRootList: LinearLayout = findViewById(R.id.homePageRootList)

    lateinit var nickName: TextInputEditText

    val viewDiff = HomeViewDiff()

    init {
        addDynamicFieldAfterInflation()
        showToastBtn.setOnClickListener { pushEvent(HomeEvent.RequestSave) }
        openBtn.setOnClickListener { pushEvent(HomeEvent.RequestNavigate(hostActivity())) }
        showToastBtn.visibility = View.VISIBLE
        openBtn.visibility = View.VISIBLE
    }

    private fun addDynamicFieldAfterInflation() {
        (LayoutInflater.from(context).inflate(R.layout.list_item_text_entry, null, false) as TextInputLayout).let { textEntry ->
            nickName = textEntry.findViewById(R.id.listItemTextEntry)
            nickName.id = R.id.nickname
            textEntry.hint = "Nickname"
            homePageRootList.addView(textEntry)
        }
    }

    override fun renderViewState(state: HomeState) {
        when (state) {
            is HomeState.Loaded -> {
                message.text = state.data
                Glide.with(context).load(state.imageUrl)
                    .fitCenter()
                    .centerCrop()
                    .into(image)

                firstName.setText(state.firstName)
                lastName.setText(state.lastName)
                middleName.setText(state.middleName)
                nickName.setText(state.nickName)
            }
        }
    }

    override fun provideViewDiff() = viewDiff.also { diff ->
        diff.firstName = firstName.text.toString()
        diff.lastName = lastName.text.toString()
        diff.middleName = middleName.text.toString()
    }
}

