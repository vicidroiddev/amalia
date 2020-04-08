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



##1. Create your own `RecyclerViewDelegate`

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

##2. Create a recycler item to bind data to view
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

##3. Emit recycler items with a presenter
```kotlin
class MyLongListPresenter(private val repository: MyRepository) :
    BasePresenter<RecyclerViewState<MyRecyclerViewItem>, ViewEvent>(),
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

##4. Don't forget to bind the presenter and delegate!
See getting started section for how to perform binding.

