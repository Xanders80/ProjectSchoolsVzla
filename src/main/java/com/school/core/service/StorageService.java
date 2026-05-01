package com.school.core.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class StorageService {

    @Value("${app.upload.dir:/tmp/school-uploads}")
    private String uploadDir;

    public String store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("No se puede guardar un archivo vacío.");
            }

            Path root = Paths.get(uploadDir);
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }

			String originalFilename = file.getOriginalFilename();
			String safeFilename = (originalFilename != null)
					? originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_")
					: "unknown";
			String filename = UUID.randomUUID().toString() + "_" + safeFilename;
            Path destinationFile = root.resolve(Paths.get(filename))
                    .normalize().toAbsolutePath();

            if (!destinationFile.getParent().equals(root.toAbsolutePath())) {
                throw new IllegalArgumentException("No se puede guardar fuera del directorio actual.");
            }

            try (var inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
            }

            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar archivo", e);
        }
    }
}
