package com.project.StudentCourse.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "study_materials")
public class StudyMaterial {

    @Id
    private String id;
    private String fileName;
    private String courseId;
    private String studentId;
    private String fileId; // GridFS file ID
    private String thumbnailId; // GridFS thumbnail ID
    private LocalDateTime uploadDate;
    private long fileSize;

    public StudyMaterial() {
        this.uploadDate = LocalDateTime.now();
    }

    public StudyMaterial(String fileName, String courseId, String studentId, String fileId, long fileSize) {
        this.fileName = fileName;
        this.courseId = courseId;
        this.studentId = studentId;
        this.fileId = fileId;
        this.thumbnailId = null;
        this.fileSize = fileSize;
        this.uploadDate = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getFileId() { return fileId; }
    public void setFileId(String fileId) { this.fileId = fileId; }

    public LocalDateTime getUploadDate() { return uploadDate; }
    public void setUploadDate(LocalDateTime uploadDate) { this.uploadDate = uploadDate; }

    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }

    public String getThumbnailId() { return thumbnailId; }
    public void setThumbnailId(String thumbnailId) { this.thumbnailId = thumbnailId; }
}
