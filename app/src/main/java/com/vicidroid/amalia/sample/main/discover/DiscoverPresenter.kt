package com.vicidroid.amalia.sample.main.discover

import android.widget.Toast
import androidx.lifecycle.mainScope
import com.vicidroid.amalia.core.BasePresenter
import com.vicidroid.amalia.core.ViewEvent
import com.vicidroid.amalia.ext.debugLog
import com.vicidroid.amalia.sample.api.themoviedb.discover.DiscoverRepository
import com.vicidroid.amalia.sample.api.themoviedb.discover.DiscoverResult
import com.vicidroid.amalia.sample.main.dashboard.Refreshable
import com.vicidroid.amalia.ui.recyclerview.AmaliaCommonEvent
import com.vicidroid.amalia.ui.recyclerview.RecyclerViewState
import kotlinx.coroutines.launch

class DiscoverPresenter(private val repository: DiscoverRepository) :
    BasePresenter(),
    Refreshable {

    private var results: MutableList<DiscoverResult> = mutableListOf()

    override fun loadInitialState() {
        debugLog(TAG_INSTANCE, "loadInitialState()")
        mainScope.launch {
            results.clear()
            results.addAll(repository.discoverFromApi())
            results.forEach { debugLog(TAG_INSTANCE, it.toString()) }

            pushState(
                RecyclerViewState.ListLoaded(
                    results.mapIndexed { index, discoverResult ->
                            listOf(
                                DiscoverTvItemHeader(discoverResult.firstAirDate),
                                DiscoverTvItem(discoverResult)
                            )
                    }.flatten()
                )
            )
        }
    }

    override fun onRefreshRequest() {
    }

    override fun onViewEvent(event: ViewEvent) {
        when (event) {
            is DiscoverEvent.RemoveTvShow -> {
                results.remove(event.discoverResult)
                Toast.makeText(applicationContext, "Removed: ${event.discoverResult.name}", Toast.LENGTH_LONG).show()

                pushState(
                    RecyclerViewState.ListLoaded(
                        results.map { DiscoverTvItem(it) })
                )
            }

            is AmaliaCommonEvent.NewVisibleItemsDetected -> {
                event.visibleItems
                    .mapNotNull { item -> item as? DiscoverResult }
                    .forEach { discoverResult ->
                        debugLog(TAG_INSTANCE, "Track: Seen discover result: ${discoverResult.name} ")
                }

            }
        }

    }
}
