package ru.sapegin.androidhomework.domain.model

data class UserProfile(
    val fullName: String = "",
    val avatarUri: String? = null,
    val resumeUrl: String = "",
    val position: String = ""
)