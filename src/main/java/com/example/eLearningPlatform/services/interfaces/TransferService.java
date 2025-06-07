package com.example.eLearningPlatform.services.interfaces;

import com.example.eLearningPlatform.models.entities.Course;
import com.example.eLearningPlatform.models.entities.ShoppingCart;
import com.stripe.exception.StripeException;

import java.util.Set;


public interface TransferService {
    void transferFundsToLecturer(Set<Course> courseSet) throws StripeException;

}
