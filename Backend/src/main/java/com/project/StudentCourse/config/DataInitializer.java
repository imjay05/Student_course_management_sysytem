package com.project.StudentCourse.config;

import com.project.StudentCourse.models.Course;
import com.project.StudentCourse.repo.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CourseRepository courseRepository;

    @Override
    public void run(String... args) {
        // Initialize sample courses if database is empty
        if (courseRepository.count() == 0) {
            courseRepository.save(new Course("Data Structures", "CS201", "Learn fundamental data structures and algorithms", 4));
            courseRepository.save(new Course("Database Management", "CS301", "Introduction to database design and SQL", 3));
            courseRepository.save(new Course("Web Development", "CS401", "Full-stack web development with modern frameworks", 4));
            courseRepository.save(new Course("Machine Learning", "CS501", "Introduction to ML algorithms and applications", 4));
            courseRepository.save(new Course("Operating Systems", "CS302", "OS concepts, processes, and memory management", 3));
            courseRepository.save(new Course("Computer Networks", "CS303", "Network protocols, architecture, and security", 3));

            System.out.println("Sample courses initialized!");
        }
    }
}
