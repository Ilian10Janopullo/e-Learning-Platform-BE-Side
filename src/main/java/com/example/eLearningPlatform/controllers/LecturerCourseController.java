package com.example.eLearningPlatform.controllers;

import com.example.eLearningPlatform.controllers.dto.CourseCreationDTO;
import com.example.eLearningPlatform.controllers.dto.CourseUpdateDTO;
import com.example.eLearningPlatform.controllers.dto.respones.*;
import com.example.eLearningPlatform.services.interfaces.LessonService;
import jakarta.validation.Valid;
import com.example.eLearningPlatform.models.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.eLearningPlatform.services.interfaces.CourseService;
import com.example.eLearningPlatform.services.interfaces.LecturerService;
import com.example.eLearningPlatform.utils.SecurityUtil;
import com.example.eLearningPlatform.utils.ResponseMessage;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/lecturer/courses")
public class LecturerCourseController {

    private final CourseService courseService;
    private final LecturerService lecturerService;
    private final SecurityUtil securityUtil;
    private final LessonService lessonService;


    @Autowired
    public LecturerCourseController(CourseService courseService, LecturerService lecturerService, SecurityUtil securityUtil, LessonService lessonService) {
        this.courseService = courseService;
        this.lecturerService = lecturerService;
        this.securityUtil = securityUtil;
        this.lessonService = lessonService;
    }

    @GetMapping
    public ResponseEntity<List<CourseResponseDTO>> getCourses() {
        Long lecturerId = securityUtil.getAuthenticatedUserId();

        List<Course> courses = courseService.getCoursesByLecturerId(lecturerId);

        List<CourseResponseDTO> courseResponseDTOS = new ArrayList<>();

        for(Course course : courses){
            courseResponseDTOS.add(CourseController.setCourseResponseDto(course));
        }

        return ResponseEntity.ok(courseResponseDTOS);

    }

    @GetMapping("/get-course/{courseId}")
    public ResponseEntity<CourseLecturerResponseDTO> getCourseTest2(@PathVariable Long courseId) throws Throwable {

        Long id = securityUtil.getAuthenticatedUserId();

        if(id == null){
            return ResponseEntity.status(401).body(null);
        }

        Course course = courseService.getCourseById(courseId);

        CourseLecturerResponseDTO responseDTO = new CourseLecturerResponseDTO();

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

        List<Lesson> lessons = lessonService.getLessonsByCourseId(courseId);

        List<LessonResponseDTO> lessonResponseDTOS = new ArrayList<>();

        for(Lesson lesson : lessons){
            LessonResponseDTO lessonResponseDTO = new LessonResponseDTO();
            lessonResponseDTO.setLessonId(lesson.getId());
            lessonResponseDTO.setTitle(lesson.getTitle());
            lessonResponseDTO.setDescription(lesson.getDescription());
            lessonResponseDTO.setVideo(lesson.getVideo());
            lessonResponseDTO.setCourseId(lesson.getCourse().getId());
            lessonResponseDTO.setResources(lesson.getResources());
            lessonResponseDTO.setCreatedAt(lesson.getCreatedAt());
            lessonResponseDTOS.add(lessonResponseDTO);

            for(Comment comment : lesson.getComments()){
                lessonResponseDTO.getComments().add(FeedbackController.convertToCommentsResponseDTO(comment));
            }
        }

        responseDTO.setLessons(lessonResponseDTOS);

        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping(value = "/create-course",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseMessage> createCourse(@RequestPart("dto") @Valid CourseCreationDTO dto,
                                               @RequestPart(name = "courseCover", required = false) MultipartFile courseCover) {

        Long lecturerId = securityUtil.getAuthenticatedUserId();

        Lecturer lecturer = lecturerService.getUserById(lecturerId)
                .orElseThrow(() -> new RuntimeException("Lecturer not found"));

        if(dto.getPrice() > 0 && lecturer.getStripeAccountId() == null){
            throw new RuntimeException("Stripe Account ID is needed in order to create a paid course!");
        }

        Course course = new Course();
        course.setName(dto.getName());
        course.setPrice(dto.getPrice());
        course.setDescription(dto.getDescription());
        course.setTags(dto.getTags());

        if (courseCover != null && !courseCover.isEmpty()) {
            course.setCourseCover(convertMultipartFileToByteArray(courseCover));
        }

        course.setLecturer(lecturer);
        courseService.createCourse(course);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseMessage("Course Created Successfully!"));
    }

    @PutMapping(value = "/update-course/{courseId}" ,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseMessage> updateCourse(@PathVariable Long courseId, @RequestPart @Valid CourseUpdateDTO dto,
                                               @RequestPart(name = "courseCover", required = false) MultipartFile courseCover) {

        Long lecturerId =securityUtil.getAuthenticatedUserId();
        Course course = courseService.getCourseById(courseId);

        if (!course.getLecturer().getId().equals(lecturerId)) {
            throw new RuntimeException("You are not allowed to update this course!");
        }

        course.setName(dto.getName());
        course.setPrice(dto.getPrice());
        course.setDescription(dto.getDescription());
        course.setTags(dto.getTags());

        if (courseCover != null && !courseCover.isEmpty()) {
            course.setCourseCover(convertMultipartFileToByteArray(courseCover));
        }

        courseService.updateCourse(course);

        return ResponseEntity.ok(new ResponseMessage("Course updated Successfully!"));
    }

    @DeleteMapping("/delete-course/{courseId}")
    public ResponseEntity<ResponseMessage> deleteCourse(@PathVariable Long courseId) {

        Long lecturerId =securityUtil.getAuthenticatedUserId();
        Course course = courseService.getCourseById(courseId);

        if (!course.getLecturer().getId().equals(lecturerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ResponseMessage("You are not authorized to delete this course."));
        }

        if(!course.getStudents().isEmpty()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "The course you are trying to delete have students enrolled in it!");
        }

        courseService.deleteCourse(courseId);
        return ResponseEntity.ok(new ResponseMessage("Course deleted successfully"));
    }


    public byte[] convertMultipartFileToByteArray(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("Error converting file to byte array", e);
        }
    }

}
