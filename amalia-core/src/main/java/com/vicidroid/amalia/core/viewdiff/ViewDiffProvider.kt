package com.vicidroid.amalia.core.viewdiff

interface ViewDiffProvider {
    /**
     * Allows view delegate to summarize the differences in the view due to user input.
     * The ViewDiff will be made available to the presenter for persistence purposes.
     */
    fun provideViewDiff() : BaseViewDiff
}