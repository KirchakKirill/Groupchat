package com.example.group_chat.data.local.repository

import com.example.group_chat.Utils.toUserEntity
import com.example.group_chat.Utils.toUserModel
import com.example.group_chat.data.local.dao.UserDao
import com.example.group_chat.domain.model.UserModel

class LocalUserRepositoryImpl(
    private val userDao: UserDao) : LocalUserRepository
{
    override suspend fun addUser(user: UserModel ) {
        userDao.addUser(user.toUserEntity())
    }

    override suspend fun getUserById(id: String): UserModel {
            return userDao.getUserById(id).toUserModel()
    }

    override suspend fun deleteById(id: String) {
        userDao.deleteById(id)
    }

    override suspend fun updateUser(user: UserModel) {
        userDao.updateUser(user.toUserEntity())
    }

    override suspend fun addUsers(users: List<UserModel>){
        userDao.addUsers(users.map { it.toUserEntity() })
    }

}