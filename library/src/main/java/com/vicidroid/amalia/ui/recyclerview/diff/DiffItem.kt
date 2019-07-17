package com.vicidroid.amalia.ui.recyclerview.diff

/**
 * Used in conjunction with [RecyclerItemDiffCallback.areItemsTheSame]
 * Leverage this interface for your data
 */
interface DiffItem {
    val diffId: Long
}
