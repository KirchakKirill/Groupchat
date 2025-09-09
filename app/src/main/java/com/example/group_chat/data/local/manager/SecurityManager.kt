package com.example.group_chat.data.local.manager

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

val Context.dataStore:DataStore<Preferences> by preferencesDataStore(name=
"secure_prefs")

class SecurityManager(private val context: Context)
{
    private val tokenKey = stringPreferencesKey("access_token")
    private val userIdKey = stringPreferencesKey("user_id")
    private val userNameKey = stringPreferencesKey("username_prefs")
    private val expKey = stringPreferencesKey("exp_prefs")

    suspend fun save(token:String,userId:String,username:String,exp:String){
        context.dataStore.edit { preferences ->
            preferences[tokenKey] = token
            preferences[userIdKey] = userId
            preferences[userNameKey] = username
            preferences[expKey] = exp
        }
    }

    suspend fun getData():PrefsData? {
        return context.dataStore.data.map {
            preferences ->
            PrefsData(token = preferences[tokenKey],
                userId =  preferences[userIdKey],
                username = preferences[userNameKey],
                exp = preferences[expKey])
        }.firstOrNull()
    }

    suspend fun clear(){
        context.dataStore.edit {
            preferences ->
            preferences.remove(tokenKey)
            preferences.remove(userIdKey)
            preferences.remove(userNameKey)
            preferences.remove(expKey)
        }
    }
}

data class PrefsData(
    val token:String?,
    val userId:String?,
    val username:String?,
    val exp: String?
)