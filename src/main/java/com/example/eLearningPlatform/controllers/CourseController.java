package com.example.eLearningPlatform.controllers;

import com.example.eLearningPlatform.controllers.dto.respones.*;
import com.example.eLearningPlatform.models.entities.*;
import com.example.eLearningPlatform.models.enums.TagType;
import com.example.eLearningPlatform.services.interfaces.*;
import com.example.eLearningPlatform.utils.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.eLearningPlatform.repositories.ProgressRepository;
import com.example.eLearningPlatform.services.classes.CourseServiceImpl;
import com.example.eLearningPlatform.utils.SecurityUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;
    private final LecturerService lecturerService;
    private final ProgressRepository progressRepository;
    private final StudentService studentService;
    private final SecurityUtil securityUtil;
    private final LessonService lessonService;
    private final ProgressService progressService;

    @Autowired
    public CourseController(CourseService courseService, LecturerService lecturerService, ProgressRepository progressRepository, StudentService studentService, SecurityUtil securityUtil, LessonService lessonService, ProgressService progressService) {
        this.courseService = courseService;
        this.lecturerService = lecturerService;
        this.progressRepository = progressRepository;
        this.studentService = studentService;
        this.securityUtil = securityUtil;
        this.lessonService = lessonService;
        this.progressService = progressService;
    }

    @GetMapping("/list")
    public ResponseEntity<List<Course>> listCourses(
            @RequestParam(required = false) List<TagType> tag,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String courseName,
            @RequestParam(required = false) Long lecturer_id) {

        List<Course> courses = courseService.getAllCourses(CourseServiceImpl.withFilters(tag, maxPrice, lecturer_id, courseName));

        return ResponseEntity.ok(courses);
    }

    @GetMapping("/list-courses")
    public ResponseEntity<List<CourseResponseDTO>> listCoursesTest(
            @RequestParam(required = false) List<TagType> tag,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String courseName,
            @RequestParam(required = false) Long lecturer_id) {

        List<Course> courses = courseService.getAllCourses(CourseServiceImpl.withFilters(tag, maxPrice, lecturer_id, courseName));

        List<CourseResponseDTO> courseResponseDTOS = new ArrayList<>();

        for(Course course : courses){
            courseResponseDTOS.add(setCourseResponseDto(course));
        }

        return ResponseEntity.ok(courseResponseDTOS);
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<Course> getCourse(@PathVariable Long courseId) {

        if(securityUtil.getAuthenticatedUserId() == null){
            return ResponseEntity.status(401).body(null);
        }

        Course course = courseService.getCourseById(courseId);
        return ResponseEntity.ok(course);
    }

    @GetMapping("/get-course/{courseId}")
    public ResponseEntity<CourseStudentResponseDTO> getCourseTest(@PathVariable Long courseId) throws Throwable {

        Long id = securityUtil.getAuthenticatedUserId();

        if(id == null){
            return ResponseEntity.status(401).body(null);
        }

        Course course = courseService.getCourseById(courseId);

        CourseStudentResponseDTO responseDTO = new CourseStudentResponseDTO();

        responseDTO.setId(course.getId());
        responseDTO.setName(course.getName());
        responseDTO.setDescription(course.getDescription());
        responseDTO.setPrice(course.getPrice());
        responseDTO.setCourseCover(course.getCourseCover());
        responseDTO.setTags(course.getTags());
        responseDTO.setLecturer(course.getLecturer().getFirstName() + " " + course.getLecturer().getLastName());
        responseDTO.setCreatedAt(course.getCreatedAt());
        responseDTO.setRating(course.getAverageRating());

        List<ReviewResponseDTO> reviewResponseDTOS = new ArrayList<>();

        for(Review review : course.getReviews()){
            reviewResponseDTOS.add(FeedbackController.setReviewResponseDto(review));
        }

        responseDTO.setReviews(reviewResponseDTOS);

        List<Lesson> lessons;

        if(studentService.getUserById(id).isPresent()){
            lessons = lessonService.getAcceptedLessonsByCourseId(courseId);
        } else {
            lessons = lessonService.getLessonsByCourseId(courseId);
        }

        for(Lesson lesson : lessons){
            responseDTO.getLessons().add(lesson.getId());
        }

        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/lecturer-courses/{lecturerId}") //get the courses of a lecturer
    public ResponseEntity<List<CourseResponseDTO>> listCoursesOfALecturer(@PathVariable Long lecturerId) {

        List<Course> courses = courseService.getCoursesByLecturerId(lecturerId);
        List<CourseResponseDTO> courseResponseDTOS = new ArrayList<>();

        for(Course course : courses){
            courseResponseDTOS.add(setCourseResponseDto(course));
        }

        return ResponseEntity.ok(courseResponseDTOS);
    }

    @GetMapping("/student-number/{courseId}")
    public ResponseEntity<Long> studentsEnrolled(@PathVariable Long courseId) {
        return ResponseEntity.ok(progressRepository.countTotalStudentsInCourse(courseId));
    }

    @GetMapping("/completed-student-number/{courseId}")
    public ResponseEntity<Long> studentsCompleted(@PathVariable Long courseId) {
        return ResponseEntity.ok(progressRepository.countCompletedStudentsByCourseId(courseId));
    }

    @GetMapping("/progress-percentage/{courseId}")
    public ResponseEntity<Double> progressPercentage(@PathVariable Long courseId) {
        return ResponseEntity.ok(progressRepository.getAverageProgressForCourse(courseId));
    }

    @GetMapping("/course-lecturer/{courseId}") //get the lecturer of a course
    public ResponseEntity<Lecturer> getLecturerFromCourse(@PathVariable Long courseId) {

        Lecturer lecturer = lecturerService.getLecturerByCourseId(courseId).orElseThrow(() -> new RuntimeException("Lecturer not found!"));

        return ResponseEntity.ok(lecturer);
    }

    @GetMapping("/purchased-courses")
    public ResponseEntity<List<CourseResponseDTO>> getPurchasedCourses(@RequestParam(required = false) List<TagType> tag,
                                                           @RequestParam(required = false) String courseName) {
        Long studentId = securityUtil.getAuthenticatedUserId();
        Optional<Student> studentOpt = studentService.getUserById(studentId);
        if (studentOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<Course> courses = courseService.getPurchasedCourses(studentOpt.get(), CourseServiceImpl.withFiltersForPurchasedCourses(tag, courseName));

        List<CourseResponseDTO> courseResponseDTOS = new ArrayList<>();

        for(Course course : courses){

            CourseResponseDTO courseResponseDTO = setCourseResponseDto(course);
            double percentageCompleted = 0;

            if(progressService.getProgress(studentId, course.getId()) != null){
                percentageCompleted = ceilTwoDecimals(progressService.getProgress(studentId, course.getId()).getProgressPercentage());
                courseResponseDTO.setPercentageCompleted(percentageCompleted);
            }

            courseResponseDTOS.add(courseResponseDTO);
        }

        return ResponseEntity.ok(courseResponseDTOS);
    }

    static CourseResponseDTO setCourseResponseDto(Course course){
        CourseResponseDTO courseResponseDTO = new CourseResponseDTO();
        courseResponseDTO.setId(course.getId());
        courseResponseDTO.setName(course.getName());
        courseResponseDTO.setDescription(course.getDescription());
        courseResponseDTO.setPrice(course.getPrice());
        courseResponseDTO.setCourseCover(course.getCourseCover());
        courseResponseDTO.setTags(course.getTags());
        courseResponseDTO.setLecturer(course.getLecturer().getFirstName() + " " + course.getLecturer().getLastName());
        courseResponseDTO.setCreatedAt(course.getCreatedAt());
        courseResponseDTO.setRating(course.getAverageRating());

        List<ReviewResponseDTO> reviewResponseDTOS = new ArrayList<>();

        for(Review review : course.getReviews()){
            reviewResponseDTOS.add(FeedbackController.setReviewResponseDto(review));
        }

        courseResponseDTO.setReviews(reviewResponseDTOS);

        return courseResponseDTO;
    }

    static double ceilTwoDecimals(double value) {
        return Math.ceil(value * 100.0) / 100.0;
    }


}
