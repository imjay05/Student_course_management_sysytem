package com.project.StudentCourse.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Document(collection = "students")
public class Student {

    @Id
    private String id;
    private String name;
    private String email;
    private String enrollmentId;
    private List<String> courses;

    public Student() {
        this.courses = new ArrayList<>();
    }

    public Student(String name, String email, String enrollmentId) {
        this.name = name;
        this.email = email;
        this.enrollmentId = enrollmentId;
        this.courses = new ArrayList<>();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(String enrollmentId) { this.enrollmentId = enrollmentId; }

    public List<String> getCourses() { return courses; }
    public void setCourses(List<String> courses) { this.courses = courses; }
}
