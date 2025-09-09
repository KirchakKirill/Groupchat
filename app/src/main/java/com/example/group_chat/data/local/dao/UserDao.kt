package com.example.group_chat.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.group_chat.data.local.entity.UserEntity

@Dao
interface UserDao
{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(userEntity: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUsers(userEntity: List<UserEntity>)

    @Query("SELECT * FROM users u WHERE u.id = :id")
    suspend fun getUserById(id:String):UserEntity

    @Query("DELETE FROM users  WHERE id = :id")
    suspend fun deleteById(id:String)

    @Update
    suspend fun updateUser(userEntity: UserEntity)
}