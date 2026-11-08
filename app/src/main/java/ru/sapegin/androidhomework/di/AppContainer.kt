package ru.sapegin.androidhomework.di

import android.content.Context
import ru.sapegin.androidhomework.data.repository.LocalProfileRepository
import ru.sapegin.androidhomework.domain.cache.FiltersBadgeCache
import ru.sapegin.androidhomework.domain.repository.ProfileRepository

object AppContainer {

    val filtersBadgeCache: FiltersBadgeCache by lazy {
        FiltersBadgeCache()
    }

    @Volatile
    private var profileRepositoryInstance: ProfileRepository? = null

    fun provideProfileRepository(context: Context): ProfileRepository {
        return profileRepositoryInstance ?: synchronized(this) {
            profileRepositoryInstance
                ?: LocalProfileRepository(context.applicationContext).also {
                    profileRepositoryInstance = it
                }
        }
    }
}