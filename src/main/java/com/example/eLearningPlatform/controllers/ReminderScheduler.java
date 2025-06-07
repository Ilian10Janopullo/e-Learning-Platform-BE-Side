package com.example.eLearningPlatform.controllers;

import com.example.eLearningPlatform.config.services.EmailService;
import com.example.eLearningPlatform.models.entities.Progress;
import com.example.eLearningPlatform.models.entities.Student;
import com.example.eLearningPlatform.services.interfaces.ProgressService;
import com.example.eLearningPlatform.utils.EmailMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@EnableScheduling
public class ReminderScheduler {

    @Autowired
    private ProgressService progressService;

    @Autowired
    private EmailService emailService;

    // run every Monday at 9:00 AM
    @Scheduled(cron = "0 0 9 * * MON", zone = "Europe/Tirane")
    public void sendWeeklyIncompleteCourseReminders() {

        List<Progress> incomplete = progressService.findIncompleteEnrollments();

        HashMap<Student, List<Progress>> map = new HashMap<>();

        for(Progress progress : incomplete){

            Student student = progress.getStudent();
            List<Progress> progressList = new ArrayList<>();

            if(map.containsKey(student)){
                progressList = map.get(student);
                progressList.add(progress);
                map.put(student, progressList);
            } else {
                progressList.add(progress);
                map.put(student, progressList);
            }

        }

        for (Student student : map.keySet()) {

            String subject = "Reminder: Continue your learning journey!";
            String body = EmailMessages.incompleteCoursesReminderMessage(student, map.get(student));
            emailService.sendSimpleEmail(student.getEmail(), subject, body);
        }
    }
}
