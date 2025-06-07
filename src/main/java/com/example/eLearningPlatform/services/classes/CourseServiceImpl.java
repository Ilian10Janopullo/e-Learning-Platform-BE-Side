package com.example.eLearningPlatform.services.classes;

import com.example.eLearningPlatform.models.entities.Student;
import com.example.eLearningPlatform.models.enums.AccountStatus;
import jakarta.persistence.criteria.Predicate;
import com.example.eLearningPlatform.models.entities.Course;
import com.example.eLearningPlatform.models.entities.Lecturer;
import com.example.eLearningPlatform.models.enums.TagType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.example.eLearningPlatform.repositories.CourseRepository;
import com.example.eLearningPlatform.repositories.LecturerRepository;
import com.example.eLearningPlatform.services.interfaces.CourseService;

import java.util.ArrayList;
import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final LecturerRepository lecturerRepository;

    @Autowired
    public CourseServiceImpl(CourseRepository courseRepository, LecturerRepository lecturerRepository) {
        this.courseRepository = courseRepository;
        this.lecturerRepository = lecturerRepository;
    }

    @Override
    public Course createCourse(Course course) {
        Lecturer lecturer = lecturerRepository.findById(course.getLecturer().getId())
                .orElseThrow(() -> new RuntimeException("Lecturer not found"));
        course.setLecturer(lecturer);
        return courseRepository.save(course);
    }

    @Override
    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
    }

    @Override
    public List<Course> getCoursesByLecturerId(Long lecturerId) {
        return courseRepository.findByLecturerId(lecturerId);
    }

    @Override
    public List<Course> getCoursesByPrice(Double maxPrice) {
        return courseRepository.findByPriceLessThanEqual(maxPrice);
    }

    @Override
    public List<Course> getAllCourses(Specification<Course> courseSpecification) {
        return courseRepository.findAll(courseSpecification);
    }

    @Override
    public List<Course> getPurchasedCourses(Student student, Specification<Course> courseSpecification) {
        return courseRepository.findByStudentId(student.getId(), courseSpecification);
    }

    @Override
    public List<Course> getCoursesByTag(TagType tag) {
        return courseRepository.findByTagsContaining(tag);
    }

    @Override
    public Course updateCourse(Course course) {
        return courseRepository.save(course);
    }

    @Override
    public void deleteCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        courseRepository.delete(course);
    }

    public static Specification<Course> withFilters(List<TagType> tags, Double maxPrice, Long lecturerId, String courseName) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Ensure that lecturer is not null
            predicates.add(cb.isNotNull(root.get("lecturer")));
            predicates.add(cb.notEqual(root.get("lecturer").get("accountStatus"), AccountStatus.DISABLED));

            if (tags != null && !tags.isEmpty()) {
                List<Predicate> tagPredicates = new ArrayList<>();
                for (TagType tag : tags) {
                    tagPredicates.add(cb.isMember(tag, root.get("tags")));
                }
                predicates.add(cb.or(tagPredicates.toArray(new Predicate[0])));
            }
            if (maxPrice != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
            }
            if (lecturerId != null) {
                predicates.add(cb.equal(root.get("lecturer").get("id"), lecturerId));
            }
            if (courseName != null && !courseName.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + courseName.toLowerCase() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }


    public static Specification<Course> withFiltersForPurchasedCourses(List<TagType> tags, String courseName) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (tags != null && !tags.isEmpty()) {
                List<Predicate> tagPredicates = new ArrayList<>();
                for (TagType tag : tags) {
                    tagPredicates.add(cb.isMember(tag, root.get("tags")));
                }
                predicates.add(cb.or(tagPredicates.toArray(new Predicate[0])));
            }

            if (courseName != null && !courseName.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + courseName.toLowerCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}

