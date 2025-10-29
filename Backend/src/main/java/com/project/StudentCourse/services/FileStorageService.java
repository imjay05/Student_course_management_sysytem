package com.project.StudentCourse.services;

import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class FileStorageService {
    @Autowired
    private GridFsTemplate gridFsTemplate;

    public String storeFile(MultipartFile file) throws IOException {
        ObjectId fileId = gridFsTemplate.store(
                file.getInputStream(),
                file.getOriginalFilename(),
                file.getContentType()
        );
        return fileId.toString();
    }

    public String storeThumbnail(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            BufferedImage image = pdfRenderer.renderImageWithDPI(0, 150);

            // Resize to thumbnail size
            int width = 300;
            int height = (int) (image.getHeight() * (300.0 / image.getWidth()));
            BufferedImage thumbnail = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            thumbnail.createGraphics().drawImage(
                    image.getScaledInstance(width, height, BufferedImage.SCALE_SMOOTH),
                    0, 0, null
            );

            // Convert to PNG bytes
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(thumbnail, "png", baos);
            byte[] thumbnailBytes = baos.toByteArray();

            // Store in GridFS
            ObjectId thumbnailId = gridFsTemplate.store(
                    new ByteArrayInputStream(thumbnailBytes),
                    file.getOriginalFilename() + "_thumbnail.png",
                    "image/png"
            );
            return thumbnailId.toString();
        }
    }

    public GridFsResource getFile(String fileId) {
        GridFSFile gridFSFile = gridFsTemplate.findOne(
                new Query(Criteria.where("_id").is(fileId))
        );
        if (gridFSFile == null) {
            return null;
        }
        return gridFsTemplate.getResource(gridFSFile);
    }

    public void deleteFile(String fileId) {
        gridFsTemplate.delete(new Query(Criteria.where("_id").is(fileId)));
    }
}
