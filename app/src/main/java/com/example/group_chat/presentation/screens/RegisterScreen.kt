package com.example.group_chat.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.group_chat.R
import com.example.group_chat.domain.model.RegisterModel
import com.example.group_chat.presentation.viewModel.RegisterViewModel
import io.mockk.mockk

@Composable
fun RegisterScreen(registerViewModel: RegisterViewModel, navController: NavController){
    val registerInfo by registerViewModel.registerInfo.collectAsState()
    val registerState by registerViewModel.registerState.collectAsState()

    when (val state = registerState){
        is RegisterViewModel.RegisterState.RegisterNothing -> RegisterScreenView(navController,registerViewModel, registerInfo)
        is RegisterViewModel.RegisterState.RegisterSuccess -> navController.navigate("login")
        is RegisterViewModel.RegisterState.RegisterError -> RegisterScreenView(navController,registerViewModel,registerInfo
        ) {
            ScreenErrorView(state.e?.message ?: "Register unknown error") {
                registerViewModel.resetState()
            }
        }
    }

}

@Composable
fun ErrorTextView(text:String){

    Text(text = text,
        color = Color.White,
        modifier = Modifier.width(300.dp),
        fontSize = 25.sp,
        fontWeight = FontWeight.Bold)
}

@Preview(showBackground = true)
@Composable
fun ErrorTextPreview(){
    val text = "Username cannot be empty"
    Surface(modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.primary) {
        ErrorTextView(text)
    }

}


@Composable
fun RegisterScreenView(navController: NavController,
                       registerViewModel: RegisterViewModel,
                       registerInfo:RegisterModel,
                       textBlock: (@Composable () -> Unit)? = null){
    val context = LocalContext.current
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text= stringResource(id = R.string.app_name),
            fontSize = 30.sp,
            modifier = Modifier
                .padding(top=100.dp)
        )
        if (textBlock != null){
            Spacer(modifier = Modifier.padding(10.dp))
            textBlock()
        }

        CustomOutlinedTextField(registerViewModel::updateEmail,registerInfo.email,Icons.Outlined.MailOutline,stringResource(R.string.enter_email))
        CustomOutlinedTextField(registerViewModel::updateUsername,registerInfo.username,Icons.Outlined.AccountCircle,stringResource(R.string.enter_username))
        CustomOutlinedTextField(registerViewModel::updateFirstName,registerInfo.firstName,Icons.Outlined.Info,stringResource(R.string.enter_first_name))
        CustomOutlinedTextField(registerViewModel::updateSecondName,registerInfo.secondName,Icons.Outlined.Info,stringResource(R.string.enter_second_name))
        CustomOutlinedTextField(registerViewModel::updateAvatar,registerInfo.avatar,Icons.Outlined.Face,stringResource(R.string.enter_avatar))
        CustomOutlinedTextField(registerViewModel::updatePassword,registerInfo.password,Icons.Outlined.Lock,stringResource(R.string.enter_password))

        TextButton(
            onClick = {
                navController.navigate("login")
                      },
            modifier = Modifier.padding(start = 240.dp, top = 10.dp)
            )
        {
            Text(text = stringResource(R.string.login_link),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
        }

        CustomButton({registerViewModel.register(context = context)},
            text = stringResource(R.string.registry),
            shape = RectangleShape,
            modifier = Modifier
                .padding(top = 20.dp)
                .background(MaterialTheme.colorScheme.secondary)
                .width(300.dp)
                .shadow(10.dp,shape =  RectangleShape)
        )


    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenViewPreview(){
    val nav = NavController(LocalContext.current)
    val vm = mockk<RegisterViewModel>(relaxed = true)
    val registerInfo = RegisterModel(email = "", username = "")

    RegisterScreenView(nav,vm,registerInfo)
}