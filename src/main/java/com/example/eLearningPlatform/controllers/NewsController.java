package com.example.eLearningPlatform.controllers;
import com.example.eLearningPlatform.controllers.dto.CreateNewsDTO;
import com.example.eLearningPlatform.controllers.dto.UpdateNewsDTO;
import com.example.eLearningPlatform.models.entities.Admin;
import com.example.eLearningPlatform.models.entities.News;
import com.example.eLearningPlatform.models.enums.NewsCategory;
import com.example.eLearningPlatform.models.enums.NewsStatus;
import com.example.eLearningPlatform.services.interfaces.AdminService;
import com.example.eLearningPlatform.utils.ResponseMessage;
import com.example.eLearningPlatform.utils.SecurityUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.eLearningPlatform.services.classes.NewsServiceImpl;
import com.example.eLearningPlatform.services.interfaces.NewsService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;
    private final AdminService adminService;
    private final SecurityUtil securityUtil;

    @Autowired
    public NewsController(NewsService newsService, AdminService adminService, SecurityUtil securityUtil) {
        this.newsService = newsService;
        this.adminService = adminService;
        this.securityUtil = securityUtil;
    }

    @GetMapping("/list-news")
    public ResponseEntity<List<News>> getNews(
            @RequestParam(required = false) NewsCategory category,
            @RequestParam(required = false) String title) {

        Specification<News> spec = NewsServiceImpl.withFilters(category, title);
        List<News> newsList = newsService.getAllNews(spec);
        return ResponseEntity.ok(newsList);
    }

    @GetMapping("/get-news/{id}")
    public ResponseEntity<News> getNews(@PathVariable Long id) {

        Long userId = securityUtil.getAuthenticatedUserId();
        Optional<Admin> admin = adminService.getUserById(userId);

        if (admin.isEmpty()) {
            throw new RuntimeException("You are not an admin!");
        }

        News news = newsService.getNewsById(id).orElseThrow(() -> new RuntimeException("News not found"));
        return ResponseEntity.ok(news);
    }

    @GetMapping("/admin/list-news")
    public ResponseEntity<List<News>> getNewsAdmin(
            @RequestParam(required = false) NewsCategory category,
            @RequestParam(required = false) String title,
            @RequestParam(required = false)NewsStatus newsStatus){

        Specification<News> spec = NewsServiceImpl.withFiltersForAdmin(category, title, newsStatus);
        List<News> newsList = newsService.getAllNews(spec);
        return ResponseEntity.ok(newsList);
    }

    @PostMapping(value = "/create",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<News> createNews(
            @RequestPart("dto") @Valid CreateNewsDTO dto,
            @RequestPart(name = "image", required = false) MultipartFile image) {

        Long adminId = securityUtil.getAuthenticatedUserId();
        Admin admin = adminService.getUserById(adminId)
                .orElseThrow(() -> new RuntimeException("Authenticated admin not found"));

        if(dto.getNewsStatus().equals(NewsStatus.ARCHIVED)){
            throw new RuntimeException("News cannot be archived at creation!");
        }

        News news = new News();
        news.setTitle(dto.getTitle());
        news.setContent(dto.getContent());
        news.setNewsStatus(dto.getNewsStatus());
        news.setNewsCategory(dto.getNewsCategory());
        news.setSourceUrl(dto.getSourceUrl());
        news.setCreatedBy(admin);

        if (image != null && !image.isEmpty()) {
            try {
                news.setImage(image.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Error processing uploaded image", e);
            }
        }

        News createdNews = newsService.saveNews(news);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdNews);
    }

    @PutMapping(value = "/edit/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<News> editNews(@PathVariable Long id,
                                         @RequestPart("dto") @Valid UpdateNewsDTO dto,
                                         @RequestPart(name = "image", required = false) MultipartFile image) {

        News news = newsService.getNewsById(id)
                .orElseThrow(() -> new RuntimeException("News not found"));

        Long adminId = securityUtil.getAuthenticatedUserId();
        Admin admin = adminService.getUserById(adminId)
                .orElseThrow(() -> new RuntimeException("Authenticated admin not found"));

        news.setTitle(dto.getTitle());
        news.setContent(dto.getContent());
        news.setNewsStatus(dto.getNewsStatus());
        news.setNewsCategory(dto.getNewsCategory());
        news.setSourceUrl(dto.getSourceUrl());
        news.setCreatedBy(admin);

        if (image != null && !image.isEmpty()) {
            try {
                news.setImage(image.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Error processing uploaded image", e);
            }
        }

        News updatedNews = newsService.saveNews(news);
        return ResponseEntity.ok(updatedNews);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseMessage> deleteNews(@PathVariable Long id) {

        Long adminId = securityUtil.getAuthenticatedUserId();

        News news = newsService.getNewsById(id)
                .orElseThrow(() -> new RuntimeException("News not found"));

        if (adminService.getUserById(adminId).isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        newsService.deleteNews(id);
        return ResponseEntity.ok(new ResponseMessage("News deleted successfully."));
    }


}
