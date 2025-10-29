package com.project.StudentCourse.controller;

import com.project.StudentCourse.models.StudyMaterial;
import com.project.StudentCourse.repo.StudyMaterialRepository;
import com.project.StudentCourse.services.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/materials")
@CrossOrigin(origins = "*")
public class StudyMaterialController {
    @Autowired
    private StudyMaterialRepository materialRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity<StudyMaterial> uploadMaterial(
            @RequestParam("file") MultipartFile file,
            @RequestParam("studentId") String studentId,
            @RequestParam("courseId") String courseId) throws IOException {

        String fileId = fileStorageService.storeFile(file);
        String thumbnailId = fileStorageService.storeThumbnail(file);

        StudyMaterial material = new StudyMaterial(
                file.getOriginalFilename(),
                courseId,
                studentId,
                fileId,
                file.getSize()
        );
        material.setThumbnailId(thumbnailId);

        return ResponseEntity.ok(materialRepository.save(material));
    }

    @GetMapping("/student/{studentId}/course/{courseId}")
    public List<StudyMaterial> getMaterialsByStudentAndCourse(
            @PathVariable String studentId,
            @PathVariable String courseId) {
        return materialRepository.findByStudentIdAndCourseId(studentId, courseId);
    }

    @GetMapping("/student/{studentId}")
    public List<StudyMaterial> getMaterialsByStudent(@PathVariable String studentId) {
        return materialRepository.findByStudentId(studentId);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<InputStreamResource> downloadMaterial(@PathVariable String id) throws IOException {
        StudyMaterial material = materialRepository.findById(id).orElse(null);

        if (material == null) {
            return ResponseEntity.notFound().build();
        }

        GridFsResource resource = fileStorageService.getFile(material.getFileId());

        if (resource == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + material.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(material.getFileSize())
                .body(new InputStreamResource(resource.getInputStream()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMaterial(@PathVariable String id) {
        return materialRepository.findById(id)
                .map(material -> {
                    fileStorageService.deleteFile(material.getFileId());
                    if (material.getThumbnailId() != null) {
                        fileStorageService.deleteFile(material.getThumbnailId());
                    }
                    materialRepository.delete(material);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/thumbnail/{id}")
    public ResponseEntity<InputStreamResource> getThumbnail(@PathVariable String id) throws IOException {
        StudyMaterial material = materialRepository.findById(id).orElse(null);

        if (material == null || material.getThumbnailId() == null) {
            return ResponseEntity.notFound().build();
        }

        GridFsResource resource = fileStorageService.getFile(material.getThumbnailId());

        if (resource == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(new InputStreamResource(resource.getInputStream()));
    }
}
