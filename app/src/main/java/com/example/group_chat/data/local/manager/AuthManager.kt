package com.example.group_chat.data.local.manager

import android.util.Log
import com.example.group_chat.Utils.getPayloadElement
import org.threeten.bp.Instant

class AuthManager(
    private val securityManager: SecurityManager
)
{
    suspend fun isValidAuthData():Result<PrefsData>{
        return try {
            val prefsData =  securityManager.getData()
            if (prefsData!=null)
            {
                with(prefsData){
                    val check =  listOf(token,userId,username,exp).all { it!=null }
                    if (check){
                        if (isValidToken(exp!!)){
                           return Result.success(prefsData)
                        }
                    }
                }
            }
            Result.failure(Exception("Preferences data cannot be null"))
        }
        catch (e:Exception)
        {
            Log.e("Preferences", e.message ?: "Error in method [isValidAuthData]")
            Result.failure(e)
        }

    }

    private fun isValidToken(exp:String):Boolean{
        try {
            val expTime = exp.toLong()
            Log.d("EXP_TIME", expTime.toString())
            val currentTime = System.currentTimeMillis()
            return  currentTime < expTime
        }catch (e:Exception){
            Log.e("ERROR_PARSE", e.message ?: "Unknown error during parse exp string to Instant")
            return false
        }

    }

    suspend fun save(token:String, userId:String, username:String,exp:String) = securityManager.save(token,userId,username,exp)
    suspend fun clear() = securityManager.clear()
}