# ViewDelegates

-   Should perform all view related tasks and be fairly dumb (no business logic)
-   Should inherit from  `BaseViewDelegate<ViewState, ViewEvent>`
-   Should override  `renderViewState(...)`  to process incoming ViewStates
-   Can contain contexts, views or anything else that you would normally put in an Activity/Fragment
-   Should contain your  `findViewById`  calls
-   Should contain your various view listeners (`onClick`,  `onCheckChanged`  etc..)
-   Should leverage  `pushEvent(...)`  to expose a meaningful  `ViewEvent`