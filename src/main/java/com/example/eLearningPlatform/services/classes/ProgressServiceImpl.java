package com.example.eLearningPlatform.services.classes;

import com.example.eLearningPlatform.models.entities.*;
import com.example.eLearningPlatform.repositories.LessonRepository;
import com.example.eLearningPlatform.services.interfaces.CertificateService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.eLearningPlatform.repositories.ProgressRepository;
import com.example.eLearningPlatform.repositories.StudentRepository;
import com.example.eLearningPlatform.repositories.CourseRepository;
import com.example.eLearningPlatform.services.interfaces.ProgressService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ProgressServiceImpl implements ProgressService {

    private final ProgressRepository progressRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final CertificateService certificateService;

    public ProgressServiceImpl(ProgressRepository progressRepository, StudentRepository studentRepository, CourseRepository courseRepository, LessonRepository lessonRepository, CertificateService certificateService) {
        this.progressRepository = progressRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.lessonRepository = lessonRepository;
        this.certificateService = certificateService;
    }

    @Override
    @Transactional
    public Progress updateProgress(Long studentId, Long courseId, Long lessonId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Lesson not found!"));

        Progress progress = progressRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseGet(() -> {
                    Progress progress1 = new Progress();
                    progress1.setCourse(course);
                    progress1.setStudent(student);
                    progress1.setCompleted(false);
                    return progress1;
                });

        Set<Long> lessonsCompleted = progress.getCompletedLessonIds();

        if(lessonsCompleted.contains(lessonId)){
            return progress;
        }

        lessonsCompleted.add(lessonId);

        int lesson_number = lessonRepository.findAcceptedLessonsByCourseId(courseId).size();

        if (lesson_number == 0) {
            throw new RuntimeException("Course has no lessons");
        }

        int lessen_completed_number = lessonsCompleted.size();

        double progressPercentage = ((double) lessen_completed_number / lesson_number) * 100;

        progress.setProgressPercentage(progressPercentage);

        if(progress.isCompleted() && certificateService.getCertificateByStudentAndCourse(studentId, courseId).isEmpty()){
            certificateService.issueCertificate(studentId, courseId);
        }

        return progressRepository.save(progress);
    }

    @Override
    public Progress getProgress(Long studentId, Long courseId) {
        Optional<Progress> progressOpt = progressRepository.findByStudentIdAndCourseId(studentId, courseId);


        if(progressOpt.isPresent()){
            Progress progress =  progressOpt.get();

            Set<Long> lessonsCompleted = progress.getCompletedLessonIds();

            int lesson_number = lessonRepository.findAcceptedLessonsByCourseId(courseId).size();

            if (lesson_number == 0) {
                progress.setProgressPercentage(0);
            }

            int lessen_completed_number = lessonsCompleted.size();

            double progressPercentage = ((double) lessen_completed_number / lesson_number) * 100;

            progress.setProgressPercentage(progressPercentage);

            if(progress.getProgressPercentage() == 100 && !progress.isCompleted() && certificateService.getCertificateByStudentAndCourse(studentId, courseId).isEmpty()){
                certificateService.issueCertificate(studentId, courseId);
                progress.setCompleted(true);
            }

            return progressRepository.save(progress);
        } else {
            return null;
        }
    }

    @Override
    public List<Progress> getStudentProgress(Long studentId) {
        return progressRepository.findByStudentId(studentId);
    }

    @Override
    public List<Progress> getCourseProgress(Long courseId) {
        return progressRepository.findByCourseId(courseId);
    }

    @Override
    public long countCompletedStudents(Long courseId) {
        return progressRepository.countCompletedStudentsByCourseId(courseId);
    }

    @Override
    public double getAverageProgress(Long courseId) {
        return progressRepository.getAverageProgressForCourse(courseId);
    }

    @Override
    public List<Progress> findIncompleteEnrollments() {
        return progressRepository.findByProgressPercentageLessThan(100.00);
    }
}

