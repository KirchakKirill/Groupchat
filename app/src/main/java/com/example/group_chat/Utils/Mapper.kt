package com.example.group_chat.Utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.example.group_chat.data.remote.response.ChatResponse
import com.example.group_chat.data.remote.response.LoginRequest
import com.example.group_chat.data.remote.response.MessageResponse
import com.example.group_chat.data.remote.response.RegistryRequest
import com.example.group_chat.data.remote.response.SuccessLoginResponse
import com.example.group_chat.domain.model.ChatModel
import com.example.group_chat.domain.model.LoginRequestModel
import com.example.group_chat.domain.model.LoginResponseModel
import com.example.group_chat.domain.model.MessageModel
import com.example.group_chat.domain.model.RegisterModel
import java.io.ByteArrayOutputStream
import kotlin.reflect.full.primaryConstructor
import android.util.Base64
import android.util.Log
import com.example.group_chat.data.remote.response.AuthResponse
import com.example.group_chat.data.remote.response.UserChatRolesResponse
import com.example.group_chat.domain.model.LoginGoogleModel
import com.example.group_chat.domain.model.UserChatRolesModel
import java.io.File
import java.io.FileOutputStream

object Mapper
{
    fun mapMessageResponseToModel(response: MessageResponse?):MapperResult<MessageModel>
    {
        if  (response==null) return MapperResult.Error(null,"Response should has a value")
         with(response){

            takeIf { listOf(chatId,senderId,content,contentType,username,createdAt,id).all { it!=null } }?.let{
                return MapperResult.Success(MessageModel(
                    id = id!!,
                    chatId = chatId!!,
                    senderId = senderId!!,
                    contentType = contentType!!,
                    content = content!!,
                    username = username!!,
                    mediaContent = mediaContent,
                    createdAt = createdAt!!
                ))
            } ?: MapperResult.Error(null,"Invalid data from response")
         }
        return  MapperResult.Error(null,"Unexpected error during mapping to message model")
    }

    fun mapToLoginGoogleModel(authResponse: AuthResponse?):MapperResult<LoginGoogleModel>{

        if (authResponse==null)  return MapperResult.Error(null,"Response should has a value")

        with(authResponse){
            takeIf { listOf(token,email,exp).all { !it.isNullOrEmpty() } }?.let {
                return MapperResult.Success(LoginGoogleModel(
                    token = token!!,
                    email = email!!,
                    exp = exp!!
                ))
            } ?: MapperResult.Error(null,"Invalid data from response")

        }
        return  MapperResult.Error(null,"Unexpected error during mapping to login google model")
    }
    fun mapToUserChatRolesModel(response: UserChatRolesResponse?): MapperResult<UserChatRolesModel> {
        Log.d("MAPPER","Response = $response")
            if (response == null) return MapperResult.Error(null, "Response should has a value")

            return with(response) {
                Log.d("MAPPER", "id = $id,userId = $userId,chatId = $chatId,role = $role,joinedAt = $joinedAt")
                if (listOf(id, userId, chatId, role, joinedAt).all { !it.isNullOrEmpty() }) {
                    MapperResult.Success(
                        UserChatRolesModel(
                            id = id!!,
                            chatId = chatId!!,
                            userId = userId!!,
                            role = role!!,
                            joinedAt = joinedAt!!
                        )
                    )
                } else {
                    MapperResult.Error(null, "Invalid data from response: $response")
                }
            }
        }


    fun mapChatResponseToModel(response: ChatResponse?):MapperResult<ChatModel>
    {

        return mapToModel(response, response?.id, response?.name,response?.createdAt)

    }

    fun mapToLoginResponseModel(response: SuccessLoginResponse?): MapperResult<LoginResponseModel>
    {
        val (fN,sN,ava) = listOf(response?.firstName,response?.secondName,response?.avatar).map { if (it.isNullOrEmpty()) "" else it }
       return with(response){
           mapToModel(response,
               this?.id,
               this?.token,
               this?.email,
               this?.username,
               fN,
               sN,
               ava,
               this?.exp
           )
        }

    }
    fun mapToRegistryRequest(model : RegisterModel):MapperResult<RegistryRequest>{
        return with(model){
            val op = listOf(username,email,password).all { it.isNotEmpty() }
            val (fN,sN,ava)  = listOf(firstName,secondName,avatar).map { it.ifEmpty { null } }
            op.takeIf { it }?.let {
                MapperResult.Success(
                    RegistryRequest(
                        email = email,
                        username = username,
                        password = password,
                        firstName = fN,
                        secondName = sN,
                        avatar = ava
                    )
                )
            } ?: MapperResult.Error(null,"Username, email and password cannot be empty")

        }
    }

    fun mapToLoginRequest(model: LoginRequestModel):MapperResult<LoginRequest>
    {
        val op = listOf(model.usernameOrEmail,model.password).all { it.isNotEmpty() }
        return if (op) MapperResult.Success(
            LoginRequest(
                usernameOrEmail = model.usernameOrEmail,
                password = model.password
            )
        )
        else MapperResult.Error(null,"Username or email and password cannot be empty")
    }

    fun bitmapToByteArray(context: Context, uri:Uri):String {
        return try {

            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            context.contentResolver.openInputStream(uri).use { stream ->
                val byteArray = stream?.readBytes() ?: return "ERROR: input stream is null"

                val options = BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }


                BitmapFactory.decodeStream(stream, null, options)

                Log.d("ImageInfo", "Width: ${options.outWidth}, Height: ${options.outHeight}")
                Log.d("ImageInfo", "MIME type: ${options.outMimeType}")

                val scale = calculateInSampleSize(options)

                val decodeOptions = BitmapFactory.Options().apply {
                    inSampleSize = scale
                    inPreferredConfig = Bitmap.Config.RGB_565

                }

                val bitmap = BitmapFactory.decodeByteArray(
                    byteArray, 0, byteArray.size, decodeOptions
                )
                Log.d("ImageInfo_bitmap", "Width: ${bitmap.width}, Height: ${bitmap.height}")


                val baos = ByteArrayOutputStream()

                bitmap.compress(Bitmap.CompressFormat.JPEG,50,baos)
                Base64.encodeToString(baos.toByteArray(),Base64.NO_PADDING)
            }
        }
        catch (e:Exception)
        {
            "ERROR: ${e.message}"
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int = 1000, reqHeight: Int = 750): Int {
        val (height, width) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            while (halfHeight / inSampleSize >= reqHeight &&
                halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    fun mapToBitmap(mediaContent: String?): Bitmap? {
        return mediaContent?.let {
            try {
                val decodeMedia = Base64.decode(it, Base64.NO_PADDING)
                BitmapFactory.decodeByteArray(decodeMedia, 0, decodeMedia.size)
            } catch (e: Exception) {
                null
            }
        }
    }

    fun createVideoFile(context:Context,mediaContent:String?):File?
    {
        if(mediaContent.isNullOrEmpty()) return null
        return try  {
            val videoData = Base64.decode(mediaContent,Base64.NO_PADDING)

            val tmpFile = File.createTempFile("video_",".mp4",context.cacheDir)

            FileOutputStream(tmpFile).use {
                fos -> fos.write(videoData)
            }
            tmpFile
        }
        catch (e:Exception)
        {
            Log.e("Mapper", "Error creating video file: ${e.message}")
            null
        }
    }

    fun getVideoThumbnail(context: Context,mediaContent: String?):Bitmap?{
        if (mediaContent.isNullOrEmpty()) return null
        return try {
            val videoFile = createVideoFile(context,mediaContent)
            videoFile?.let {
                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(it.absolutePath)
                retriever.frameAtTime
            }

        }catch (e:Exception){
            Log.e("Mapper","Error getting video thumbnail: ${e.message}")
            null
        }
    }

    fun videoToBase64(context: Context,uri:Uri):String{
        return try{
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            context.contentResolver.openInputStream(uri).use{stream->
                val byteArray = stream?.readBytes() ?: return "ERROR: input stream is null"
                Base64.encodeToString(byteArray,Base64.NO_PADDING)
            }
        }catch (e:Exception){
            "ERROR: ${e.message}"
        }
    }
    fun getVideoThumbnailForSending(context: Context,uri:Uri):Bitmap?{
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(context,uri)
            retriever.frameAtTime
        }catch (e:Exception){
            null
        }
    }

    private inline fun <reified R : Any> mapToModel(response: Any?, vararg  args:String?):MapperResult<R>{
        if (response == null) return  MapperResult.Error(null,"Response should has a value")
        return  try {
            takeIf { args.all { it!=null } }?.let {
                return MapperResult.Success(R::class.primaryConstructor!!.call(*args))
            } ?:MapperResult.Error(null,"Parameters must have values")
        }catch (e:Exception){
            MapperResult.Error(null,"Unexpected error: ${e.message}")
        }
    }


}

