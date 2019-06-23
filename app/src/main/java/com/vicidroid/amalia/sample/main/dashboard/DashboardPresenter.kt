package com.vicidroid.amalia.sample.main.dashboard

import com.vicidroid.amalia.core.BasePresenter
import com.vicidroid.amalia.sample.examples.ui.examplefragment1.ExampleFragmentActivity1
import com.vicidroid.amalia.sample.utils.startActivityClazz
import com.vicidroid.amalia.ui.BaseViewDelegate

class DashboardPresenter : BasePresenter<DashboardState, DashboardEvent>(),
    Refreshable {

    val imageUrl =
        "https://external-preview.redd.it/WTW1JY99hC5jGBSOsjFdmQYyWTKgoeywBL9JK6z29QA.jpg?auto=webp&s=74d4338ddb3523817e51dfcef0e0c67a666ee3a4"

    override fun onBindViewDelegate(viewDelegate: BaseViewDelegate<DashboardState, DashboardEvent>) {
        calculateTimestamp()
    }

    override fun onViewEvent(event: DashboardEvent) {
        when (event) {
            is DashboardEvent.OpenFragmentExample -> {
                event.hostActivity.startActivityClazz(ExampleFragmentActivity1::class.java)
            }
        }
    }

    override fun onRefreshRequest() {
        calculateTimestamp(true)
    }

    fun calculateTimestamp(force: Boolean = false) {
        pushState(
            DashboardState.Loaded("Dashboard " + System.currentTimeMillis().toString(), imageUrl),
            preferCachedState = !force
        )
    }
}