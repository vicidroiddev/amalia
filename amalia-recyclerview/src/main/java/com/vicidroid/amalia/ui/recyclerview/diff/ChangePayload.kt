package com.vicidroid.amalia.ui.recyclerview.diff

data class ChangePayload<T>(val oldItem: T, val newItem: T)