package com.example.group_chat.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.group_chat.data.local.entity.MessageEntity
import com.example.group_chat.domain.model.MessageModel
import org.threeten.bp.Instant

@Dao
interface MessageDAO
{
    @Insert(MessageEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMessage(msg:MessageEntity)

    @Insert(MessageEntity::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMessages(msg:List<MessageEntity>)


    @Query("SELECT * from messages m WHERE m.chat_id = :chatId ORDER BY m.created_at LIMIT :limit OFFSET :offset")
    suspend fun getMessages(chatId:String, limit: Int, offset:Int):List<MessageEntity>

    @Query("DELETE FROM messages WHERE id = :id")
    suspend fun deleteMessage(id: String)

    @Query("SELECT * from messages m WHERE m.chat_id = :chatId AND m.created_at > :followTime ORDER BY m.created_at DESC LIMIT :limit OFFSET :offset")
    suspend fun getMessagesWithTime(chatId:String, limit: Int, followTime: String, offset:Int):List<MessageEntity>
}