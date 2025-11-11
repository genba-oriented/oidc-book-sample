package com.example.order;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class OrderController {

  @GetMapping("/api/order-items")
  @CrossOrigin("http://localhost:3000")
  public List<OrderItem> getOrderItems(@AuthenticationPrincipal Jwt jwt) {

    System.out.println("user name=" + jwt.getClaim("preferred_username"));

    OrderItem keshigom = new OrderItem();
    keshigom.setId("oi01");
    keshigom.setProductName("消しゴム");
    keshigom.setQuantity(2);
    keshigom.setPrice(200);

    OrderItem note = new OrderItem();
    note.setId("oi02");
    note.setProductName("ノート");
    note.setQuantity(5);
    note.setPrice(1000);

    return List.of(keshigom, note);
  }
}
