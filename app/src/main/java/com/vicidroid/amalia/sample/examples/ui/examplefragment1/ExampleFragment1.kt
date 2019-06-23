package com.vicidroid.amalia.sample.examples.ui.examplefragment1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.vicidroid.amalia.sample.R
import kotlinx.android.synthetic.main.example_fragment1.*

class ExampleFragment1 : Fragment() {

    companion object {
        fun newInstance() = ExampleFragment1()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.example_fragment1, container, false)

        // Simulate adding a view programmatically, ensuring its state is restored by the system.
        // This only works if the view is added in onCreateView and a consistent id is set.
        val frameLayout: FrameLayout = view.findViewById(R.id.exampleFragment1DynamicRoot)
        val dynamicTextEntry = LayoutInflater.from(view.context).inflate(R.layout.list_item_text_entry, null, false)

        val textEntryRoot: TextInputLayout = dynamicTextEntry.findViewById(R.id.listItemTextEntryRoot)
        val textEntry: TextInputEditText = dynamicTextEntry.findViewById(R.id.listItemTextEntry)

        textEntryRoot.hint = "Text Entry dynamically added in onCreateView()"
        textEntry.id = R.id.textEntry1 // It needs the a consistent id to ensure restoration

        frameLayout.addView(dynamicTextEntry)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exampleFragmentAddEditTextBtn.setOnClickListener {
            // These entries will not be retained by the fragment restoration automatically.
            val newView = LayoutInflater.from(view.context).inflate(R.layout.list_item_text_entry, null, false)
            exampleFragment1Root.addView(newView)
        }
    }
}
