package ru.sapegin.androidhomework.data.repository

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import ru.sapegin.androidhomework.domain.model.UserProfile
import ru.sapegin.androidhomework.domain.repository.ProfileRepository
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class LocalProfileRepository(
    context: Context
) : ProfileRepository {

    private val appContext = context.applicationContext

    private val preferences = appContext.getSharedPreferences("user_profile", Context.MODE_PRIVATE)

    private val _profile = MutableStateFlow(loadProfile())

    override val profile: StateFlow<UserProfile> = _profile.asStateFlow()

    private fun loadProfile(): UserProfile {
        return UserProfile(
            fullName = preferences.getString("fullName", "").orEmpty(),
            avatarUri = preferences.getString("avatarUri", null),
            resumeUrl = preferences.getString("resumeUrl", "").orEmpty(),
            position = preferences.getString("position", "").orEmpty()
        )
    }

    override fun saveProfile(profile: UserProfile) {
        preferences.edit()
            .putString("fullName", profile.fullName)
            .putString("avatarUri", profile.avatarUri)
            .putString("resumeUrl", profile.resumeUrl)
            .putString("position", profile.position)
            .apply()
        _profile.value = profile
    }

    override suspend fun saveAvatar(sourceUri: String): String {
        return withContext(Dispatchers.IO) {
            val uri = Uri.parse(sourceUri)
            val mimeType = appContext.contentResolver.getType(uri)
            val extension = when (mimeType) {
                "image/png" -> "png"
                "image/webp" -> "webp"
                else -> "jpg"
            }
            val directory = File(
                appContext.filesDir,
                "profile/avatars"
            )
            if (!directory.exists() && !directory.mkdirs()) {
                throw IOException("Не удалось создать папку для фотографии")
            }
            val destination = File(
                directory,
                "avatar_${System.currentTimeMillis()}.$extension"
            )
            val inputStream = appContext.contentResolver
                .openInputStream(uri)
                ?: throw IOException("Не удалось открыть выбранную фотографию")
            inputStream.use { input ->
                destination.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            if (uri.authority == "${appContext.packageName}.fileprovider"
                && uri.pathSegments.firstOrNull() == "camera_images") {
                uri.lastPathSegment?.let { fileName ->
                    File(File(appContext.cacheDir, "camera"), fileName).delete()
                }
            }

            Uri.fromFile(destination).toString()
        }
    }

    override suspend fun downloadResume(url: String): String {
        return withContext(Dispatchers.IO) {
            val connection = URL(url).openConnection() as HttpURLConnection
            try {
                connection.connectTimeout = 15_000
                connection.readTimeout = 30_000
                connection.instanceFollowRedirects = true
                connection.requestMethod = "GET"

                val responseCode = connection.responseCode
                if (responseCode !in 200..299) {
                    throw IOException(
                        "Ошибка скачивания. Код сервера: $responseCode"
                    )
                }

                val contentType = connection.contentType?.substringBefore(";")
                if (
                    contentType != null &&
                    contentType !in listOf(
                        "application/pdf",
                        "application/octet-stream",
                        "binary/octet-stream"
                    )
                ) {
                    throw IOException("По ссылке не найден PDF-файл")
                }

                val directory = File(
                    appContext.cacheDir,
                    "resumes"
                )

                if (!directory.exists() && !directory.mkdirs()) {
                    throw IOException("Не удалось создать папку для резюме")
                }

                val destination = File(
                    directory,
                    "resume_${System.currentTimeMillis()}.pdf"
                )

                try {
                    connection.inputStream.use { input ->
                        destination.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }

                    if (destination.length() == 0L) {
                        throw IOException("Скачанный файл пуст")
                    }

                    destination.absolutePath
                } catch (e: Exception) {
                    destination.delete()
                    throw e
                }
            } finally {
                connection.disconnect()
            }
        }
    }
}