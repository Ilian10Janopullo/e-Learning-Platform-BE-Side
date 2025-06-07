package com.example.eLearningPlatform.services.classes;

import com.example.eLearningPlatform.models.entities.Course;
import com.example.eLearningPlatform.models.entities.ShoppingCart;
import com.example.eLearningPlatform.services.interfaces.TransferService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Transfer;
import com.stripe.param.TransferCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Service
public class TransferServiceImpl implements TransferService {

    @Value("${stripe.api.key}")
    private String stripeSecretKey;
    @Override
    public void transferFundsToLecturer(Set<Course> coursesSet) throws StripeException {

        Stripe.apiKey = stripeSecretKey;

        HashMap<String, Long> map = new HashMap<>();

        for(Course course : coursesSet){

            String lecturerAccountId = course.getLecturer().getStripeAccountId();
            long courseAmountInCents = Math.round(course.getPrice() * 100 *0.95);

            if(map.containsKey(lecturerAccountId)){
                map.put(lecturerAccountId, map.get(lecturerAccountId) + courseAmountInCents);
            } else {
                map.put(lecturerAccountId, courseAmountInCents);
            }
        }

        for(String lecturerStripeAccountId : map.keySet()){
            try {
                TransferCreateParams params = TransferCreateParams.builder()
                        .setAmount(map.get(lecturerStripeAccountId))
                        .setCurrency("usd")
                        .setDestination(lecturerStripeAccountId)
                        .build();

                Transfer.create(params);
            } catch (StripeException e) {
                System.err.println("Transfer failed for lecturer " + lecturerStripeAccountId + ": " + e.getMessage());
            }
        }


    }
}
