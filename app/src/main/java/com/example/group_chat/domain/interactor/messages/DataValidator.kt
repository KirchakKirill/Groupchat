package com.example.group_chat.domain.interactor.messages

import com.example.group_chat.Utils.MapperResult
import com.example.group_chat.Utils.NetworkResult
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
                val data:T = networkResult.data!!
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
                val data:List<T> = networkResult.data!!
                data.let { dt->
                    Result.success(dt.mapNotNull { d -> mapper(d).data})
                }
            }catch (e:Exception){
                Result.failure(e)
            }
        }


    }
}