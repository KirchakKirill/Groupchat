package com.example.group_chat.data.local.repository

import com.example.group_chat.data.local.entity.UserEntity
import com.example.group_chat.domain.model.UserModel

interface LocalUserRepository
{
    suspend fun addUser(user: UserModel)

    suspend fun getUserById(id:String): UserModel

    suspend fun deleteById(id:String)

    suspend fun updateUser(user: UserModel)

    suspend fun addUsers(users: List<UserModel>)
}