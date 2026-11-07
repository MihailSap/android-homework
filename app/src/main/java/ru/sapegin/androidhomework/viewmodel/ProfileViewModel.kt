package ru.sapegin.androidhomework.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.sapegin.androidhomework.di.AppContainer
import ru.sapegin.androidhomework.domain.repository.ProfileRepository
import java.net.URI

class ProfileViewModel(
    private val repository: ProfileRepository
) : ViewModel() {

    val profile = repository.profile

    private val _draft = MutableStateFlow(repository.profile.value)
    val draft = _draft.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _isSavingAvatar = MutableStateFlow(false)
    val isSavingAvatar = _isSavingAvatar.asStateFlow()

    private val _isDownloading = MutableStateFlow(false)
    val isDownloading = _isDownloading.asStateFlow()

    private val _downloadedResumePath = MutableStateFlow<String?>(null)
    val downloadedResumePath = _downloadedResumePath.asStateFlow()

    fun updateFullName(value: String) {
        _draft.value = _draft.value.copy(fullName = value)
        _error.value = null
    }

    fun updatePosition(value: String) {
        _draft.value = _draft.value.copy(position = value)
        _error.value = null
    }

    fun updateResumeUrl(value: String) {
        _draft.value = _draft.value.copy(resumeUrl = value)
        _error.value = null
    }

    fun updateAvatarFromUri(uri: String) {
        if (_isSavingAvatar.value) return

        viewModelScope.launch {
            _isSavingAvatar.value = true

            try {
                val savedUri = repository.saveAvatar(uri)

                _draft.value = _draft.value.copy(
                    avatarUri = savedUri
                )

                _error.value = null
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.value = e.message
                    ?: "Не удалось сохранить фотографию"
            } finally {
                _isSavingAvatar.value = false
            }
        }
    }

    fun saveProfile(): Boolean {
        val updatedProfile = _draft.value.copy(
            fullName = _draft.value.fullName.trim(),
            position = _draft.value.position.trim(),
            resumeUrl = _draft.value.resumeUrl.trim()
        )

        if (
            updatedProfile.resumeUrl.isNotBlank() &&
            !isValidHttpsUrl(updatedProfile.resumeUrl)
        ) {
            _error.value =
                "Укажите корректную ссылку на файл, начинающуюся с https://"
            return false
        }

        repository.saveProfile(updatedProfile)

        _draft.value = updatedProfile
        _error.value = null

        return true
    }

    fun downloadResume() {
        if (_isDownloading.value) return

        val url = profile.value.resumeUrl.trim()

        if (url.isBlank() || !isValidHttpsUrl(url)) {
            _error.value = "Ссылка на резюме не указана или некорректна"
            return
        }

        _downloadedResumePath.value = null
        _error.value = null

        viewModelScope.launch {
            _isDownloading.value = true

            try {
                _downloadedResumePath.value =
                    repository.downloadResume(url)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _error.value = e.message
                    ?: "Не удалось скачать резюме"
            } finally {
                _isDownloading.value = false
            }
        }
    }

    fun clearDownloadedResume() {
        _downloadedResumePath.value = null
    }

    private fun isValidHttpsUrl(value: String): Boolean {
        return try {
            val uri = URI(value)

            uri.scheme.equals("https", ignoreCase = true) &&
                    !uri.host.isNullOrBlank()
        } catch (e: Exception) {
            false
        }
    }
}

class ProfileViewModelFactory(
    context: Context
) : ViewModelProvider.Factory {

    private val repository = AppContainer.provideProfileRepository(context)

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}