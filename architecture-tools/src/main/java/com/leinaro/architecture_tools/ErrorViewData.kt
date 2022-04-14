package com.leinaro.architecture_tools

sealed class ErrorViewData(val error: Throwable)
data class DefaultError(val throwable: Throwable) : ErrorViewData(throwable)
