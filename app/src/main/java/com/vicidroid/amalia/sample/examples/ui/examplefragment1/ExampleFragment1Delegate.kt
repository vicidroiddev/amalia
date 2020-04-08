package com.vicidroid.amalia.sample.examples.ui.examplefragment1

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.vicidroid.amalia.core.ViewState
import com.vicidroid.amalia.core.viewdiff.ViewDiffProvider
import com.vicidroid.amalia.sample.R
import com.vicidroid.amalia.ui.BaseViewDelegate

class ExampleFragment1Delegate(lifecycleOwner: LifecycleOwner, view: View) :
    BaseViewDelegate(lifecycleOwner, view), ViewDiffProvider {

    private val viewDiff = ExampleFragment1ViewDiff()

    private lateinit var editText: TextInputEditText
    private val linearLayout = findViewById<LinearLayout>(R.id.exampleFragment1Root)

    init {
        addDynamicView()
    }

    override fun renderViewState(state: ViewState) {
        Log.v(ExampleFragment1Delegate::class.java.simpleName, state.toString())
        when (state) {
            is ExampleFragment1ViewState.Loaded -> {
                Log.v(this::class.java.simpleName, "Setting: ${state.viewDiff.name}")
                editText.setText(state.viewDiff.name)
            }
        }
    }

    private fun addDynamicView() {
        val textEntryView =
            LayoutInflater.from(context).inflate(R.layout.list_item_text_entry, null, false) as TextInputLayout

        textEntryView.hint = "ViewDelegate - should restore"
        textEntryView.id = View.generateViewId()

        editText = textEntryView.findViewById(R.id.listItemTextEntry)
        editText.id = View.generateViewId()

        linearLayout.addView(textEntryView)
    }

    override fun provideViewDiff() = viewDiff.also { diff ->
        // Ensure we save what was edited by user.
        diff.name = editText.text.toString()
    }
}
