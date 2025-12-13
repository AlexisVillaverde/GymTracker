package edu.itvo.trackergym

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import edu.itvo.trackergym.ui.navigation.AppNavigation
import edu.itvo.trackergym.ui.theme.TrackerGymTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TrackerGymTheme {
                AppNavigation()
            }
        }
    }
}