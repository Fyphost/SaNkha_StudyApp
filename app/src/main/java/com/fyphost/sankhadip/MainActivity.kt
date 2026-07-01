package com.fyphost.sankhadip

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.fyphost.sankhadip.ui.StudyApp
import com.fyphost.sankhadip.ui.theme.SanKhadipTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            SanKhadipTheme {
                StudyApp()
            }
        }
    }
}
