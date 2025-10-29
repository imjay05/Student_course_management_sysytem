package com.project.StudentCourse.repo;

import com.project.StudentCourse.models.StudyMaterial;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface StudyMaterialRepository extends MongoRepository<StudyMaterial, String> {
    List<StudyMaterial> findByStudentIdAndCourseId(String studentId, String courseId);
    List<StudyMaterial> findByStudentId(String studentId);
}
