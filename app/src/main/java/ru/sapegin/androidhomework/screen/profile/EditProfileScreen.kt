package ru.sapegin.androidhomework.screen.profile

import android.Manifest
import android.app.TimePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import ru.sapegin.androidhomework.data.notification.FavoriteClassAlarmScheduler
import ru.sapegin.androidhomework.viewmodel.ProfileViewModel
import ru.sapegin.androidhomework.viewmodel.ProfileViewModelFactory
import java.io.File
import java.util.Calendar
import java.util.Locale

@Composable
fun ProfileEditRoute(
    onBackClick: () -> Unit,
    onDone: () -> Unit
) {
    val context = LocalContext.current

    val viewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(context)
    )

    val draft by viewModel.draft.collectAsState()
    val error by viewModel.error.collectAsState()
    val isSavingAvatar by viewModel.isSavingAvatar.collectAsState()

    val photoPermissions = remember {
        getPhotoStoragePermissions()
    }

    var hasPhotoAccess by remember {
        mutableStateOf(hasPhotoStorageAccess(context))
    }

    var showAvatarSourceDialog by remember {
        mutableStateOf(false)
    }

    val storagePermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) {
            if (hasPhotoStorageAccess(context)) {
                hasPhotoAccess = true
            } else {
                onBackClick()
            }
        }

    LaunchedEffect(Unit) {
        if (hasPhotoStorageAccess(context)) {
            hasPhotoAccess = true
        } else {
            storagePermissionLauncher.launch(photoPermissions)
        }
    }

    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                if (viewModel.saveProfile()) {
                    onDone()
                }
            } else {
                Toast.makeText(
                    context,
                    "Для получения напоминаний необходимо разрешить уведомления",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.updateAvatarFromUri(uri.toString())
        }
    }

    var pendingCameraUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var pendingCameraFile by remember {
        mutableStateOf<File?>(null)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        val uri = pendingCameraUri

        if (success && uri != null) {
            viewModel.updateAvatarFromUri(uri.toString())
        } else {
            pendingCameraFile?.delete()
        }

        pendingCameraUri = null
        pendingCameraFile = null
    }

    fun launchCamera() {
        try {
            val directory = File(context.cacheDir, "camera")

            if (!directory.exists() && !directory.mkdirs()) {
                throw IllegalStateException(
                    "Не удалось создать папку для фотографии"
                )
            }

            val imageFile = File(
                directory,
                "profile_${System.currentTimeMillis()}.jpg"
            )

            val imageUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                imageFile
            )

            pendingCameraFile = imageFile
            pendingCameraUri = imageUri

            cameraLauncher.launch(imageUri)
        } catch (e: Exception) {
            pendingCameraFile?.delete()
            pendingCameraFile = null
            pendingCameraUri = null

            Toast.makeText(
                context,
                "Не удалось открыть камеру",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            launchCamera()
        } else {
            Toast.makeText(
                context,
                "Для съемки необходимо разрешение камеры",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun requestCamera() {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) {
            launchCamera()
        } else {
            cameraPermissionLauncher.launch(
                Manifest.permission.CAMERA
            )
        }
    }

    val isReminderTimeValid =
        FavoriteClassAlarmScheduler.isValidReminderTime(
            draft.reminderTime
        )

    fun saveProfile() {
        if (!isReminderTimeValid) {
            return
        }
        if (
            !FavoriteClassAlarmScheduler.canScheduleExactAlarms(
                context
            )
        ) {
            Toast.makeText(
                context,
                "Разрешите установку будильников и напоминаний",
                Toast.LENGTH_LONG
            ).show()

            FavoriteClassAlarmScheduler.requestExactAlarmPermission(
                context
            )

            return
        }
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )

            return
        }

        if (viewModel.saveProfile()) {
            onDone()
        }
    }

    if (hasPhotoAccess) {
        Scaffold(
            topBar = {
                EditProfileTopBar(
                    onBackClick = onBackClick
                )
            }
        ) { innerPadding ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                ProfileAvatar(
                    avatarUri = draft.avatarUri,
                    modifier = Modifier
                        .size(140.dp)
                        .align(Alignment.CenterHorizontally)
                        .clickable(enabled = !isSavingAvatar) {
                            showAvatarSourceDialog = true
                        }
                )

                Text(
                    text = "Нажмите на фотографию, чтобы изменить её",
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    style = MaterialTheme.typography.bodySmall
                )

                if (isSavingAvatar) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                OutlinedTextField(
                    value = draft.fullName,
                    onValueChange = viewModel::updateFullName,
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("ФИО")
                    },
                    singleLine = true
                )

                OutlinedTextField(
                    value = draft.position,
                    onValueChange = viewModel::updatePosition,
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Должность")
                    },
                    singleLine = true
                )

                OutlinedTextField(
                    value = draft.resumeUrl,
                    onValueChange = viewModel::updateResumeUrl,
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Ссылка на резюме/портфолио")
                    },
                    placeholder = {
                        Text("https://example.com/resume.pdf")
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Uri
                    ),
                    singleLine = true
                )

                OutlinedTextField(
                    value = draft.reminderTime,
                    onValueChange = viewModel::updateReminderTime,
                    modifier = Modifier.fillMaxWidth(),
                    label = {
                        Text("Время любимой пары")
                    },
                    placeholder = {
                        Text("HH:mm")
                    },
                    singleLine = true,
                    isError = !isReminderTimeValid,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text
                    ),
                    supportingText = {
                        if (!isReminderTimeValid) {
                            Text("Введите корректное время в формате HH:mm")
                        } else {
                            Text("Напоминание будет установлено на сегодня")
                        }
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                val currentTime = Calendar.getInstance()

                                val parts = draft.reminderTime.split(":")

                                val initialHour = parts
                                    .getOrNull(0)
                                    ?.toIntOrNull()
                                    ?.takeIf { it in 0..23 }
                                    ?: currentTime.get(
                                        Calendar.HOUR_OF_DAY
                                    )

                                val initialMinute = parts
                                    .getOrNull(1)
                                    ?.toIntOrNull()
                                    ?.takeIf { it in 0..59 }
                                    ?: currentTime.get(
                                        Calendar.MINUTE
                                    )

                                TimePickerDialog(
                                    context,
                                    { _, hour, minute ->
                                        val selectedTime = String.format(
                                            Locale.US,
                                            "%02d:%02d",
                                            hour,
                                            minute
                                        )

                                        viewModel.updateReminderTime(
                                            selectedTime
                                        )
                                    },
                                    initialHour,
                                    initialMinute,
                                    true
                                ).show()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AccessTime,
                                contentDescription = "Выбрать время"
                            )
                        }
                    }
                )

                error?.let { message ->
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Button(
                    onClick = {
                        saveProfile()
                    },
                    enabled = !isSavingAvatar && isReminderTimeValid,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Готово")
                }
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

    if (showAvatarSourceDialog && hasPhotoAccess) {
        AlertDialog(
            onDismissRequest = {
                showAvatarSourceDialog = false
            },
            title = {
                Text("Изменить фотографию")
            },
            text = {
                Text("Выберите источник фотографии")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showAvatarSourceDialog = false
                        galleryLauncher.launch("image/*")
                    }
                ) {
                    Text("Галерея")
                }
            },
            dismissButton = {
                Row {
                    TextButton(
                        onClick = {
                            showAvatarSourceDialog = false
                            requestCamera()
                        }
                    ) {
                        Text("Камера")
                    }

                    TextButton(
                        onClick = {
                            showAvatarSourceDialog = false
                        }
                    ) {
                        Text("Отмена")
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditProfileTopBar(
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text("Редактирование профиля")
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Назад"
                )
            }
        }
    )
}

private fun getPhotoStoragePermissions(): Array<String> {
    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            )
        }

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES
            )
        }

        else -> {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }
}

private fun hasPhotoStorageAccess(context: Context): Boolean {
    fun isGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
            isGranted(Manifest.permission.READ_MEDIA_IMAGES) ||
                    isGranted(
                        Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
                    )
        }

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
            isGranted(Manifest.permission.READ_MEDIA_IMAGES)
        }

        else -> {
            isGranted(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }
}
