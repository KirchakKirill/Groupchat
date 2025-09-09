package com.example.group_chat.domain.interactor

import android.util.Log
import com.example.group_chat.Utils.MapperResult
import com.example.group_chat.Utils.NetworkResult
import com.example.group_chat.data.remote.response.UserChatRolesResponse
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class DataValidator
{

    suspend fun <T,V> validate(repositoryCall: suspend() -> NetworkResult<T>,
                                   mapper: (T?)->MapperResult<V>,
                                   dispatcher:CoroutineDispatcher):Result<V>{
        return withContext(dispatcher)
        {
            try{
                val networkResult:NetworkResult<T> = repositoryCall()
                val data:T = networkResult.data ?: return@withContext Result.failure(Exception("${networkResult.errorCode}:${networkResult.message}"))
                data.let { dt->
                    val mapperResult = mapper.invoke(dt)
                    mapperResult.data?.let { d->
                        Result.success(d)
                    } ?: Result.failure(Exception((mapperResult as MapperResult.Error).message))

                }
            }
            catch(e:Exception)
            {
                Result.failure(e)
            }
        }
    }

    suspend fun <T,V> validateCollectionResponse(repositoryCall: suspend() -> NetworkResult<List<T>>,
                                           mapper: (T?)->MapperResult<V>,
                                           dispatcher:CoroutineDispatcher):Result<List<V>>
    {
        return withContext(dispatcher){
            try {
                val networkResult:NetworkResult<List<T>> = repositoryCall()
                Log.d("VALIDATE","i`m here")
                val data:List<T> = networkResult.data ?: return@withContext Result.failure(Exception("${networkResult.errorCode}:${networkResult.message}"))
                Log.d("VALIDATE","i`m here 2")
                val res = data.let { dt->
                    Result.success(dt.mapNotNull { d ->
                        mapper(d).data!!})
                }
                Log.d("VALIDATE","i`m here 3")
                res
            }catch (e:Exception){
                Result.failure(e)
            }
        }


    }
}