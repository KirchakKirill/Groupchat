package com.example.group_chat.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.group_chat.data.local.converter.InstantConverter
import org.threeten.bp.Instant

@Entity(tableName = "user_chats",
    foreignKeys = [ForeignKey(UserEntity::class,
        parentColumns = ["id"],
        childColumns = ["userId"],
        onDelete = ForeignKey.CASCADE
    ),
    ForeignKey(ChatEntity::class,
        parentColumns = ["id"],
        childColumns = ["chatId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["chatId"]),
        Index(value = ["userId", "chatId"], unique = true)
    ])
@TypeConverters(InstantConverter::class)
data class UserChatEntity(
    @PrimaryKey
    @ColumnInfo("id") val id:String,
    @ColumnInfo("userId") val userId:String,
    @ColumnInfo("chatId") val chatId:String,
    @ColumnInfo("role") val role:String,
    @ColumnInfo("following_at") val followingAt:Instant
)