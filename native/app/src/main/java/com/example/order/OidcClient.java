package com.example.order;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Base64;

import net.openid.appauth.AppAuthConfiguration;
import net.openid.appauth.AuthState;
import net.openid.appauth.AuthorizationException;
import net.openid.appauth.AuthorizationRequest;
import net.openid.appauth.AuthorizationResponse;
import net.openid.appauth.AuthorizationService;
import net.openid.appauth.AuthorizationServiceConfiguration;
import net.openid.appauth.EndSessionRequest;
import net.openid.appauth.ResponseTypeValues;
import net.openid.appauth.connectivity.ConnectionBuilder;

import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.function.Consumer;

public class OidcClient {

  private static OidcClient singleton;

  public static OidcClient getInstance(Context applicationContext) {
    if (singleton != null) {
      return singleton;
    }
    singleton = new OidcClient(applicationContext);
    return singleton;
  }

  private AuthorizationServiceConfiguration idpMetadata;
  private AuthorizationService authorizationService;
  private AuthState authState;
  private OidcClient(Context applicationContext) {

    // http（httpsではない）で動かすための設定
    ConnectionBuilder connectionBuilder = uri -> {
      URL url = new URL(uri.toString());
      return (HttpURLConnection) url.openConnection();
    };

    AuthorizationServiceConfiguration.fetchFromIssuer(
      Uri.parse("http://10.0.2.2:18080/realms/master"),
      (idpMetadata, ex) -> {
        this.idpMetadata = idpMetadata;
      }, connectionBuilder);

    AppAuthConfiguration appAuthConfig = new AppAuthConfiguration.Builder()
      .setConnectionBuilder(connectionBuilder)
      .setSkipIssuerHttpsCheck(true)
      .build();

    this.authorizationService = new AuthorizationService(applicationContext, appAuthConfig);
    this.authState = new AuthState();
  }

  public Intent createAuthorizationRequestIntent() {
    AuthorizationRequest authRequest = new AuthorizationRequest.Builder(
      this.idpMetadata,
      "sample-native",
      ResponseTypeValues.CODE,
      Uri.parse("sample.native:/oidc-redirect")
    ).setScope("openid")
      .build();
    return this.authorizationService.getAuthorizationRequestIntent(authRequest);
  }

  public void handleAuthorizationResponse(Intent intent, Runnable afterLogin) {
    AuthorizationResponse authResponse = AuthorizationResponse.fromIntent(intent);
    AuthorizationException ex = AuthorizationException.fromIntent(intent);
    this.authState.update(authResponse, ex);
    if (authResponse == null) {
      return;
    }
    this.authorizationService.performTokenRequest(
      authResponse.createTokenExchangeRequest(),
      (tokenResponse, tokenEx) -> {
        if (tokenResponse != null) {
          this.authState.update(tokenResponse, tokenEx);
          afterLogin.run();
        }
      });
  }

  public String getClaimFromIdToken(String claimName) {
    try {
      String idToken = this.authState.getIdToken();
      String[] parts = idToken.split("\\.");
      byte[] decodedBytes = Base64.decode(parts[1], Base64.URL_SAFE);
      String payload = new String(decodedBytes);
      JSONObject jsonObject = new JSONObject(payload);
      return jsonObject.optString(claimName);
    } catch (Exception ex) {
      return null;
    }
  }

  public void performWithRefreshedAccessToken(Consumer<String> consumer) {
    this.authState.performActionWithFreshTokens(this.authorizationService, (accessToken, idToken, ex) -> {
      consumer.accept(accessToken);
    });
  }

  public Intent createLogoutRequestIntent() {
    EndSessionRequest endSessionRequest = new EndSessionRequest.Builder(this.idpMetadata)
        .setIdTokenHint(this.authState.getIdToken())
        .setPostLogoutRedirectUri(Uri.parse("sample.native:/oidc-redirect"))
        .build();
    return this.authorizationService.getEndSessionRequestIntent(endSessionRequest);
  }


  public void unauthenticate() {
    this.authState = new AuthState();
  }




}

