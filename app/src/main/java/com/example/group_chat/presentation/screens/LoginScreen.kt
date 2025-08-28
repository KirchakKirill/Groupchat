package com.example.group_chat.presentation.screens


import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.group_chat.R
import com.example.group_chat.presentation.viewModel.AuthViewModel


@Composable
fun LoginScreen(authViewModel: AuthViewModel, navController: NavController)
{
    val authState by authViewModel.authState.collectAsState()
    val senderId by authViewModel.senderId.collectAsState() // ?
    val context  = LocalContext.current

    when (val state = authState) {

        is AuthViewModel.AuthState.Error ->{
           ScreenErrorView(state.message,{authViewModel.resetState()})
        }
        is AuthViewModel.AuthState.Loading ->{
            Box(modifier = Modifier.fillMaxSize())
            {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.secondary
                )
            }

        }
        is AuthViewModel.AuthState.Success -> {
            Log.d("TOKEN", state.token)
            navController.navigate("listChat/${senderId}/${state.token}/${authViewModel.nameUser}")


        }
        else -> {
            LoginView(authViewModel, context,navController)
        }

    }

}

@Composable
fun LoginView(
              authViewModel: AuthViewModel,
              context: Context,
              navController: NavController){
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            fontSize = 30.sp,
            modifier = Modifier
                .padding(top = 100.dp)
        )
        val emailOrUsername = remember { mutableStateOf("") }
        val password = remember { mutableStateOf("") }
        CustomOutlinedTextField(
            {emailOrUsername.value = it}, emailOrUsername.value, Icons.Outlined.AccountBox,
            stringResource(R.string.email_or_username)
        )
        CustomOutlinedTextField(
            { password.value = it} , password.value, Icons.Outlined.Lock,
            stringResource(R.string.enter_password)
        )
        Spacer(modifier = Modifier.padding(10.dp))
        TextButton(
            onClick = {
                navController.navigate("register")
            },
            modifier = Modifier.padding(start = 245.dp, top = 10.dp)
        )
        {
            Text(text = stringResource(R.string.registry_link),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        CustomButton({authViewModel.login(context)},
            painter = painterResource(R.drawable.google_icon28dp),
            text = stringResource(R.string.login_with_google),
            shape = RectangleShape,
            modifier = Modifier
                .padding(top = 20.dp)
                .background(MaterialTheme.colorScheme.secondary)
                .width(300.dp)
                .shadow(10.dp,shape =  RectangleShape)
        )
        CustomButton({authViewModel.loginPassword(emailOrUsername.value,password.value)},
            text = stringResource(R.string.custom_login),
            shape = RectangleShape,
            modifier = Modifier
                .padding(top = 20.dp)
                .background(MaterialTheme.colorScheme.secondary)
                .width(300.dp)
                .shadow(10.dp,shape =  RectangleShape)
        )
    }
}

//@Preview(showBackground = true)
//@Composable
//fun LoginViewPreview(){
//    val modifier = Modifier
//    val context = LocalContext.current
//    val fakeViewModel = mockk<AuthViewModel>(relaxed = true)
//    val nav = NavController(context)
//    LoginView(modifier,fakeViewModel,context, nav)
//
//
//}

