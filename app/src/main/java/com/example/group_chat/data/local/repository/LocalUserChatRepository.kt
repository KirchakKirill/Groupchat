package com.example.group_chat.data.local.repository

import com.example.group_chat.data.local.entity.UserChatEntity
import com.example.group_chat.domain.model.UserChatRolesModel

interface LocalUserChatRepository {

    suspend fun findChatsByUser(userId:String):List<UserChatRolesModel>

    suspend fun followChats(userChatModels: List<UserChatRolesModel>)

    suspend fun unfollowChat(userChatModel: UserChatRolesModel)

    suspend fun getChatsFollowing(userId:String,chatsIds:List<String>):List<UserChatRolesModel>

    suspend fun followChat(userChatRolesModel: UserChatRolesModel)
}