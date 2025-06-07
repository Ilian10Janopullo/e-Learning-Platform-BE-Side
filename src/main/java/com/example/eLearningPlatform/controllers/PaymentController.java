package com.example.eLearningPlatform.controllers;
import com.example.eLearningPlatform.models.entities.Coupon;
import com.example.eLearningPlatform.models.entities.Course;
import com.example.eLearningPlatform.models.entities.ShoppingCart;
import com.example.eLearningPlatform.services.interfaces.CouponService;
import com.example.eLearningPlatform.services.interfaces.CourseService;
import com.example.eLearningPlatform.services.interfaces.OrderService;
import com.example.eLearningPlatform.services.interfaces.ShoppingCartService;
import com.example.eLearningPlatform.utils.SecurityUtil;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @Value("${stripe.api.key}")
    private String stripeSecretKey;

    @Autowired
    private CourseService courseService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private SecurityUtil securityUtil;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CouponService couponService;

    @PostMapping("/create-checkout-session")
    public Map<String, Object> createCheckoutSession() throws StripeException {
        Stripe.apiKey = stripeSecretKey;

        Long studentId = securityUtil.getAuthenticatedUserId();
        ShoppingCart cart = shoppingCartService.getCartByStudentId(studentId);
        Set<Course> courses = cart.getCourses();

        List<SessionCreateParams.LineItem> lineItems = new ArrayList<>();
        long totalAmount = 0;
        for (Course course : courses) {

            double coursePrice = course.getPrice();

            if(!couponService.getCouponsByCourseAndStudent(course.getId(), studentId).isEmpty() && couponService.getCouponsByCourseAndStudent(course.getId(), studentId).getFirst().getExpirationDate().isAfter(LocalDateTime.now())){
                Coupon coupon = couponService.getCouponsByCourseAndStudent(course.getId(), studentId).getFirst();
                double discountPercentage = coupon.getDiscountPercentage();
                double discountAmount = course.getPrice() * (discountPercentage / 100.0);
                coursePrice = course.getPrice() - discountAmount;
            }

            totalAmount += (long) coursePrice;
            SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                    .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                    .setCurrency("usd")
                                    .setUnitAmount((long) (coursePrice * 100))  // In cents
                                    .setProductData(
                                            SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                    .setName(course.getName())
                                                    .build()
                                    )
                                    .build()
                    )
                    .setQuantity(1L)
                    .build();
            lineItems.add(lineItem);
        }

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:5173/landpage/student-courses")
                .setCancelUrl("http://localhost:5173/landpage/courses-list")
                .addAllLineItem(lineItems)
                .build();

        Session session = Session.create(params);

        orderService.createOrderFromCart(studentId, cart, session.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("sessionId", session.getId());
        return response;
    }
}
