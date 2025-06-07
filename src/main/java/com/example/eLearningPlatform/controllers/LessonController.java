package com.example.eLearningPlatform.controllers;

import com.example.eLearningPlatform.config.services.S3Service;
import com.example.eLearningPlatform.controllers.dto.LessonCreationDTO;
import com.example.eLearningPlatform.controllers.dto.LessonUpdateDTO;
import com.example.eLearningPlatform.controllers.dto.ResourceCreationDTO;
import com.example.eLearningPlatform.controllers.dto.respones.LessonResponseDTO;
import com.example.eLearningPlatform.models.entities.*;
import com.example.eLearningPlatform.models.enums.LessonStatus;
import com.example.eLearningPlatform.services.interfaces.*;
import com.example.eLearningPlatform.utils.ResponseMessage;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.eLearningPlatform.utils.SecurityUtil;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class LessonController {

    private final LessonService lessonService;
    private final SecurityUtil securityUtil;
    private final CourseService courseService;
    private final LecturerService lecturerService;
    private final S3Service s3Service;
    private final StudentService studentService;
    private final ProgressService progressService;

    @Autowired
    public LessonController(LessonService lessonService, SecurityUtil securityUtil, CourseService courseService, LecturerService lecturerService, S3Service s3Service, StudentService studentService, ProgressService progressService) {
        this.lessonService = lessonService;
        this.securityUtil = securityUtil;
        this.courseService = courseService;
        this.lecturerService = lecturerService;
        this.s3Service = s3Service;
        this.studentService = studentService;
        this.progressService = progressService;
    }

    @PostMapping(value = "/lecturer/courses/{courseId}/lessons/create-lesson",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Lesson> createLesson(@PathVariable Long courseId,
                                               @RequestPart("dto") @Valid LessonCreationDTO dto,
                                               @RequestPart(name = "video", required = true) MultipartFile video) throws IOException, InterruptedException {

        Course course = courseService.getCourseById(courseId);
        Long lecturerId = securityUtil.getAuthenticatedUserId();

        Optional<Lecturer> lecturerOpt = lecturerService.getUserById(lecturerId);

        if(lecturerOpt.isEmpty()){
            throw new RuntimeException("Lecturer could not be found!");
        }

        Lecturer lecturer = lecturerOpt.get();

        if(!Objects.equals(lecturer.getId(), course.getLecturer().getId())){
            throw new RuntimeException("This lecturer cannot create a lesson for this course!");
        }

        Lesson lesson = new Lesson();
        lesson.setTitle(dto.getTitle());
        lesson.setDescription(dto.getDescription());

        Lesson created = lessonService.createLesson(courseId, lesson);

        if (video != null && !video.isEmpty()) {

            created.setVideo(s3Service.upload(video,  UUID.randomUUID().toString()));
            created = lessonService.updateLesson(created);

        }

        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/student/courses/{courseId}/lessons")
    public ResponseEntity<List<Lesson>> getAcceptedLessons(@PathVariable Long courseId) {
        List<Lesson> lessons = lessonService.getAcceptedLessonsByCourseId(courseId);
        return ResponseEntity.ok(lessons);
    }

    @GetMapping("/lecturer/courses/{courseId}/lessons")
    public ResponseEntity<List<Lesson>> getAllLessons(@PathVariable Long courseId) {
        List<Lesson> lessons = lessonService.getLessonsByCourseId(courseId);
        return ResponseEntity.ok(lessons);
    }

    @GetMapping("/lesson/{lessonId}")
    public ResponseEntity<Lesson> getLesson(@PathVariable Long lessonId) throws IOException {
        Lesson lesson = lessonService.getLessonById(lessonId);

        Long userId = securityUtil.getAuthenticatedUserId();

        Optional<Student> optionalStudent = studentService.getUserById(userId);
        Optional<Lecturer> optionalLecturer = lecturerService.getUserById(userId);

        if(optionalStudent.isPresent()){
            if(!optionalStudent.get().getCourses().contains(lesson.getCourse())){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            } else {
                progressService.updateProgress(optionalStudent.get().getId(), lesson.getCourse().getId(), lessonId);
            }
        }

        if(optionalLecturer.isPresent()){
            if(!optionalLecturer.get().getCourses().contains(lesson.getCourse())){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
        }

        lesson.setVideo(s3Service.download(lesson.getVideo()));

        return ResponseEntity.ok(lesson);
    }

    @GetMapping("/get-lesson/{lessonId}")
    public ResponseEntity<LessonResponseDTO> getLessonTest(@PathVariable Long lessonId) throws IOException {
        Lesson lesson = lessonService.getLessonById(lessonId);

        Long userId = securityUtil.getAuthenticatedUserId();

        Optional<Student> optionalStudent = studentService.getUserById(userId);
        Optional<Lecturer> optionalLecturer = lecturerService.getUserById(userId);

        if(optionalStudent.isPresent()){
            if(!optionalStudent.get().getCourses().contains(lesson.getCourse())){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            } else {
                progressService.updateProgress(optionalStudent.get().getId(), lesson.getCourse().getId(), lessonId);
            }
        }

        if(optionalLecturer.isPresent()){
            if(!optionalLecturer.get().getCourses().contains(lesson.getCourse())){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
        }

        lesson.setVideo(s3Service.download(lesson.getVideo()));

        LessonResponseDTO lessonResponseDTO = new LessonResponseDTO();
        lessonResponseDTO.setLessonId(lesson.getId());
        lessonResponseDTO.setResources(lesson.getResources());
        lessonResponseDTO.setCourseId(lesson.getCourse().getId());
        lessonResponseDTO.setCreatedAt(lesson.getCreatedAt());
        lessonResponseDTO.setDescription(lesson.getDescription());
        lessonResponseDTO.setTitle(lesson.getTitle());
        lessonResponseDTO.setVideo(lesson.getVideo());

        for(Comment comment : lesson.getComments()){
            lessonResponseDTO.getComments().add(FeedbackController.convertToCommentsResponseDTO(comment));
        }

        return ResponseEntity.ok(lessonResponseDTO);
    }

    @PutMapping(value = "/update-lesson/{lessonId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Lesson> updateLesson(@PathVariable Long lessonId,
                                               @RequestPart @Valid LessonUpdateDTO dto,
                                               @RequestPart(name = "video", required = false) MultipartFile video) throws IOException, InterruptedException {
        Lesson lesson = lessonService.getLessonById(lessonId);
        Long lecturerId = securityUtil.getAuthenticatedUserId();

        Optional<Lecturer> lecturerOpt = lecturerService.getUserById(lecturerId);

        if(lecturerOpt.isEmpty()){
            throw new RuntimeException("Lecturer could not be found!");
        }

        Lecturer lecturer = lecturerOpt.get();

        if(!Objects.equals(lecturer.getId(), lesson.getCourse().getLecturer().getId())){
            throw new RuntimeException("This lecturer cannot update a lesson for this course!");
        }

        lesson.setTitle(dto.getTitle());
        lesson.setDescription(dto.getDescription());

        if (video != null && !video.isEmpty()) {

            lesson.setVideo(s3Service.upload(video, UUID.randomUUID().toString()));

        }

        lesson.setStatus(LessonStatus.PENDING);
        Lesson updatedLesson = lessonService.updateLesson(lesson);

        return ResponseEntity.ok(updatedLesson);
    }

    @DeleteMapping("/delete-lesson/{lessonId}")
    public ResponseEntity<ResponseMessage> deleteLesson(@PathVariable Long lessonId) {

        Long lecturerId = securityUtil.getAuthenticatedUserId();
        Lesson lesson = lessonService.getLessonById(lessonId);

        if (!lesson.getCourse().getLecturer().getId().equals(lecturerId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ResponseMessage("You are not authorized to delete this lesson."));
        }

        if(!lesson.getCourse().getStudents().isEmpty()){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "The course of the lesson you are trying to delete have students enrolled in it!");
        }

        lessonService.deleteLesson(lessonId);
        s3Service.delete(lesson.getVideo());
        return ResponseEntity.ok(new ResponseMessage("Lesson deleted successfully."));
    }


}
