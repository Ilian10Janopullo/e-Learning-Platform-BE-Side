package com.example.eLearningPlatform.controllers;

import com.example.eLearningPlatform.controllers.dto.ResourceCreationDTO;
import com.example.eLearningPlatform.models.enums.LessonStatus;
import com.example.eLearningPlatform.utils.ResponseMessage;
import jakarta.validation.Valid;
import com.example.eLearningPlatform.models.entities.Lesson;
import com.example.eLearningPlatform.models.entities.Resources;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.eLearningPlatform.services.interfaces.LessonService;
import com.example.eLearningPlatform.services.interfaces.ResourcesService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/lecturer/courses/{courseId}/lessons/{lessonId}/resources")
public class ResourceController {

    private final LessonService lessonService;
    private final ResourcesService resourcesService;

    @Autowired
    public ResourceController(LessonService lessonService, ResourcesService resourcesService) {
        this.lessonService = lessonService;
        this.resourcesService = resourcesService;
    }
    @PostMapping(value = "/add-resource",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Resources> addResource(@PathVariable Long lessonId,
                                                 @RequestPart @Valid ResourceCreationDTO dto,
                                                 @RequestPart(name = "file", required = true) MultipartFile file,
                                                 @PathVariable String courseId) {
        Lesson lesson = lessonService.getLessonById(lessonId);

        Resources resource = new Resources();
        resource.setName(dto.getName());
        resource.setLesson(lesson);

        if (file != null && !file.isEmpty()) {
            resource.setFile(convertMultipartFileToByteArray(file));
        }


        lesson.getResources().add(resource);

        Resources savedResource = resourcesService.addResource(resource);
        lesson.setStatus(LessonStatus.PENDING);
        lessonService.updateLesson(lesson);

        return ResponseEntity.ok(savedResource);
    }

    @GetMapping
    public ResponseEntity<List<Resources>> getResources(@PathVariable Long lessonId) {
        List<Resources> resources = resourcesService.getResourcesByLesson(lessonId);
        return ResponseEntity.ok(resources);
    }

    @DeleteMapping("/delete-resource/{resourceId}")
    public ResponseEntity<ResponseMessage> deleteResource(@PathVariable Long lessonId,
                                                          @PathVariable Long resourceId, @PathVariable String courseId) {

        Resources resource = resourcesService.getResourceById(resourceId);

        if (!resource.getLesson().getId().equals(lessonId)) {
            return ResponseEntity.badRequest().body(new ResponseMessage("Resource does not belong to the specified lesson."));
        }

        resourcesService.deleteResource(resourceId);
        return ResponseEntity.ok(new ResponseMessage("Resource deleted successfully."));
    }

    @GetMapping(value = "/download/{resourceId}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadResource(@PathVariable Long resourceId) {

        Resources resource = resourcesService.getResourceById(resourceId);

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_PDF);

        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(resource.getName())
                .build());

        return new ResponseEntity<>(resource.getFile(), headers, HttpStatus.OK);
    }


    public byte[] convertMultipartFileToByteArray(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("Error converting file to byte array", e);
        }
    }
}
