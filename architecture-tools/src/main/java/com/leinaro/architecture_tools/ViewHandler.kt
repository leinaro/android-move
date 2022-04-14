package com.leinaro.architecture_tools

interface ViewHandler<T, C> {
    fun T.perform(context: Any, viewModel: C)
}
