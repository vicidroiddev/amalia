package com.vicidroid.amalia.sample.examples.ui.examplefragment1

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.vicidroid.amalia.ext.presenterProvider
import com.vicidroid.amalia.sample.R
import com.vicidroid.amalia.sample.databinding.ExampleFragment1Binding

class ExampleFragment1 : Fragment() {

    private val presenter by presenterProvider { ExampleFragment1Presenter() }
    private lateinit var delegate: ExampleFragment1Delegate

    companion object {
        fun newInstance() = ExampleFragment1()
    }

    private lateinit var mothersName: TextInputEditText

    private var binding: ExampleFragment1Binding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.example_fragment1, container, false)
        binding = ExampleFragment1Binding.bind(view)

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
        addDynamicViewThatWontRestore()
        addDynamicViewWithIdThatWillRestore(savedInstanceState)

        delegate = ExampleFragment1Delegate(viewLifecycleOwner, view)
        presenter.bind(delegate)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(mothersName.id.toString(), mothersName.onSaveInstanceState())
    }

    /**
     * The fragment will inflate a fresh view using the layout inflater.
     * Of course it will not restore those views we add after onCreateView.
     * This view will go through the onSaveInstance state procedure.
     * However if we don't inject that saveInstanceState nothing will be restored.
     */
    private fun addDynamicViewThatWontRestore() {
        val newView =
            LayoutInflater.from(context).inflate(R.layout.list_item_text_entry, null, false) as TextInputLayout
        newView.hint = "Nick mame - should not restore"
        newView.id = View.generateViewId() // This doesn't matter it can also be View.NO_ID
        newView.findViewById<View>(R.id.listItemTextEntry).id = View.generateViewId()
        binding!!.exampleFragment1Root.addView(newView)
    }

    /**
     * The fragment will inflate a fresh view using the layout inflater.
     * Of course it will not restore those views we add after onCreateView.
     * Now when we add the view we should ensure it is restored.
     * One could try to access the view state that is saved under "android:view state", however things get slightly tricky there.
     */
    private fun addDynamicViewWithIdThatWillRestore(savedInstanceState: Bundle?) {
        val newView =
            LayoutInflater.from(context).inflate(R.layout.list_item_text_entry, null, false) as TextInputLayout
        newView.hint = "Mothers name - should restore"
        newView.id = R.id.motherRoot
        mothersName = newView.findViewById(R.id.listItemTextEntry)
        mothersName.id = R.id.mother
        mothersName.onRestoreInstanceState(savedInstanceState?.getParcelable(R.id.mother.toString()))

        binding!!.exampleFragment1Root.addView(newView)
    }
}
