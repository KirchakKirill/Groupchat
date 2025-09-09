package com.example.group_chat.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.group_chat.data.local.entity.ChatEntity

@Dao
interface ChatDao
{
    @Insert(ChatEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun addChats(chats:List<ChatEntity>)

    @Insert(ChatEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun addChat(chat:ChatEntity)

    @Query("SELECT * FROM chats c ORDER BY c.created_at DESC LIMIT :limit OFFSET :offset")
    suspend fun getChats(limit:Int, offset:Int):List<ChatEntity>

    @Query("SELECT * FROM chats c WHERE c.id = :chatId")
    suspend fun getChatById(chatId:String):ChatEntity

    @Query("DELETE FROM chats WHERE id = :id")
    suspend fun deleteChat(id:String)

    @Query("SELECT * FROM chats c WHERE c.id IN (:chatIds)")
    suspend fun existsChats(chatIds:List<String>):List<ChatEntity>


}