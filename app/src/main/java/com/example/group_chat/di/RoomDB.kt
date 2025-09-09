package com.example.group_chat.di

import android.content.Context
import com.example.group_chat.data.local.GroupChatDB
import com.example.group_chat.data.local.dao.ChatDao
import com.example.group_chat.data.local.dao.MessageDAO
import com.example.group_chat.data.local.dao.UserChatDao
import com.example.group_chat.data.local.dao.UserDao
import org.koin.dsl.module
import com.example.group_chat.data.local.getDatabaseBuilder
import com.example.group_chat.data.local.getRoomDataBase
import com.example.group_chat.data.local.manager.AuthManager
import com.example.group_chat.data.local.repository.LocalChatRepository
import com.example.group_chat.data.local.repository.LocalChatRepositoryImpl
import com.example.group_chat.data.local.repository.LocalMessageRepository
import com.example.group_chat.data.local.repository.LocalMessageRepositoryImpl
import com.example.group_chat.data.local.repository.LocalUserRepository
import com.example.group_chat.data.local.repository.LocalUserRepositoryImpl
import com.example.group_chat.data.local.manager.SecurityManager
import com.example.group_chat.data.local.repository.LocalUserChatRepository
import com.example.group_chat.data.local.repository.LocalUserChatRepositoryImpl

val DBModule = module {

    single{ provideGroupChatDB(get()) }
    single { provideUserDao(get()) }
    single { provideMessageDao(get()) }
    single { provideChatDao(get()) }
    single { provideUserChatDao(get()) }

    single<LocalUserRepository> { LocalUserRepositoryImpl(get()) }
    single<LocalChatRepository> { LocalChatRepositoryImpl(get()) }
    single<LocalMessageRepository> { LocalMessageRepositoryImpl(get()) }
    single<LocalUserChatRepository> { LocalUserChatRepositoryImpl(get()) }
    single { SecurityManager(get()) }
    single { AuthManager(get()) }

}

fun provideGroupChatDB(context:Context):GroupChatDB{
    return  getRoomDataBase(getDatabaseBuilder(context))
}
fun provideUserDao(groupChatDB: GroupChatDB):UserDao{
    return groupChatDB.getUserDao()
}
fun provideMessageDao(groupChatDB: GroupChatDB):MessageDAO{
    return groupChatDB.getMessageDao()
}
fun provideChatDao(groupChatDB: GroupChatDB):ChatDao{
    return groupChatDB.getChatDao()
}
fun provideUserChatDao(groupChatDB: GroupChatDB): UserChatDao {
    return groupChatDB.getUserChatDao()
}

