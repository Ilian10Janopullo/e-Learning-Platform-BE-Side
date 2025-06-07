package com.example.eLearningPlatform.services.interfaces;

import com.example.eLearningPlatform.models.entities.Course;
import com.example.eLearningPlatform.models.entities.Student;
import com.example.eLearningPlatform.models.enums.TagType;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface CourseService {
    Course createCourse(Course course);
    Course getCourseById(Long id);
    List<Course> getCoursesByLecturerId(Long lecturerId);
    List<Course> getCoursesByPrice(Double maxPrice);
    List<Course> getAllCourses(Specification<Course> courseSpecification);
    List<Course> getPurchasedCourses(Student student, Specification<Course> courseSpecification);
    List<Course> getCoursesByTag(TagType tag);
    Course updateCourse(Course course);
    void deleteCourse(Long id);
}

