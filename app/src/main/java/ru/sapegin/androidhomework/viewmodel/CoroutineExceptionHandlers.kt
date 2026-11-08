package ru.sapegin.androidhomework.viewmodel

import kotlinx.coroutines.CoroutineExceptionHandler

fun createCoroutineExceptionHandler(
    onError: (Throwable) -> Unit
): CoroutineExceptionHandler {
    return CoroutineExceptionHandler { _, throwable ->
        onError(throwable)
    }
}