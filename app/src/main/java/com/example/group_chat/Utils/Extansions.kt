package com.example.group_chat.Utils

import androidx.room.util.joinIntoString
import com.example.group_chat.data.local.entity.ChatEntity
import com.example.group_chat.data.local.entity.MessageEntity
import com.example.group_chat.data.local.entity.UserChatEntity
import com.example.group_chat.data.local.entity.UserEntity
import com.example.group_chat.domain.model.ChatModel
import com.example.group_chat.domain.model.MessageModel
import com.example.group_chat.domain.model.UserChatRolesModel
import com.example.group_chat.domain.model.UserModel
import org.threeten.bp.Instant

fun MessageModel.toMessageEntity(): MessageEntity {

    return MessageEntity(
        id = id.toLong(),
        chatId = chatId,
        senderId = senderId,
        senderName = username,
        createdAt = Instant.parse(createdAt),
        content = content,
        contentType = contentType,
        mediaContent = mediaContent
    )
}

fun MessageEntity.toMessageModel():MessageModel{
    return MessageModel(
        id = id.toString(),
        chatId = chatId,
        senderId = senderId,
        username = senderName,
        createdAt = createdAt.toString(),
        content = content,
        contentType = contentType,
        mediaContent = mediaContent
    )
}

fun ChatModel.toChatEntity():ChatEntity{
    return ChatEntity(
        id = id,
        name = name,
        createdAt = Instant.parse(createdAt)
    )
}

fun ChatEntity.toChatModel():ChatModel{
    return ChatModel(
        id = id,
        name = name,
        createdAt = createdAt.toString()
    )
}

fun UserModel.toUserEntity():UserEntity{
    return UserEntity(
        id = id,
        username = username,
        email =  email,
        firstName = firstName,
        secondName = secondName,
        avatar = avatar
    )
}

fun UserEntity.toUserModel():UserModel{
    return UserModel(
        id = id,
        username = username,
        email = email,
        firstName = firstName,
        secondName = secondName,
        avatar = avatar
    )
}

fun UserChatRolesModel.toUserChatEntity():UserChatEntity{
    return UserChatEntity(
        id = id,
        chatId = chatId,
        userId = userId,
        followingAt = Instant.parse(joinedAt),
        role = role
    )
}

fun UserChatEntity.toUserChatRolesModel():UserChatRolesModel{
    return UserChatRolesModel(
        id = id,
        chatId = chatId,
        userId = userId,
        role = role,
        joinedAt = followingAt.toString()
    )
}
