package com.example.order

import android.app.Application
import android.content.ContentValues.TAG
import android.content.Intent
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.lang.reflect.Type


class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
  private val oidcClient = OidcClient.getInstance(application.applicationContext);
  private val _userState: MutableStateFlow<User> = MutableStateFlow(User(false, null))
  val userState: StateFlow<User> = _userState.asStateFlow()

  private val _orderItemsState: MutableStateFlow<List<OrderItem>> =
    MutableStateFlow(ArrayList<OrderItem>())
  val orderItemsState: StateFlow<List<OrderItem>> = _orderItemsState.asStateFlow()

  private val gson = Gson();

  fun createAuthorizationRequestIntent(): Intent {
    return this.oidcClient.createAuthorizationRequestIntent()
  }
  fun handleAuthorizationResponse(intent: Intent) {
    this.oidcClient.handleAuthorizationResponse(intent, { ->
      _userState.update({ current ->
        val userName = this.oidcClient.getClaimFromIdToken("preferred_username")
        User(true, userName)
      });
    })
  }


  fun createLogoutRequestIntent(): Intent {
    return this.oidcClient.createLogoutRequestIntent()
  }

  fun unauthenticate() {
    this.oidcClient.unauthenticate();
    _userState.update({ current ->
      User(false, null)
    });
  }

  fun fetchOrderItmes() {
    this.oidcClient
      .performWithRefreshedAccessToken { accessToken ->
        val client = OkHttpClient()
        val url = "http://10.0.2.2:8080/api/order-items";
        val req = Request.Builder()
          .url(url)
          .header("Authorization", "Bearer " + accessToken)
          .build()

        client.newCall(req).enqueue(object : Callback {
          override fun onFailure(call: Call, e: IOException) {
            Log.i(TAG, e.toString())
          }
          override fun onResponse(call: Call, response: Response) {
            val bodyString = response.body?.string()
            val listType: Type? = object : TypeToken<List<OrderItem>>() {}.getType()
            val orderItems: List<OrderItem> = gson.fromJson(bodyString, listType)
            _orderItemsState.update { current -> orderItems }
          }
        })
      }
  }

}