package ru.sapegin.androidhomework.domain.repository

import kotlinx.coroutines.flow.StateFlow
import ru.sapegin.androidhomework.domain.model.UserProfile

interface ProfileRepository {

    val profile: StateFlow<UserProfile>

    fun saveProfile(profile: UserProfile)

    suspend fun saveAvatar(sourceUri: String): String

    suspend fun downloadResume(url: String): String
}