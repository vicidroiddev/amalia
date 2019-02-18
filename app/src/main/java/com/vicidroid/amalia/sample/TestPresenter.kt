package com.vicidroid.amalia.sample

import android.os.Bundle
import android.view.View
import androidx.lifecycle.LifecycleOwner
import com.vicidroid.amalia.core.BasePresenter
import com.vicidroid.amalia.core.ViewEvent
import com.vicidroid.amalia.core.ViewState
import com.vicidroid.amalia.ui.BaseViewDelegate


class TestPresenter() : BasePresenter<TestViewState, TestViewEvent>() {

}

class TestViewDelegate(viewLifeCycleOwner: LifecycleOwner, rootView: View) :
    BaseViewDelegate<TestViewState, TestViewEvent>(
        viewLifeCycleOwner,
        rootView
    ) {
    override fun renderViewState(state: TestViewState) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSaveInstanceState(outState: Bundle) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}


sealed class TestViewState : ViewState {

}

sealed class TestViewEvent : ViewEvent {

}