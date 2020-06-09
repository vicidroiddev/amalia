package com.vicidroid.amalia.ui.recyclerview.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseHeaderViewHolder(itemView: View) :
    RecyclerView.ViewHolder(itemView),
    ViewHolderHelper {

    override val parentView = itemView
}
