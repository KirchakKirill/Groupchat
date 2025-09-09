package com.example.group_chat.data.local

import android.content.Context
import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.example.group_chat.data.local.dao.ChatDao
import com.example.group_chat.data.local.dao.MessageDAO
import com.example.group_chat.data.local.dao.UserChatDao
import com.example.group_chat.data.local.dao.UserDao
import com.example.group_chat.data.local.entity.ChatEntity
import com.example.group_chat.data.local.entity.MessageEntity
import com.example.group_chat.data.local.entity.UserChatEntity
import com.example.group_chat.data.local.entity.UserEntity
import kotlinx.coroutines.Dispatchers


@Database(entities = [ChatEntity::class,MessageEntity::class,UserEntity::class,UserChatEntity::class], version = 16,exportSchema = false)
abstract class GroupChatDB:RoomDatabase()
{
    abstract fun getUserDao(): UserDao
    abstract fun  getChatDao(): ChatDao
    abstract fun  getMessageDao(): MessageDAO
    abstract fun  getUserChatDao(): UserChatDao
}

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<GroupChatDB>{
    val appContext = context.applicationContext
    val dbFile  = appContext.getDatabasePath("group_chat.db")
    return Room.databaseBuilder(
        context = appContext,
        klass = GroupChatDB::class.java,
        name = dbFile.absolutePath
    )
}

fun getRoomDataBase(
    builder : RoomDatabase.Builder<GroupChatDB>
): GroupChatDB{
    return  builder
        .setQueryCoroutineContext(Dispatchers.IO)
        .fallbackToDestructiveMigration(true)
        .build()
}