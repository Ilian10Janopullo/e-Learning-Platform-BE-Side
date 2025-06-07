package com.example.eLearningPlatform.controllers;

import com.example.eLearningPlatform.config.services.EmailService;
import com.example.eLearningPlatform.models.entities.Order;
import com.example.eLearningPlatform.models.enums.OrderStatus;
import com.example.eLearningPlatform.services.interfaces.OrderService;
import com.example.eLearningPlatform.services.interfaces.TransferService;
import com.example.eLearningPlatform.utils.EmailMessages;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/stripe/webhook")
public class StripeWebhookController {

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    @Autowired
    private OrderService orderService;

    @Autowired
    private TransferService transferService;

    @Autowired
    private EmailService emailService;

    @PostMapping
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {

        Event event = null;
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (SignatureVerificationException e) {
            System.out.println("Failed signature verification");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;

        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        }

        if (stripeObject == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to deserialize event data");
        }

        // Handle the event using a switch-case structure (similar to the Spark template)
        switch (event.getType()) {
            case "checkout.session.completed": {
                Session stripeSession = (Session) stripeObject;
                handleCheckoutSessionCompleted(stripeSession);
                break;
            }
            case "payment_intent.payment_failed":
            case "checkout.session.expired": {
                Session stripeSession = (Session) stripeObject;
                handleCheckoutSessionFailed(stripeSession);
                break;
            }
            default:
                System.out.println("Unhandled event type: " + event.getType());
        }

        return ResponseEntity.ok("Event received");
    }

    private void handleCheckoutSessionCompleted(Session stripeSession) {
        if ("paid".equals(stripeSession.getPaymentStatus())) {
            String sessionId = stripeSession.getId();
            Optional<Order> existingOrder = orderService.getOrderBySessionId(sessionId);
            if (existingOrder.isPresent()) {
                Order order = existingOrder.get();
                if (!OrderStatus.PAID.equals(order.getOrderStatus())) {
                    orderService.confirmOrderFromCart(order.getId());
                    emailService.sendSimpleEmail(order.getStudent().getEmail(), "Order Confirmation", EmailMessages.paymentConfirmationMessage(order, order.getStudent()));
                }

                try {
                    transferService.transferFundsToLecturer(order.getCourses());
                } catch (StripeException e) {
                    System.err.println("Transfer failed: " + e.getMessage());
                }
            }
        }
    }

    private void handleCheckoutSessionFailed(Session stripeSession) {
        Optional<Order> existingOrder = orderService.getOrderBySessionId(stripeSession.getId());
        if (existingOrder.isPresent()) {
            Order order = existingOrder.get();
            if (!OrderStatus.FAILED.equals(order.getOrderStatus())) {
                order.setOrderStatus(OrderStatus.FAILED);
                orderService.cancelOrder(order.getId());
                emailService.sendSimpleEmail(order.getStudent().getEmail(), "Order Not Processed", EmailMessages.paymentPendingMessage(order, order.getStudent()));
            }
        }
    }

}
