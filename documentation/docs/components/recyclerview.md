# RecyclerView Delegate

!!! example "Features"
    - Async DiffUtil support
    - Sticky headers
    - Multiple view types
    - Space decorators
    - Notifications about items seen


## Basic setup with all the options

Create a subclass of `RecyclerViewDelegate` and pass your desired params.
Most arguments are optional. The bare minimum is `viewLifecycleOwner`, `rootView`, and `recyclerViewId`.

```kotlin
class MyListViewDelegate(lifecycleOwner: LifecycleOwner, view: View) : RecyclerViewDelegate(
        // Provide a lifecycle owner to ensure events can be sent to your presenter
        viewLifeCycleOwner = lifecycleOwner,

        // Provide the parent view
        rootView = view,

        // Provide the id of the recyclerview in your layout
        recyclerViewId = R.id.main_discover_page_list_root,

        // Optional: Pass a layout manager, by default a vertical linear layout manager is used
        layoutManager = LinearLayoutManager(rootView.context),

        // Optional: Pass the spacing to be used between items in dp
        spaceSeparationInDp = 8,

        // Optional: Pass the divider decoration to use. A space offset one is provided by default
        defaultDividerDecoration =  SpaceItemOffsetDecoration(rootView.context, spaceSeparationInDp),

        // Optional: Provide a DiffItem callback, by default one is provided for items that implement `DiffItem`
        asyncDiffCallback = AsyncRecyclerItemDiffCallback(),

        // Optional: Provide a different setting in case your recyclerview is not a fixed size
        recyclerViewHasFixedSize = true,

        // Optional: Pass true to get ViewEvents notifying you of Items that were seen.
        trackItemsSeen = false,

        // Optional: Pass the percent threshold 1-100 for when an item is considered visible.
        visibilityThresholdPercentage = 80,

        // Optional: Pass true if you wish to use sticky headers.
        useStickyHeaders = false
    )
```

## Sticky header support

A basic example for supporting headers involves passing `useStickyHeaders = true` to your view delegate.

```kotlin
MyListDelegate(lifecycleOwner: LifecycleOwner, view: View) : RecyclerViewDelegate(
            ..., // required params
            useStickyHeaders = true // required for sticky support
```

In addition, we need to define the header layout and what section each item belongs to.
Similiar to how your RecyclerItem provides the necessary view for each item, it can also provide details
for the header section it belongs to.

You will need to provide the header layout and an identifier which tells the system
when a new header should be drawn for the next view item. View items using the same header id will have the same sticky header while scrolling.


```kotlin
class DiscoverTvItem(val discoverResult: DiscoverResult)
    : BaseRecyclerItem<DiscoverTvItem.ViewHolder>(discoverResult) {

    // Provide the layout that represents the sticky header
    override val headerLayoutRes = R.layout.list_item_discover_tv_header_not_rounded

    // Provide a positive long to denote the header id this item belongs to.
    // For example if you have a contact list, return a representation of each letter as your id.
    override val headerId: Long = abs(discoverResult.firstAirDate.hashCode().toLong())

    // Return an instance of your own viewholder class which can hold on to your header view
    override fun createHeaderViewHolder(itemView: View) = MyHeaderViewHolder(itemView)

    // Ensure that you keep your header up to date
    override fun bindHeader(viewHolder: BaseHeaderViewHolder) {
        (viewHolder as MyHeaderViewHolder).title.text = discoverResult.firstAirDate
    }

    // Cache your child views. In our case we want to set the title for our header.
    class MyHeaderViewHolder(itemView: View) : BaseHeaderViewHolder(itemView) {
        val title = findViewById<TextView>(R.id.list_item_discover_tv_header_title)
    }
}
```