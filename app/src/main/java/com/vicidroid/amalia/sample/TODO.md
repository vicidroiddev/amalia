Handling events from the viewholder
------------------------------------

• Adapter should accept `eventLiveData` in it's constructor.
  It will be passed in by the view delegate which processes events of type E
• Add a private lateinit field to the BaseViewHolder
• in onCreateViewHolder, the `eventLiveData` should be applied to the viewholder.
• Add a pushEvent method to the BaseViewHolder

Handling orientation changes in recyclerview with modified data
-------------------------------------

• Recyclerview flow will not save change items back to data. This is only done when unbind() is called.
• Recyclerview should support saving of checkboxing, edittexts, radiobuttons etc...
• Perhaps we can go from Activity/Fragment:
    -> viewDelegate.onSaveInstanceState
    -> use adapter to get position for each of the items
    -> use findViewHolderForAdapterPosition which returns a viewholder
    -> viewholder may be null as it has been recycled.
    -> we should probably ensure our unbind method is called when the viewholder is recycled.
    -> call each item's unbind with the viewholder
    -> this will ensure the data is updated.


