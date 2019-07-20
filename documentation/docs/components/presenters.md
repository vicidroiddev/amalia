# Presenters

- Should perform all data loading/saving/refreshing (anything background heavy)
- Will survive configuration changes (member fields stay in tact)
- Should leverage  `pushState(...)`  to expose a meaningful  `ViewState`.
- Should inherit from  `BasePresenter<ViewState, ViewEvent>`
- Should override  `onViewEvent(...)`  to process incoming ViewEvents
- Should never contain views, or activity contexts that may leak
- Can leverage the application context field to get resources
- Can use savedStateHandle for process death restoration