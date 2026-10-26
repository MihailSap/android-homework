package ru.sapegin.androidhomework.di

import ru.sapegin.androidhomework.domain.cache.FiltersBadgeCache

object AppContainer {

    val filtersBadgeCache: FiltersBadgeCache by lazy {
        FiltersBadgeCache()
    }
}