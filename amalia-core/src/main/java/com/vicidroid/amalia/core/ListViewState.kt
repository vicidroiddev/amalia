package com.vicidroid.amalia.core

import android.os.Parcel
import android.os.Parcelable
import com.vicidroid.amalia.ui.ViewItem
import kotlinx.android.parcel.Parceler
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue


/**
 * Various general states useful for handling changes to a list.
 * Extend from [ListViewState] to specify non-general states for a given feature.
 * [D] data entity that backs the view that backs the view
 * [VI] a view items that wraps all views and should ideally contain [D]
 */
sealed class ListViewState : ViewState {
  class Loading : ListViewState()
  class Empty : ListViewState()
  class DataLoaded(val data: MutableList<Any>) : ListViewState()
  class ItemAdded(val data: Any) : ListViewState()
  class ItemRemoved(val viewItem: ViewItem) : ListViewState()
  class ConfirmationRequired : ListViewState()
  class NetworkRequired: ListViewState()
}
