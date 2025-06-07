package com.example.eLearningPlatform.controllers;

import com.example.eLearningPlatform.models.entities.Order;
import com.example.eLearningPlatform.models.entities.ShoppingCart;
import com.example.eLearningPlatform.models.entities.Student;
import com.example.eLearningPlatform.models.enums.OrderStatus;
import com.example.eLearningPlatform.services.interfaces.TransferService;
import com.example.eLearningPlatform.utils.ResponseMessage;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.eLearningPlatform.services.interfaces.OrderService;
import com.example.eLearningPlatform.services.interfaces.ShoppingCartService;
import com.example.eLearningPlatform.services.interfaces.StudentService;
import com.example.eLearningPlatform.utils.SecurityUtil;

import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {


    @GetMapping("/success")
    public ResponseEntity<ResponseMessage> createOrder() {
        return ResponseEntity.ok(new ResponseMessage("Order created successfully!"));
    }

    @GetMapping("/fail")
    public ResponseEntity<ResponseMessage> fail() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseMessage("Payment Failed!"));
    }

}
