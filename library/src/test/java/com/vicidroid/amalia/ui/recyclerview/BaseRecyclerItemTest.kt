package com.vicidroid.amalia.ui.recyclerview

import android.view.View
import com.nhaarman.mockitokotlin2.spy
import com.vicidroid.amalia.ui.recyclerview.diff.DiffItem
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class BaseRecyclerItemTest {

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun `RecyclerItem should leverage diff item to define itemId for recyclerview getItemId()`() {
        val myData = spy(MyData("product_1"))
        val myRecyclerItem = spy(MyRecyclerItem(myData))

        assertEquals(myRecyclerItem.diffItem, myData)
        assertEquals(myRecyclerItem.uniqueItemId, myData.id.hashCode().toLong())
    }

    class MyViewHolder(view: View) : BaseRecyclerViewHolder(view)

    class MyRecyclerItem(myData: MyData) : BaseRecyclerItem<MyViewHolder>(myData) {
        override fun bind(viewHolder: MyViewHolder) {
        }

        override val layoutRes = 1000

        override fun createViewHolder(itemView: View) = MyViewHolder(itemView)
    }

    class MyData(val id: String) : DiffItem {
        override val diffId = id
    }
}