package com.example.group_chat.presentation.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.MediaController
import android.widget.VideoView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.group_chat.Utils.Mapper
import com.example.group_chat.data.remote.WebSocketConfig.WebSocketMessageType
import com.example.group_chat.presentation.viewModel.WSViewModel
import com.example.group_chat.ui.theme.Pink
import java.io.File
import kotlinx.coroutines.delay

enum class MediaType {
    IMAGE, VIDEO, AUDIO, FILE
}

fun getMediaTypeFromUri(context: Context, uri: Uri): MediaType {
    val contentResolver = context.contentResolver
    val mimeType = contentResolver.getType(uri) ?: return MediaType.FILE

    return when {
        mimeType.startsWith("image/") -> MediaType.IMAGE
        mimeType.startsWith("video/") -> MediaType.VIDEO
        mimeType.startsWith("audio/") -> MediaType.AUDIO
        else -> MediaType.FILE
    }

}

@Composable
fun ChatScreen(wsViewModel: WSViewModel, chatId:String, senderId:String, token:String,navController: NavController,
               nameUser:String){
    val isConnected by wsViewModel.isConnected.collectAsState()
    val messages by wsViewModel.messages.collectAsState()
    val context = LocalContext.current

    val mediaContent = remember { mutableStateOf<String?>(null) }
    val mediaUri  = remember { mutableStateOf<Uri?>(null) }
    val mediaType = remember { mutableStateOf<MediaType?>(null) }
    val currentPortion by wsViewModel.currentPortion.collectAsState()

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.PickVisualMedia())
    {uri ->

        if(uri == null) return@rememberLauncherForActivityResult
        context.contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )

        Handler(Looper.getMainLooper()).postDelayed({
            Log.d("URI", uri.toString())
            Log.d("MEDIA_CONTENT", mediaContent.value ?: "Media content is null")
            mediaUri.value = uri
            mediaType.value = getMediaTypeFromUri(context, uri)
            mediaContent.value = if(mediaType.value == MediaType.VIDEO) Mapper.videoToBase64(context,uri) else Mapper.bitmapToByteArray(context, uri)
            Log.d("MEDIA_TYPE", mediaType.value?.name ?: "Media type is null")
        },100)
    }



    val selectMediaHandler = {launcher.launch(PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageAndVideo))}
    when(val connectionState = isConnected){
        is WSViewModel.ConnectionState.Connecting -> ChatConnectingView()
        is WSViewModel.ConnectionState.Error ->
            ScreenErrorView(connectionState.error?.message ?: "Load chat error,please,try again") { navController.popBackStack() }
        is WSViewModel.ConnectionState.Disconnected -> ChatDisconnectedView(wsViewModel,token,chatId,nameUser)
        is WSViewModel.ConnectionState.Connected -> ChatConnectedView(messages,wsViewModel,chatId,senderId,nameUser,{navController.popBackStack()},
                selectMediaHandler = selectMediaHandler,mediaType,mediaUri,mediaContent )
    }

}

@Composable
fun ChatConnectingView(){
    Box(modifier = Modifier.fillMaxSize())
    {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun ChatDisconnectedView(wsViewModel: WSViewModel,token: String,chatId: String,nameUser: String){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = { wsViewModel.connectWebSocket(token, chatId, nameUser) },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text("Connect", fontSize = 12.sp)
        }
    }
}

@Composable
fun ChatConnectedView(messages:List<WebSocketMessageType>,
                      wsViewModel: WSViewModel,
                      chatId:String,
                      senderId: String,
                      nameUser: String,
                      errorHandler: ()->Unit,
                      selectMediaHandler: ()->Unit,
                      mediaType: MutableState<MediaType?>,
                      mediaUri: MutableState<Uri?>,
                      mediaContent:MutableState<String?>) {

    val isLoading by remember {
         wsViewModel.isLoading }
    val hasMore by remember {
       wsViewModel.hasMore }


    Column(
        modifier =
            Modifier.fillMaxSize()
                .statusBarsPadding()
    )
    {

        if (hasMore && !isLoading) {
            IconButton(
                onClick = {
                    wsViewModel.preloadMessages(chatId) },
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Load older messages"
                )
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .animateContentSize()

        ) {
            itemsIndexed(items = messages,
                key = { index,message ->
                    when (message) {
                        is WebSocketMessageType.SystemMessage -> "system_${message.text}_$index"
                        is WebSocketMessageType.UserMessage -> "user_${message.data.id}_$index"
                        is WebSocketMessageType.MessageError -> "error_${System.currentTimeMillis()}_$index"
                    }
                })
            { _, message ->
                AnimatedVisibility(visible = true,
                    enter = fadeIn(animationSpec = tween(300)) +
                            slideInVertically(animationSpec = tween(300)) { it / 2 },
                    exit = fadeOut(animationSpec = tween(300)) +
                            slideOutVertically(animationSpec = tween(300)) { it / 2 }) {
                    when (message) {
                        is WebSocketMessageType.SystemMessage -> SystemMessageView(message)
                        is WebSocketMessageType.UserMessage -> UserMessageView(message,senderId)
                        is WebSocketMessageType.MessageError -> ScreenErrorView(message.error.message ?: "Load chats error", errorHandler)
                    }
                }

            }

        }
        MediaSendView(mediaUri, mediaType,mediaContent)
        EnterMessageFieldView(wsViewModel,chatId,senderId,nameUser,selectMediaHandler,mediaType,mediaContent,mediaUri)
    }

}

@Composable
fun SystemMessageView(message: WebSocketMessageType.SystemMessage) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message.text,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium,
                fontStyle = FontStyle.Italic
            ),
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

@Composable
fun UserMessageView(message: WebSocketMessageType.UserMessage, senderId: String){
    val fromMe = message.data.senderId == senderId
    val context = LocalContext.current
    Log.d(
        "FROM_ME",
        "senderId: $senderId, current senderId: ${message.data.senderId}, result: $fromMe"
    )
    val shape = RoundedCornerShape(
        topStart = if (fromMe) 12.dp else 2.dp,
        topEnd = if (fromMe) 2.dp else 12.dp,
        bottomEnd = 12.dp,
        bottomStart = 12.dp
    )
    val maxMediaWidth = 280.dp
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        horizontalArrangement = if (fromMe) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .shadow(8.dp, shape = shape)
                .clip(shape)
                .background(if (fromMe) Pink else MaterialTheme.colorScheme.secondary)
                .padding(
                    top = 5.dp,
                    bottom = 8.dp,
                    end = 10.dp,
                    start = 10.dp
                )
                .widthIn(max = if (message.data.mediaContent != null) maxMediaWidth else Dp.Unspecified)

        ) {
            Column {
                Text(
                    text = if (fromMe) "You" else message.data.username,
                    fontSize = 12.sp,
                    color = if (fromMe) Color(0xffeeeeee) else Color.Gray,
                    modifier = Modifier.align(if(fromMe) Alignment.End else Alignment.Start)
                        .then(if (message.data.mediaContent != null) Modifier.fillMaxWidth() else Modifier)
                )
                Spacer(modifier = Modifier.height(2.dp))
                if(message.data.contentType == "image/jpeg")
                {
                    val bitmap = Mapper.mapToBitmap(message.data.mediaContent)
                    bitmap?.let {
                        AsyncImage(
                            model = it,
                            contentDescription = null,
                            modifier = Modifier
                                .heightIn(max=200.dp)
                                .fillMaxWidth(),
                            contentScale = ContentScale.Crop
                        )
                    }

                }
                if(message.data.contentType == "video/mp4")
                {
                    var showVideoPlayer by remember { mutableStateOf(false) }
                    var videoFile by remember { mutableStateOf<File?>(null) }
                    LaunchedEffect(message.data.mediaContent) {
                        videoFile = Mapper.createVideoFile(
                            context = context,
                            mediaContent =  message.data.mediaContent
                        )
                    }
                    if (showVideoPlayer && videoFile !=null){
                        AndroidView(
                            factory = {
                                ctx->
                                VideoView(ctx).apply {
                                    setVideoPath(videoFile!!.absolutePath)
                                    setMediaController(MediaController(ctx))
                                    start()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }
                    else{
                        val bitmap = Mapper.getVideoThumbnail(context,message.data.mediaContent)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clickable { showVideoPlayer = true }
                        ) {
                            bitmap?.let {
                                AsyncImage(
                                    model = it,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(48.dp)
                                    .align(Alignment.Center)
                                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                    .padding(4.dp),
                                tint = Color.White
                            )

                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                Text(text = message.data.content,
                    color = Color.White,
                    modifier = Modifier.padding(top = if (message.data.mediaContent != null) 4.dp else 0.dp)
                        .align(if(fromMe) Alignment.End else Alignment.Start)
                )


            }
        }
    }
}

@Composable
fun MediaSendView(mediaUri: MutableState<Uri?>,
                  mediaType: MutableState<MediaType?>,
                  mediaContent: MutableState<String?>) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
    ) {
        if (mediaUri.value != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(bottom = 8.dp)
                    .shadow(4.dp, shape = RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                when (mediaType.value) {
                    MediaType.IMAGE -> {
                        AsyncImage(
                            model = mediaUri.value,
                            contentDescription = "Selected image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    MediaType.VIDEO -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.7f)),
                            contentAlignment = Alignment.Center
                        ) {
                            val bitmap = Mapper.getVideoThumbnailForSending(context = context, mediaUri.value!!)
                            bitmap?.let {
                                AsyncImage(
                                    model = it,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Video",
                                tint = Color.White,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }

                    else -> {
                        Text(
                            text = "Media file",
                            modifier = Modifier.align(Alignment.Center),
                            color = Color.White
                        )
                    }
                }

                IconButton(
                    onClick = {
                        mediaUri.value = null
                        mediaType.value = null
                        mediaContent.value = null
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(32.dp)
                        .background(
                            MaterialTheme.colorScheme.secondary,
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Remove media",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EnterMessageFieldView(wsViewModel: WSViewModel,
                          chatId: String,
                          senderId: String,
                          username:String,
                          selectMediaHandler: () -> Unit,
                          mediaType: MutableState<MediaType?>,
                          mediaContent: MutableState<String?>,
                          mediaUri: MutableState<Uri?>){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(6.dp)
            .shadow(4.dp, shape = RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.secondary)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    )
    {
        val (text, setText) = remember {
            mutableStateOf("")
        }
        TextField(
            value = text,
            onValueChange = {
                setText(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            placeholder = { Text(text = "Enter message...") }

        )
        IconButton(
            onClick = {
                selectMediaHandler.invoke()
            },
            modifier = Modifier
                .size(40.dp)
                .shadow(4.dp, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = null
            )
        }
        Spacer(modifier = Modifier.padding(2.dp))
        IconButton(
            onClick = {
                when (mediaType.value) {
                    MediaType.IMAGE -> {
                        wsViewModel.sendMediaMessage(chatId,text,senderId,mediaContent.value,"image/jpeg",username)
                    }
                    MediaType.VIDEO -> {
                        wsViewModel.sendMediaMessage(chatId,text,senderId,mediaContent.value,"video/mp4",username)
                    }
                    null -> {
                        wsViewModel.sendTextMessage(chatId, text, senderId,username)
                    }
                    else ->{

                    }
                }
                mediaUri.value = null
                mediaType.value = null
                mediaContent.value = null
                setText("")
            },
            modifier = Modifier
                .size(40.dp)
                .shadow(4.dp, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.Send,
                contentDescription = "Send message"
            )
        }
    }
}
