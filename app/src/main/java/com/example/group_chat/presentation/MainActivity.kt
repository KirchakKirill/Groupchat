package com.example.group_chat.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.group_chat.di.chatModule
import com.example.group_chat.presentation.viewModel.AuthViewModel
import com.example.group_chat.presentation.viewModel.MainViewModel
import com.example.group_chat.presentation.screens.Navigation
import com.example.group_chat.presentation.viewModel.ChatsViewModel
import com.example.group_chat.presentation.viewModel.RegisterViewModel
import com.example.group_chat.presentation.viewModel.WSViewModel
import com.example.group_chat.ui.theme.GroupchatTheme
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.startKoin

class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModel()
    private val authViewModel: AuthViewModel by viewModel()
    private val wsViewModel: WSViewModel by viewModel()
    private val chatsViewModel:ChatsViewModel by viewModel()
    private val registerViewModel:RegisterViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startKoin {
            androidContext(this@MainActivity)
            modules(chatModule)
        }
        enableEdgeToEdge()
        setContent {
            GroupchatTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.primary
                ) {
                   Navigation(authViewModel,wsViewModel,chatsViewModel,registerViewModel)
                }
            }
        }
    }
}
