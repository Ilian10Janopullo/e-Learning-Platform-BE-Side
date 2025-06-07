package com.example.eLearningPlatform.services.classes;

import com.example.eLearningPlatform.models.entities.Coupon;
import com.example.eLearningPlatform.models.entities.Student;
import com.example.eLearningPlatform.repositories.CouponRepository;
import com.example.eLearningPlatform.repositories.CourseRepository;
import com.example.eLearningPlatform.repositories.LecturerRepository;
import com.example.eLearningPlatform.repositories.StudentRepository;
import com.example.eLearningPlatform.services.interfaces.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final LecturerRepository lecturerRepository;
    private final Random random = new Random();

    @Autowired
    public CouponServiceImpl(CouponRepository couponRepository, StudentRepository studentRepository, CourseRepository courseRepository, LecturerRepository lecturerRepository) {
        this.couponRepository = couponRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.lecturerRepository = lecturerRepository;
    }

    @Override
    @Transactional
    public Coupon create(Coupon coupon) {
        if (coupon.getStudent() == null) {
            List<Student> eligibleStudents = studentRepository.findByCoursesNotContaining(coupon.getCourse());
            if (!eligibleStudents.isEmpty()) {
                int randomIndex = random.nextInt(eligibleStudents.size());
                coupon.setStudent(eligibleStudents.get(randomIndex));
            }
        }

        if(!getCouponsByCourseAndStudent(coupon.getCourse().getId(), coupon.getStudent().getId()).isEmpty()){
            deleteCoupon(getCouponsByCourseAndStudent(coupon.getCourse().getId(), coupon.getStudent().getId()).getFirst());
        }

        return couponRepository.save(coupon);
    }

    @Override
    @Transactional
    public Optional<Coupon> redeemByCodeAndCourseId(String code, Long courseId) {
        LocalDateTime now = LocalDateTime.now();
        Optional<Coupon> couponOpt = couponRepository.findByCouponCodeAndExpirationDateAfterAndCourse(
                code, now, courseRepository.findById(courseId));
        //couponOpt.ifPresent(couponRepository::delete);
        return couponOpt;
    }

    @Override
    public List<Coupon> getCouponsByCourse(Long courseId) {
        LocalDateTime now = LocalDateTime.now();
        return couponRepository.findByCourseAndExpirationDateAfter(courseRepository.findById(courseId), now);
    }

    @Override
    public List<Coupon> getCouponsByStudent(Long studentId) {
        LocalDateTime now = LocalDateTime.now();
        return couponRepository.findByStudentAndExpirationDateAfterOrderByExpirationDateAsc(studentRepository.findById(studentId), now);
    }

    @Override
    public List<Coupon> getCouponsByCourseAndStudent(Long courseId, Long studentId) {
        LocalDateTime now = LocalDateTime.now();
        return couponRepository.findByCourseAndStudentAndExpirationDateAfter(courseRepository.findById(courseId), studentRepository.findById(studentId), now);
    }

    @Override
    public List<Coupon> getCouponsByLecturer(Long lecturerId) {
        LocalDateTime now = LocalDateTime.now();
        return couponRepository.findByLecturerAndExpirationDateAfter(lecturerRepository.findById(lecturerId), now);
    }

    @Override
    public Coupon getCouponsById(String couponId) {
        return couponRepository.findById(couponId).orElseThrow(() -> new RuntimeException("Coupon not found!"));
    }

    @Override
    @Transactional
    public boolean deleteCoupon(Coupon coupon) {
        try {
            couponRepository.delete(coupon);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Coupon updateCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }
}
