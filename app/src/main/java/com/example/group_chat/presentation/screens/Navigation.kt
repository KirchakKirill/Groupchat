package com.example.group_chat.presentation.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.group_chat.presentation.viewModel.AuthViewModel
import com.example.group_chat.presentation.viewModel.ChatsViewModel
import com.example.group_chat.presentation.viewModel.RegisterViewModel
import com.example.group_chat.presentation.viewModel.WSViewModel

@Composable
fun Navigation(authViewModel: AuthViewModel,
               wsViewModel: WSViewModel,
               chatsViewModel: ChatsViewModel,
               registerViewModel: RegisterViewModel)
{
   val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ){
        composable("login"){
            LoginScreen(authViewModel,navController)
        }
        composable("chat/{chatId}/{senderId}/{token}/{nameUser}", arguments = listOf(
            navArgument("chatId"){
                type= NavType.StringType
            },
            navArgument("senderId"){
                type = NavType.StringType
            },
            navArgument("token"){
                type = NavType.StringType
            },
            navArgument("nameUser"){
                type = NavType.StringType
            }


        )){
            val chatId = it.arguments?.getString("chatId") ?: ""
            val senderId = it.arguments?.getString("senderId") ?: ""
            val token = it.arguments?.getString("token") ?: ""
            val nameUser = it.arguments?.getString("nameUser") ?: ""
            ChatScreen(wsViewModel,chatId,senderId,token,navController,nameUser)
        }
        composable("listChat/{senderId}/{token}/{nameUser}", arguments = listOf(
            navArgument("senderId"){
                type = NavType.StringType
            },
            navArgument("token"){
                type = NavType.StringType
            } ,
            navArgument("nameUser"){
                type = NavType.StringType
            }

        )){
            val senderId = it.arguments?.getString("senderId") ?: ""
            val token = it.arguments?.getString("token") ?: ""
            val nameUser = it.arguments?.getString("nameUser") ?: ""
            ListChatScreen(chatsViewModel,navController,senderId,token,nameUser)
        }

        composable("register"){
            RegisterScreen(registerViewModel = registerViewModel,navController)
        }
    }
}