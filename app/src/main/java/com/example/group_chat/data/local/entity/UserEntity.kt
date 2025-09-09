package com.example.group_chat.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id:String,
    @ColumnInfo(name = "username") val username:String,
    @ColumnInfo(name = "email") val email:String?,
    @ColumnInfo(name = "first_name") val firstName:String?,
    @ColumnInfo(name = "second_name") val secondName:String?,
    @ColumnInfo(name = "avatar") val avatar:String?,
)