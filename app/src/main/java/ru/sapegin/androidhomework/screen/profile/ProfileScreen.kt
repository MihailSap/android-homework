package ru.sapegin.androidhomework.screen.profile

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.sapegin.androidhomework.viewmodel.ProfileViewModel
import ru.sapegin.androidhomework.viewmodel.ProfileViewModelFactory
import java.io.File

@Composable
fun ProfileRoute(
    onEditClick: () -> Unit
) {
    val context = LocalContext.current

    val viewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(context)
    )

    val profile by viewModel.profile.collectAsState()
    val error by viewModel.error.collectAsState()
    val isDownloading by viewModel.isDownloading.collectAsState()
    val downloadedResumePath by
    viewModel.downloadedResumePath.collectAsState()

    LaunchedEffect(downloadedResumePath) {
        val path = downloadedResumePath ?: return@LaunchedEffect

        try {
            val file = File(path)

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(
                Intent.createChooser(
                    intent,
                    "Открыть резюме"
                )
            )
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Не удалось открыть файл. Проверьте наличие PDF-просмотрщика.",
                Toast.LENGTH_LONG
            ).show()
        } finally {
            viewModel.clearDownloadedResume()
        }
    }

    ProfileScreen(
        fullName = profile.fullName,
        position = profile.position,
        avatarUri = profile.avatarUri,
        resumeUrl = profile.resumeUrl,
        error = error,
        isDownloading = isDownloading,
        onEditClick = onEditClick,
        onResumeClick = viewModel::downloadResume
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileScreen(
    fullName: String,
    position: String,
    avatarUri: String?,
    resumeUrl: String,
    error: String?,
    isDownloading: Boolean,
    onEditClick: () -> Unit,
    onResumeClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Профиль")
                },
                actions = {
                    IconButton(onClick = onEditClick) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Редактировать профиль"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProfileAvatar(
                avatarUri = avatarUri,
                modifier = Modifier.size(140.dp)
            )

            Text(
                text = fullName.ifBlank { "Имя не указано" },
                style = MaterialTheme.typography.headlineSmall
            )

            if (position.isNotBlank()) {
                Text(
                    text = position,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            if (resumeUrl.isBlank()) {
                Text(
                    text = "Ссылка на резюме не указана",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Button(
                    onClick = onResumeClick,
                    enabled = !isDownloading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isDownloading) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Text("Скачивание...")
                        }
                    } else {
                        Text("Резюме")
                    }
                }
            }

            error?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}