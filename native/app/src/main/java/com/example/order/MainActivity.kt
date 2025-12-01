/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.order

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.getValue


// okhttp https://medium.com/@appdevinsights/okhttp-in-android-4c2771141f79

class MainActivity : ComponentActivity() {

  private lateinit var model: MainActivityViewModel;

  private val startLogin = { ->
    val authIntent = this.model.createAuthorizationRequestIntent()
    this.loginLauncher.launch(authIntent)
  }
  private val loginLauncher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
  ) { activityResult ->
    this.model.handleAuthorizationResponse(activityResult.data!!);
  }

  private val startLogout = { ->
    val logoutIntent = this.model.createLogoutRequestIntent()
    this.logoutLauncher.launch(logoutIntent)
  }
  private val logoutLauncher = registerForActivityResult(
    ActivityResultContracts.StartActivityForResult()
  ) { activityResult ->
    this.model.unauthenticate()
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    val model: MainActivityViewModel by viewModels()
    this.model = model;
    setContent {
      val user: User by model.userState.collectAsState()
      Column(
        modifier = Modifier.padding(top=100.dp).fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
          text = "トップ画面",
          style = MaterialTheme.typography.displaySmall
        )
        if (!user.isAuthenticated) {
          BeforeLogin(startLogin)
        } else {
          AfterLogin(model, user!!.name, startLogout)
        }
      }
    }
  }
}

@Composable
fun BeforeLogin(startLogin: () -> Unit) {
  Text(
    text = "ログインしてください",
    style = MaterialTheme.typography.bodyLarge
  )
  Button(onClick = startLogin) {
    Text("ログイン")
  }
}

@Composable
fun AfterLogin(model: MainActivityViewModel, userName: String, startLogout: () -> Unit) {
  LaunchedEffect(Unit) {
    model.fetchOrderItems()
  }
  val orderItems: List<OrderItem> by model.orderItemsState.collectAsState()
  Text(
    text = "こんにちは" + userName + "さん",
    style = MaterialTheme.typography.bodyLarge
  )
  LazyColumn(Modifier.padding(50.dp)) {
    item {
      Row {
        Text(text = "ID", Modifier.weight(.25f))
        Text(text = "商品名", Modifier.weight(.25f))
        Text(text = "数量", Modifier.weight(.25f))
        Text(text = "値段", Modifier.weight(.25f))
      }
    }
    items(orderItems.size) { idx ->
      val orderItem = orderItems.get(idx)
      Row {
        Text(text = orderItem.id(), Modifier.weight(.25f))
        Text(text = orderItem.productName(), Modifier.weight(.25f))
        Text(text = orderItem.quantity().toString(), Modifier.weight(.25f))
        Text(text = orderItem.price().toString(), Modifier.weight(.25f))
      }
    }
  }
  Button(onClick = startLogout) {
    Text("ログアウト")
  }
}
