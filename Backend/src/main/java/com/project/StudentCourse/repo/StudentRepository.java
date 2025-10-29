package com.project.StudentCourse.repo;

import com.project.StudentCourse.models.Course;
import com.project.StudentCourse.models.Student;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface StudentRepository extends MongoRepository<Student, String> {
    Optional<Student> findByEnrollmentId(String enrollmentId);
}
