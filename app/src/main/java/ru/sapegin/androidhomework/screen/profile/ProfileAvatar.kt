package ru.sapegin.androidhomework.screen.profile

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.ui.unit.dp

@Composable
fun ProfileAvatar(
    avatarUri: String?,
    modifier: Modifier = Modifier
) {
    val bitmap = remember(avatarUri) {
        val path = avatarUri?.let {
            Uri.parse(it).path
        }

        path?.let {
            BitmapFactory.decodeFile(it)?.asImageBitmap()
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap,
            contentDescription = "Фотография профиля",
            modifier = modifier.clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = "Нет фотографии",
                modifier = Modifier.size(72.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}