package com.vicidroid.amalia.ext

import androidx.fragment.app.Fragment
import com.vicidroid.amalia.core.BasePresenter

/**
 * Workaround for reified types which allows the use of [presenterProvider] in Java.
 * Moreover, this will support constructor arguments in presenters.
 * Casting will be required to get the original type since
 */

class JavaPresenterProvider {
    companion object {
        @JvmStatic
        fun provide(fragment: Fragment, presenterCreator: () -> BasePresenter): BasePresenter {
            return fragment.presenterProvider { presenterCreator() }.value
        }
    }
}