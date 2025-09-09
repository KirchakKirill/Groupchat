package com.example.group_chat.data.local.repository

import com.example.group_chat.Utils.toUserChatEntity
import com.example.group_chat.Utils.toUserChatRolesModel
import com.example.group_chat.data.local.dao.UserChatDao
import com.example.group_chat.data.local.entity.UserChatEntity
import com.example.group_chat.domain.model.UserChatRolesModel

class LocalUserChatRepositoryImpl(
    private val userChatDao: UserChatDao
):LocalUserChatRepository
{
    override suspend fun findChatsByUser(userId: String):List<UserChatRolesModel> {
        return userChatDao.findChatsByUser(userId).map { it.toUserChatRolesModel() }
    }

    override suspend fun followChats(userChatModels: List<UserChatRolesModel>) {
        userChatDao.followChats(userChatModels.map { it.toUserChatEntity() })
    }

    override suspend fun unfollowChat(userChatModel: UserChatRolesModel) {
        userChatDao.unfollowChat(userChatModel.toUserChatEntity())
    }

    override suspend fun getChatsFollowing(userId: String,chatsIds:List<String>): List<UserChatRolesModel> {
        return userChatDao.getChatsFollowing(userId,chatsIds).map{it.toUserChatRolesModel()}
    }

    override suspend fun followChat(userChatRolesModel:UserChatRolesModel)
    {
        userChatDao.followChat(userChatRolesModel.toUserChatEntity())
    }


}