package com.example.group_chat.data.local.entity


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.group_chat.data.local.converter.InstantConverter
import org.threeten.bp.Instant


@Entity(tableName = "messages",
    foreignKeys = [ForeignKey(ChatEntity::class,
        parentColumns = ["id"],
        childColumns = ["chat_id"]
        ),
        ForeignKey(UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["sender_id"])],
    indices = [Index(value = ["chat_id"]),
        Index(value = ["sender_id"]),
    Index(value = ["created_at"])]
)
@TypeConverters(InstantConverter::class)
data class MessageEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id:Long,
    @ColumnInfo(name = "chat_id") val chatId:String,
    @ColumnInfo(name = "sender_id") val senderId:String,
    @ColumnInfo(name = "sender_name") val senderName:String,
    @ColumnInfo(name = "created_at") val createdAt:Instant,
    @ColumnInfo(name = "content") val content:String,
    @ColumnInfo(name = "contentType") val contentType:String,
    @ColumnInfo(name = "mediaContent",typeAffinity = ColumnInfo.TEXT) val mediaContent:String?
)
