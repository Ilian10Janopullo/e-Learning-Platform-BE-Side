package com.example.eLearningPlatform.repositories;

import com.example.eLearningPlatform.models.entities.Course;
import com.example.eLearningPlatform.models.enums.TagType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {

    List<Course> findByLecturerId(Long lecturerId);
    List<Course> findByPriceLessThanEqual(Double maxPrice);

    List<Course> findByTagsContaining(TagType tag);

    List<Course> findAll(Specification<Course> courseSpecification);

    @Query("SELECT c FROM Course c JOIN c.students s WHERE s.id = :studentId")
    List<Course> findByStudentId(Long studentId, Specification<Course> courseSpecification);

}
