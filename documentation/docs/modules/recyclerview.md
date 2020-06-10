# RecyclerView Backed Delegates

This module gives plug and play support for simple and complex lists.

You can build complex dashboards where each item in the RecyclerView is backed by a different presenter. If each card needs to own its own logic for when to display - NO problem.

You can also create simple lists that are composed of different view items, such as cards, headers, etc...

Out of the box you will get:

 * Async diff support (send a bunch of items via pushState, only the items that changed will be processed)
 * A wrapper around RecyclerView that allows you to focus on your own display logic
 * Optional tracking of items seen via event propagation to your presenter

##Adding gradle dependency
Add the amalia dependency in your app level build.gradle file.

```groovy
implementation 'com.github.vicidroiddev.amalia:amalia-recyclerview:{latest_version}@aar'
```
[![Jitpack](https://jitpack.io/v/vicidroiddev/amalia.svg)](https://jitpack.io/#vicidroiddev/amalia)


## Basic usage

####1. Create your own RecyclerViewDelegate

This is the view layer. You likely won't have much code to put in here.
You can customize some options such as spacing or layout managers for the recyclerview if needed.
See the constructor for additional options.


```kotlin
class MyLongListViewDelegate(lifecycleOwner: LifecycleOwner, view: View) :
    RecyclerViewDelegate<DiscoverTvItem, DiscoverTvItem.ViewHolder>(
        lifecycleOwner,
        view,
        R.id.the_recyclerview_id,
        trackItemsSeen = true /* optional */) {
}
```

####2. Create a recycler item to bind data to view
```kotlin
class MyRecyclerViewItem(val myItem: MyItem) : BaseRecyclerItem<DiscoverTvItem.ViewHolder>(discoverResult) {
    // Pass a layout represented by the given item to display - CardView for example
    override val layoutRes = R.layout.my_list_item_layout

    override fun createViewHolder(itemView: View) = MyViewHolder(itemView)

    override fun bind(viewHolder: ViewHolder, payloads: List<ChangePayload<DiffItem>>) {
        with(viewHolder) {
            // bind myItem to the view here
            name.text = myItem.name

        }
    }

    class MyViewHolder(itemView: View) : BaseRecyclerViewHolder(itemView) {
        val name: TextView = findViewById(R.id.my_name)

        init {
            // Set onclick listeners here or any other view listeners

            // Push events here to be processed by your presenter
        }
    }
}
```

####3. Emit recycler items with a presenter
```kotlin
class MyLongListPresenter(private val repository: MyRepository) :
    BasePresenter(),
    Refreshable {

    private var results: MutableList<DiscoverResult> = mutableListOf()

    override fun loadInitialState() {
        mainScope.launch {
            // Make a suspending call that could be expensive, get a long list of items from a db for example.
            val items = repository.fetchItems()

            // Convert that data item (regular POJO class) to a RecyclerItem
            val viewItems = items.map { myItem -> MyRecyclerViewItem(myItem) }

            pushState(RecyclerViewState.ListLoaded(viewItems))
        }
    }
}
```

####4. Don't forget to bind the presenter and delegate!
See getting started section for how to perform binding.

## RecyclerView options

Create a subclass of `RecyclerViewDelegate` and pass your desired params.
Most arguments are optional. The bare minimum is `viewLifecycleOwner`, `rootView`, and `recyclerViewId`.
These arguments are subject to change.

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

        // Optional: Pass the divider decoration to use. A space offset decorator is provided by default
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