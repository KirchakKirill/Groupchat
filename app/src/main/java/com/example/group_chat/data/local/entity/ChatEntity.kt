package com.example.group_chat.data.local.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.group_chat.data.local.converter.InstantConverter
import org.threeten.bp.Instant

@Entity(tableName = "chats")
@TypeConverters(InstantConverter::class)
class ChatEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") var id: String,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "created_at") val createdAt:Instant
)