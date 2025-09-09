package com.example.group_chat.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.group_chat.data.local.entity.UserChatEntity


@Dao
interface UserChatDao {

    @Query("SELECT * FROM user_chats u WHERE u.userId = :userId")
    suspend fun findChatsByUser(userId:String):List<UserChatEntity>

    @Query("SELECT * FROM user_chats u WHERE u.userId = :userId AND u.chatId IN (:chatIds)")
    suspend fun getChatsFollowing(userId:String, chatIds:List<String>):List<UserChatEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun followChats(userChatEntities: List<UserChatEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun followChat(userChatEntity: UserChatEntity)

    @Delete
    suspend fun unfollowChat(userChatEntity: UserChatEntity)
}