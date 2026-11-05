package ru.sapegin.androidhomework

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import ru.sapegin.androidhomework.navigation.AppNavigation
import ru.sapegin.androidhomework.ui.theme.AndroidHomeworkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidHomeworkTheme {
                AppNavigation()
            }
        }
    }
}