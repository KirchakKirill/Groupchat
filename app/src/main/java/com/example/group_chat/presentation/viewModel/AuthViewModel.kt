package com.example.group_chat.presentation.viewModel

import android.accounts.AccountManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat.getString
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.group_chat.R
import com.example.group_chat.Utils.EncryptionHelper
import com.example.group_chat.domain.interactor.authentication.AuthUseCase
import com.example.group_chat.domain.interactor.authentication.LoginUseCase
import com.example.group_chat.domain.model.LoginRequestModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch



open class AuthViewModel(
    private val authUseCase: AuthUseCase,
    private val loginUseCase: LoginUseCase,
    private val encryptionHelper: EncryptionHelper
):ViewModel()
{
    private var _senderId = MutableStateFlow("")
    val senderId: StateFlow<String> = _senderId.asStateFlow()

    private var _authState = MutableStateFlow<AuthState>(AuthState.doNothing)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    lateinit var nameUser:String
        private set



    fun loginPassword(emailOrUsername:String, password:String)
    {
        viewModelScope.launch {
            val loginData = LoginRequestModel(
                usernameOrEmail = emailOrUsername,
                password = encryptionHelper.encrypt(password)
            )
            loginUseCase.invoke(loginData).onSuccess {
                _authState.value = it.token?.let { token ->
                      AuthState.Success(token)
                 } ?: AuthState.Error("Token cannot be a null")
                _senderId.value  = it.id
                nameUser = it.username
            }
                .onFailure {
                    _authState.value = AuthState.Error(it.message?: "Unknown error login")
                }
        }
    }

    fun  login(context: Context){

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(getString(context,R.string.client_id))
            .setAutoSelectEnabled(false)
            .build()

        Log.d("GIO",googleIdOption.toString())


        val request:GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .setPreferIdentityDocUi(true)
            .build()

        Log.d("REQUEST",request.toString())

        viewModelScope.launch {

            try {
                _authState.value = AuthState.Loading
                Log.d("AUTH", "Creating CredentialManager...")
                val credentialManager = CredentialManager.create(context)

                val checkGoogleServices =  isGooglePlayServicesAvailable(context)
                Log.d("AUTH","Google Play Services: $checkGoogleServices")

                Log.d("AUTH", "Getting credential...")
                val result:GetCredentialResponse = credentialManager.getCredential(context = context,
                    request = request)
                Log.d("AUTH","Credential type: ${result.credential.type}")
                val idToken =  handleSignIn(result)
                if (idToken == null) _authState.value =
                    AuthState.Error("Google token cannot be null")
                else {
                    verifyGoogleTokenAndGetJWT(idToken)

                }

            }catch (e:GetCredentialException){
                Log.e("AUTH", "Credential error: ${e.message}", e)
                _authState.value = AuthState.Error("Authentication error: ${e.message}")
            }
            catch (e:Exception){
                Log.e("AUTH", "Unexpected error: ${e.message}", e)
                _authState.value = AuthState.Error("Unexpected error")
            }
        }
    }

    private fun handleSignIn(credentialResponse: GetCredentialResponse):String? {
        val credential = credentialResponse.credential
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)
        {
            try {
                val googleIdTokenCredential = GoogleIdTokenCredential
                    .createFrom(credential.data)
                _senderId.value = getGoogleSub(googleIdTokenCredential.idToken)
                googleIdTokenCredential.displayName?.let {
                    nameUser = it
                }
                Log.d("ID",senderId.value)
                return  googleIdTokenCredential.idToken

            }
            catch(e:GoogleIdTokenParsingException)
            {
                Log.e(TAG,"Received an invalid google id token response", e)
            }
        }
        else {

            Log.e(TAG, "Unexpected type of credential")
        }
        return null
    }
    private fun getGoogleSub(idToken:String): String {
        val map = getPayloadElement(idToken,"sub")
        Log.d("SUB", map["sub"]!!)
        return map["sub"]!!
    }

    private fun getPayloadElement(idToken: String,vararg names:String):Map<String,String>{
        val segments = idToken.split(".")
        val payloadAsByteArray:ByteArray =  Base64.decode(segments[1], Base64.NO_PADDING)
        val payloadInJson = Gson().fromJson(payloadAsByteArray.toString(Charsets.UTF_8),JsonObject::class.java)
        return names.associateWith { payloadInJson.get(it).asString }
    }

    private fun addGoogleAccount(context: Context) {
        val intent = Intent(Settings.ACTION_ADD_ACCOUNT).apply {
            putExtra(Settings.EXTRA_ACCOUNT_TYPES, arrayOf("com.google"))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    private fun verifyGoogleTokenAndGetJWT(googleToken:String)
    {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
           val result =  authUseCase.verify(googleToken)
            result.onSuccess {
                _authState.value = AuthState.Success(it.token!!)
            }
                .onFailure { cause ->
                    _authState.value = AuthState.Error(
                        cause.message ?: "Error during verify Google token and create JWT"
                    )
                }
        }
    }

    private fun isGooglePlayServicesAvailable(context: Context): Boolean {
        return GoogleApiAvailability.getInstance()
            .isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS
    }

    fun resetState(){
        _authState.value = AuthState.doNothing
    }


    sealed class AuthState()
    {
        data object doNothing: AuthState()
        data object Loading : AuthState()
        data class Success(val token: String) : AuthState()
        data class Error(val message: String) : AuthState()
    }
}