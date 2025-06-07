package com.example.eLearningPlatform.utils;

import com.example.eLearningPlatform.models.entities.*;
import com.example.eLearningPlatform.models.enums.AccountStatus;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EmailMessages {
    public static String AdminBodyMessage(String firstName, String lastName) {
        return "Dear " + firstName + " " + lastName +",\n" +
                "\n" +
                "We regret to inform you that, due to recent actions associated with your account, we have made the decision to permanently delete it from our platform. This decision follows our commitment to maintaining a safe and respectful environment for all users.\n" +
                "\n" +
                "If you believe this action was taken in error or have any further inquiries, please feel free to reach out to us.\n" +
                "\n" +
                "Sincerely,\n" +
                "eLearning Platform Admin\n" +
                "epoka.learning@gmail.com";
    }

    public static String AdminBodyMessage(String firstName, String lastName, AccountStatus status) {

        if(status.equals(AccountStatus.DISABLED)){
            return "Dear " + firstName + " " + lastName +",\n" +
                    "\n" +
                    "We regret to inform you that, due to recent actions associated with your account, we have made the decision to disable it from our platform. This decision follows our commitment to maintaining a safe and respectful environment for all users.\n" +
                    "\n" +
                    "If you believe this action was taken in error or have any further inquiries, please feel free to reach out to us.\n" +
                    "\n" +
                    "Sincerely,\n" +
                    "eLearning Platform Admin\n" +
                    "epoka.learning@gmail.com";
        }

        return "Dear " + firstName + " " + lastName +",\n" +
                "\n" +
                "We are pleased to inform you that your account has been re-enabled on our platform. You can now log in and resume enjoying all the features and benefits available.\n" +
                "\n" +
                "If you have any questions or need further assistance, please feel free to contact our support team at epoka.learning@gmail.com.\n" +
                "\n" +
                "Thank you for your continued support.\n" +
                "\n" +
                "Best regards,\n" +
                "eLearning Platform Admin\n";
    }

    public static String RegisterBodyMessage(String firstName, String lastName) {
        return "Dear " + firstName + " " + lastName +",\n" +
                "\n" +
                "We are pleased to inform you that your account at eLearning Platform has been successfully created. You may now log in to our platform and begin exploring our services.\n" +
                "\n" +
                "If you require any assistance or have any questions, please do not hesitate to contact our support team at epoka.learning@gmail.com.\n" +
                "\n" +
                "Thank you for choosing eLearning Platform. We look forward to serving you.\n" +
                "\n" +
                "Sincerely,\n" +
                "eLearning Platform Admin\n";
    }

    public static String approveMaterialMessage(Lesson lesson, Student student){
        return "Dear " + student.getFirstName()  + " " + student.getLastName() + ",\n" +
                "\n" +
                "We are pleased to inform you that new learning material has just been published for "+ lesson.getCourse().getName() +" on our eLearning Platform. The new content is now ready for your review and study.\n" +
                "\n" +
                "Title: " + lesson.getTitle() + "\n" +
                "Description: " + lesson.getDescription() + "}\n" +
                "Published On: " + lesson.getCreatedAt() + "\n" +
                "\n" +
                "\n" +
                "We encourage you to explore this new resource at your earliest convenience. Should you have any questions, require clarification, or wish to discuss the material further, please feel free to reach out to your instructor, " + lesson.getCourse().getLecturer().getFirstName() + " " + lesson.getCourse().getLecturer().getLastName() + ", at " + lesson.getCourse().getLecturer().getEmail() +".\n" +
                "\n" +
                "Thank you for your continued dedication to your studies. We hope this material supports you in achieving your learning goals.\n" +
                "\n" +
                "Warm regards,\n" +
                "\n" +
                "eLearning Platform Team\n" +
                "eLearning Platform\n" +
                "epoka.learning@gmail.com\n" +
                "This is an automated message. Please do not reply directly to this email.";
    }

    public static String paymentConfirmationMessage(Order order, Student student) {
        return "Dear " + student.getFirstName() + " " + student.getLastName() + ",\n" +
                "\n" +
                "We’re excited to let you know that we have successfully received payment for your course purchase on our eLearning Platform. Here are the details of your transaction:\n" +
                "\n" +
                "Purchase ID: "      + order.getId()       + "\n" +
                "Amount Paid: "      + order.getTotalPrice()           + "\n" +
                "Payment Date: "     + order.getOrderDate()      + "\n" +
                "\n" +
                "Your enrollment is now confirmed, and you can access your new course immediately by logging in to your account and navigating to “My Courses.”\n" +
                "\n" +
                "If you have any questions about the course content or need technical assistance, please reach out to our support team at epoka.learning@gmail.com.\n" +
                "\n" +
                "Thank you for choosing our eLearning Platform to further your learning journey. We wish you great success in your studies!\n" +
                "\n" +
                "Warm regards,\n" +
                "\n" +
                "The eLearning Platform Team\n" +
                "epoka.learning@gmail.com\n" +
                "This is an automated message. Please do not reply directly to this email.";
    }

    public static String paymentPendingMessage(Order order, Student student) {
        return "Dear " + student.getFirstName() + " " + student.getLastName() + ",\n" +
                "\n" +
                "We noticed that your payment for the following course purchase on our eLearning Platform is not yet completed:\n" +
                "\n" +
                "Purchase ID: "      + order.getId()             + "\n" +
                "Amount Due: "       + order.getTotalPrice()      + "\n" +
                "Order Date: "       + order.getOrderDate()       + "\n" +
                "\n" +
                "To complete your enrollment and gain instant access to your course, please finish your payment at your earliest convenience. " +
                "You can do this by logging in to your account and visiting the “My Orders” section.\n" +
                "\n" +
                "If you encountered any issues during checkout or need assistance, our support team is here to help. " +
                "Please reach out to us at epoka.learning@gmail.com.\n" +
                "\n" +
                "Thank you for choosing our eLearning Platform. We look forward to welcoming you into the course soon!\n" +
                "\n" +
                "Warm regards,\n" +
                "\n" +
                "The eLearning Platform Team\n" +
                "epoka.learning@gmail.com\n" +
                "This is an automated message. Please do not reply directly to this email.";
    }

    public static String incompleteCoursesReminderMessage(Student student, List<Progress> progresses) {
        StringBuilder sb = new StringBuilder();
        sb.append("Dear ")
                .append(student.getFirstName())
                .append(" ")
                .append(student.getLastName())
                .append(",\n\n")
                .append("You have unfinished courses on our eLearning Platform. Here’s your current progress:\n\n");

        for (Progress e : progresses) {
            Course c = e.getCourse();
            sb.append("• ")
                    .append(c.getName())
                    .append(" — ")
                    .append(String.format("%.0f%% complete", e.getProgressPercentage()))
                    .append(")\n");
        }

        sb.append("If you need help, reply to this email or contact us at epoka.learning@gmail.com.\n\n")
                .append("Happy learning,\n\n")
                .append("The eLearning Platform Team\n")
                .append("epoka.learning@gmail.com\n")
                .append("This is an automated message. Please do not reply directly.");

        return sb.toString();
    }

    public static String couponNotificationMessage(Coupon coupon) {

        Course course  = coupon.getCourse();
        Student student = coupon.getStudent();
        double discountPercentage = coupon.getDiscountPercentage();
        LocalDateTime expiryDate = coupon.getExpirationDate();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
        String formattedDate = expiryDate.format(formatter);

        return "Dear " + student.getFirstName() + " " + student.getLastName() + ",\n\n" +
                "Good news! You’ve received an exclusive coupon for “" + course.getName() + "” on our eLearning Platform.\n\n" +
                "Discount: " + String.format("%.0f", discountPercentage) + "% off\n" +
                "Expires on: " + formattedDate + "\n\n" +
                "To redeem your discount, simply log in to your account, and just add add the course to your cart.\n\n" +
                "If you have any questions or need assistance, feel free to reach out to us at epoka.learning@gmail.com.\n\n" +
                "Happy learning,\n\n" +
                "The eLearning Platform Team\n" +
                "epoka.learning@gmail.com\n\n" +
                "This is an automated message. Please do not reply directly to this email.";
    }




}
