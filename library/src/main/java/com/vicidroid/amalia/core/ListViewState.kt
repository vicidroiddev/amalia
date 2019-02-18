package com.vicidroid.amalia.core

import com.vicidroid.amalia.ui.ViewItem


/**
 * Various general states useful for handling changes to a list.
 * Extend from [ListViewState] to specify non-general states for a given feature.
 * [D] data entity that backs the view that backs the view
 * [VI] a view items that wraps all views and should ideally contain [D]
 */
sealed class ListViewState<D, VI : ViewItem> : ViewState {
  class Loading<D, VI : ViewItem> : ListViewState<D, VI>()
  class Empty<D, VI : ViewItem> : ListViewState<D, VI>()
  class DataLoaded<D, VI : ViewItem>(val data: MutableList<D>) : ListViewState<D, VI>()
  class ItemAdded<D, VI : ViewItem>(val data: D) : ListViewState<D, VI>()
  class ItemRemoved<D, VI : ViewItem>(val viewItem: VI) : ListViewState<D, VI>()
  class ConfirmationRequired<D, VI : ViewItem> : ListViewState<D, VI>()
  class NetworkRequired<D, VI : ViewItem> : ListViewState<D, VI>()
}
