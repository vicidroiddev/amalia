package com.vicidroid.amalia.sample.main.test

import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.vicidroid.amalia.sample.R
import com.vicidroid.amalia.sample.test.Data
import com.vicidroid.amalia.ui.recyclerview.BaseRecyclerItem
import com.vicidroid.amalia.ui.recyclerview.BaseRecyclerViewHolder

data class TestRecyclerItem(val data: Data) : BaseRecyclerItem<TestRecyclerItem.TestViewHolder>(data) {
    override val layoutRes = R.layout.list_item_flow_test

    override fun createViewHolder(itemView: View) = TestViewHolder(itemView)


    override fun bind(viewHolder: TestViewHolder) {
        Log.v(TAG,"bind() on $data}")

        viewHolder.apply {
            title.text = data.title
            subTitle.text = data.subTitle
            editRoot.hint = data.hint
            editText.setText(data.editableText)
        }
    }

    override fun unbind(viewHolder: TestViewHolder) {
        viewHolder.apply {
            data.editableText = editText.text.toString()
        }
    }

    override val viewType: Int
        get() = super.viewType

    class TestViewHolder(itemView: View) : BaseRecyclerViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.list_item_main_test_title)
        val subTitle: TextView = itemView.findViewById(R.id.list_item_main_test_subtitle)
        val editRoot: TextInputLayout = itemView.findViewById(R.id.list_item_main_test_edit_root)
        val editText: TextInputEditText = itemView.findViewById(R.id.list_item_main_test_edit)
        val nextBtn: MaterialButton = itemView.findViewById(R.id.list_item_main_test_next_btn)

        init {
            Log.v(TAG,"TestRecyclerItem initialize()}")

            nextBtn.setOnClickListener {
                Toast.makeText(
                    itemView.context,
                    "TODO, bind events from adapter",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    companion object {
        val TAG = TestRecyclerItem::class.java.simpleName
    }
}