package com.example.group_chat.Utils

import android.util.Base64
import com.google.gson.Gson
import com.google.gson.JsonObject

fun getPayloadElement(idToken: String,vararg names:String):Map<String,String>{
    val segments = idToken.split(".")
    val payloadAsByteArray:ByteArray =  Base64.decode(segments[1], Base64.NO_PADDING)
    val payloadInJson = Gson().fromJson(payloadAsByteArray.toString(Charsets.UTF_8), JsonObject::class.java)
    return names.associateWith { payloadInJson.get(it).asString }
}