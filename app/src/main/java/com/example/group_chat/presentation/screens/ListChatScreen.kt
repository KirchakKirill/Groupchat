package com.example.group_chat.presentation.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.example.group_chat.domain.model.ChatModel
import com.example.group_chat.presentation.viewModel.ChatsViewModel
import io.mockk.mockk
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.group_chat.R


@Composable
fun ListChatScreen(chatsViewModel: ChatsViewModel,
                   navController: NavController,
                   senderId:String,
                   token:String,
                   nameUser:String)
{
    val chats by chatsViewModel.chats.collectAsState()
    val state by chatsViewModel.state.collectAsState()
    val chatUser by chatsViewModel.chatUser.collectAsState()

    LaunchedEffect(Unit) {
        chatsViewModel.getAllChats(token)
        chatsViewModel.getChatByUser(token)
    }

    when (state){
        is ChatsViewModel.ChatState.Loading ->{
            Box(modifier = Modifier.fillMaxSize())
            {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
        is ChatsViewModel.ChatState.ChatSuccess ->{
            ChatSuccessView(chatsViewModel,chats,chatUser,navController,senderId,token,nameUser)
        }
        is ChatsViewModel.ChatState.ChatError -> {
            ScreenErrorView((state as ChatsViewModel.ChatState.ChatError).e?.message ?: "Server error, please, try again later") { navController.popBackStack() }
        }
    }

}

@Composable
fun ChatSuccessView(chatsViewModel: ChatsViewModel,
                    chats:List<ChatModel>,
                    chatUser:List<ChatModel>,
                    navController: NavController,
                    senderId: String,
                    token:String,
                    nameUser: String)
{

    val isEnableDialog = remember { mutableStateOf(false)}

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        LazyColumn {
            items(chats, key = {it.id}){ chat->
                ChatCard(chat,chatsViewModel, navController, senderId,token,chatUser,nameUser)
            }
        }
        Spacer(modifier = Modifier.padding(10.dp))
        Button(onClick = {isEnableDialog.value = true},
            modifier = Modifier.align(Alignment.End)) {
            Text(text="Create?")
        }

        if (isEnableDialog.value)
            CustomDialogUI(chatsViewModel,isEnableDialog,token)
    }



}

@Composable
fun CustomDialogUI(chatsViewModel: ChatsViewModel,enableDialog: MutableState<Boolean>,token:String)
{
    Dialog(onDismissRequest = {enableDialog.value = false}) {
        CustomUI(chatsViewModel, enableDialog,token)
    }
}

@Composable
fun CustomUI(chatsViewModel: ChatsViewModel,enableDialog: MutableState<Boolean>,token: String) {
    Card(shape= RoundedCornerShape(10.dp),
        modifier = Modifier.padding(start = 10.dp,
            top = 5.dp,
            end = 10.dp,
            bottom = 5.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    )
    {
        Column(
            modifier = Modifier.background(Color.Transparent)
        ) {
            Image(painter = painterResource(R.drawable.create_chat_icon512dp),
                contentDescription = "",
                contentScale = ContentScale.Fit,
                colorFilter = ColorFilter.tint(
                    color = MaterialTheme.colorScheme.secondary
                ),
                modifier = Modifier
                    .padding(top=35.dp)
                    .height(70.dp)
                    .fillMaxWidth()
            )

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                val (dialogText,setDialogText) = remember { mutableStateOf("")}
                Text(text = "Create a new chat",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top=5.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.titleMedium
                )

                TextField(
                    value = dialogText,
                    onValueChange = {setDialogText(it)},
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    placeholder = {Text(text="Enter chat name...")}
                )

                Row(
                    modifier = Modifier.
                    padding(top= 10.dp)
                        .fillMaxWidth()
                        .background(Color.Transparent),
                    horizontalArrangement = Arrangement.SpaceAround
                )
                {
                    
                    val clickHandler  =  {
                        if (dialogText.isNotEmpty())
                        {
                            chatsViewModel.createChat(dialogText,token)
                            enableDialog.value = false
                        } }
                    val clickHandlerCancel = {enableDialog.value = false}
                    CustomButtonRow(
                        button1 = {
                        CustomButton(onClick = clickHandler,
                        modifier =  Modifier.padding(top=5.dp,bottom=5.dp),
                        text = "Create")
                        },
                        button2 = {
                            CustomButton(onClick = clickHandlerCancel,
                                modifier =  Modifier.padding(top=5.dp,bottom=5.dp) ,
                                text = "Cancel",
                            )
                        }
                    )


                }
            }

        }
    }
}

@Composable
fun ChatCard(chat:ChatModel,
             chatsViewModel: ChatsViewModel,
             navController: NavController,
             senderId:String,
             token:String,
             chatUser:List<ChatModel>,
             nameUser: String){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        onClick = { navController.navigate("chat/${chat.id}/$senderId/$token/$nameUser") },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.chat_icon_800dp),
                    contentDescription = "Chat",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text = chat.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
            )

            if (!chatUser.map { it.id }.contains(chat.id)) {
                val followButtonClickHandler = {
                    chatsViewModel.followChat(chatId = chat.id, token = token)
                }

                Button(
                    onClick = followButtonClickHandler,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 2.dp,
                        pressedElevation = 4.dp
                    ),
                    modifier = Modifier.height(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Follow",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Follow",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

        }
    }
}


@Preview(showBackground = true)
@Composable
fun ChatSuccessViewPreview(){
    val context = LocalContext.current
    val chatsViewModel = mockk<ChatsViewModel>(relaxed = true)
    val navController = NavController(context)
    val senderId = "id1"
    val token = "sglmrglksnfampaa4243smg"

    val chats = listOf(
        ChatModel("1","room1"),
        ChatModel("2","room2"),
        ChatModel("3","room3")
    )
    val chatUser = listOf(
        ChatModel("2","room2"),
        ChatModel("3","room3")
    )
    val nameUser = "User1"

    ChatSuccessView(chatsViewModel,chats,chatUser,navController,senderId,token,nameUser)
}