package com.example.order;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class OrderBatchApplication {

  record TokenResponse(String access_token) {
  }

  private static String getAccessToken() {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    headers.setBasicAuth("sample-batch", "fSO2UEiS8xb54IAlkgNlXaoJJylQXY9s");
    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    body.add("grant_type", "client_credentials");
    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
    TokenResponse response = restTemplate.postForObject(
      "http://localhost:18080/realms/master/protocol/openid-connect/token",
      request,
      TokenResponse.class
    );
    return response.access_token();
  }

  record OrderItem(String id, String productName, Integer quantity, Integer price) {
  }

  public static void main(String[] args) {

    String accessToken = getAccessToken();

    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);
    HttpEntity<String> entity = new HttpEntity<>(headers);

    OrderItem[] orderItems = restTemplate.exchange(
      "http://localhost:8080/api/order-items",
      HttpMethod.GET,
      entity,
      OrderItem[].class
    ).getBody();

    for (OrderItem orderItem : orderItems) {
      System.out.println(orderItem);
    }
  }
}
