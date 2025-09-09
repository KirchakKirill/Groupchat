package com.example.group_chat.di

import com.example.group_chat.Utils.EncryptionHelper
import com.example.group_chat.data.local.repository.LocalMessageRepository
import com.example.group_chat.data.remote.service.AuthService
import com.example.group_chat.data.remote.service.MessageService
import com.example.group_chat.data.remote.dataSource.AuthRemoteDataSource
import com.example.group_chat.data.remote.dataSource.RemoteDataSource
import com.example.group_chat.data.remote.WebSocketConfig.WebSocketManager
import com.example.group_chat.data.remote.dataSource.ChatDataSource
import com.example.group_chat.data.repositoryImpl.AuthRepositoryImpl
import com.example.group_chat.data.repositoryImpl.ChatRepositoryImpl
import com.example.group_chat.data.repositoryImpl.MessageRepositoryImpl
import com.example.group_chat.data.remote.service.ChatService
import com.example.group_chat.domain.interactor.authentication.AuthUseCase
import com.example.group_chat.domain.interactor.authentication.LoginUseCase
import com.example.group_chat.domain.interactor.authentication.RegistryUseCase
import com.example.group_chat.domain.interactor.chats.ChatAllUseCase
import com.example.group_chat.domain.interactor.chats.ChatByUserUseCase
import com.example.group_chat.domain.interactor.chats.ChatCreateUseCase
import com.example.group_chat.domain.interactor.chats.ChatFollowUseCase
import com.example.group_chat.domain.interactor.chats.ChatsPartUseCase
import com.example.group_chat.domain.interactor.DataValidator
import com.example.group_chat.domain.interactor.chats.CheckFollowingChatsUseCase
import com.example.group_chat.domain.interactor.messages.MessagesAllUseCase
import com.example.group_chat.domain.interactor.messages.MessagesByChatUseCase
import com.example.group_chat.domain.interactor.messages.MessagesByTypeUseCase
import com.example.group_chat.domain.interactor.messages.MessagesByUserUseCase
import com.example.group_chat.domain.interactor.validation.ValidateEmail
import com.example.group_chat.domain.interactor.validation.ValidatePassword
import com.example.group_chat.domain.interactor.validation.ValidateUsername
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
import com.example.group_chat.presentation.viewModel.RegisterViewModel
import com.example.group_chat.presentation.viewModel.WSViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
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
        ChatFollowUseCase(get(),get())
    }

    single{
        ChatsPartUseCase(get(),get(),get())
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
        MessagesByChatUseCase(get<MessageRepository>(),get<DataValidator>(),get<LocalMessageRepository>(),get())
    }
    single {
        MessagesByTypeUseCase(get<MessageRepository>(),get<DataValidator>())
    }
    single {
        MessagesByUserUseCase(get<MessageRepository>(),get<DataValidator>())
    }
    single {
        WebSocketManager(get<OkHttpClient>(),get<Gson>(),get())
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

    single {
        ValidateUsername()
    }

    single {
        ValidatePassword()
    }
    single { ValidateEmail() }


    single { CheckFollowingChatsUseCase(get(),get(),get(),get(),get()) }


    single{
         dotenv{
            directory = "/assets"
             filename = ".env"
            ignoreIfMissing = true
        }
    }
    single { EncryptionHelper(get()) }


    viewModel {
        WSViewModel(
            webSocketManager = get(),
            gson = get(),
            localChatRepository = get(),
            messagesByChatUseCase = get(),
            localMessageRepository = get()
        )
    }

    viewModel {
        AuthViewModel(
            authUseCase = get(),
            loginUseCase = get(),
            encryptionHelper = get(),
            authManager = get(),
            userRepository = get()
        )
    }
    viewModel {
        ChatsViewModel(
            chatCreateUseCase = get(),
            chatFollowUseCase = get(),
            chatPartUseCase = get(),
            checkFollowingChatsUseCase = get(),
            localChatRepository = get(),
            localUserChatRepository = get()
        )
    }

    viewModel {
        RegisterViewModel(
            registryUseCase = get(),
            encryptionHelper = get(),
            validatePassword = get(),
            validateUsername = get(),
            validateEmail = get()
        )
    }



}