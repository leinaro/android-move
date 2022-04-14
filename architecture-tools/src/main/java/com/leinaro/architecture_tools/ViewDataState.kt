package com.leinaro.architecture_tools

typealias ViewDataState<T> = Pair<T, ViewHandler<out T, out BaseViewModel<T>>>
typealias ViewEventState<T> = Pair<ViewEvent, ViewHandler<out ViewEvent, out BaseViewModel<T>>>

