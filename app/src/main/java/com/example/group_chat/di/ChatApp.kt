package com.example.group_chat.di

import com.example.group_chat.Utils.EncryptionHelper
import com.example.group_chat.data.service.AuthService
import com.example.group_chat.data.service.MessageService
import com.example.group_chat.data.dataSource.AuthRemoteDataSource
import com.example.group_chat.data.dataSource.RemoteDataSource
import com.example.group_chat.data.WebSocketConfig.WebSocketManager
import com.example.group_chat.data.dataSource.ChatDataSource
import com.example.group_chat.data.repositoryImpl.AuthRepositoryImpl
import com.example.group_chat.data.repositoryImpl.ChatRepositoryImpl
import com.example.group_chat.data.repositoryImpl.MessageRepositoryImpl
import com.example.group_chat.data.service.ChatService
import com.example.group_chat.domain.interactor.authentication.AuthUseCase
import com.example.group_chat.domain.interactor.authentication.LoginUseCase
import com.example.group_chat.domain.interactor.authentication.RegistryUseCase
import com.example.group_chat.domain.interactor.chats.ChatAllUseCase
import com.example.group_chat.domain.interactor.chats.ChatByUserUseCase
import com.example.group_chat.domain.interactor.chats.ChatCreateUseCase
import com.example.group_chat.domain.interactor.chats.ChatFollowUseCase
import com.example.group_chat.domain.interactor.messages.DataValidator
import com.example.group_chat.domain.interactor.messages.MessagesAllUseCase
import com.example.group_chat.domain.interactor.messages.MessagesByChatUseCase
import com.example.group_chat.domain.interactor.messages.MessagesByTypeUseCase
import com.example.group_chat.domain.interactor.messages.MessagesByUserUseCase
import com.example.group_chat.domain.repository.AuthRepository
import com.example.group_chat.domain.repository.ChatRepository
import com.example.group_chat.domain.repository.MessageRepository
import com.example.group_chat.presentation.viewModel.AuthViewModel
import com.example.group_chat.presentation.viewModel.ChatsViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.group_chat.presentation.viewModel.MainViewModel
import com.example.group_chat.presentation.viewModel.RegisterViewModel
import com.example.group_chat.presentation.viewModel.WSViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv

const val BASE_URL ="http://10.0.2.2:8080"

val chatModule = module {


    single { HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    } }

    single {
        OkHttpClient.Builder()
            .addInterceptor(get<HttpLoggingInterceptor>())
            .build()
    }

    single{
        Retrofit.Builder()
            .client(get<OkHttpClient>())
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single {
        get<Retrofit>().create(MessageService::class.java)
    }

    single {
        get<Retrofit>().create(AuthService::class.java)
    }
    single {
        get<Retrofit>().create(ChatService::class.java)
    }
    single {
        ChatDataSource(get())
    }
    single<ChatRepository> {
        ChatRepositoryImpl(get())
    }
    single {
        ChatAllUseCase(get(), get())
    }
    single {
        ChatByUserUseCase(get(),get())
    }

    single {
        ChatCreateUseCase(get(),get())
    }
    single {
        ChatFollowUseCase(get())
    }
    single {
        DataValidator()
    }

    single {
        RemoteDataSource(get<MessageService>())
    }

    single {
        AuthRemoteDataSource(get<AuthService>())
    }

    single<MessageRepository> {
        MessageRepositoryImpl(get<RemoteDataSource>())
    }

    single<AuthRepository> {
        AuthRepositoryImpl(get<AuthRemoteDataSource>())
    }
    single {
        AuthUseCase(get<AuthRepository>())
    }

    single {
        MessagesAllUseCase(get<MessageRepository>(),get<DataValidator>())
    }
    single {
        MessagesByChatUseCase(get<MessageRepository>(),get<DataValidator>())
    }
    single {
        MessagesByTypeUseCase(get<MessageRepository>(),get<DataValidator>())
    }
    single {
        MessagesByUserUseCase(get<MessageRepository>(),get<DataValidator>())
    }
    single {
        WebSocketManager(get<OkHttpClient>(),get<Gson>())
    }
    single {
        GsonBuilder().serializeNulls().create()
    }

    single {
        RegistryUseCase(get())
    }

    single {
        LoginUseCase(get(),get())
    }

    single{
         dotenv{
            directory = "/assets"
             filename = ".env"
            ignoreIfMissing = true
        }
    }
    single { EncryptionHelper(get()) }

    viewModel {
        MainViewModel(
            messagesAllUseCase = get(),
            messagesByChatUseCase = get(),
            messagesByTypeUseCase = get(),
            messagesByUserUseCase = get()
        )
    }
    viewModel {
        WSViewModel(
            webSocketManager = get(),
            gson = get()
        )
    }

    viewModel {
        AuthViewModel(
            authUseCase = get(),
            loginUseCase = get(),
            encryptionHelper = get()
        )
    }
    viewModel {
        ChatsViewModel(
            chatAllUseCase = get(),
            chatByUserUseCase = get(),
            chatCreateUseCase = get(),
            chatFollowUseCase = get()
        )
    }

    viewModel {
        RegisterViewModel(
            registryUseCase = get(),
            encryptionHelper = get()
        )
    }



}