package com.gokul.help_desk_api.service;

import com.gokul.help_desk_api.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;


    // =========================================================
    // Store File
    // =========================================================

    public String storeFile(
            MultipartFile file,
            String ticketNumber) {

        try {

            Path ticketDirectory = Paths.get(uploadDir)
                    .toAbsolutePath()
                    .normalize()
                    .resolve(ticketNumber);

            Files.createDirectories(ticketDirectory);

            String originalFileName =
                    file.getOriginalFilename();

            if (originalFileName == null ||
                    originalFileName.isBlank()) {

                throw new IllegalArgumentException(
                        "Invalid file name"
                );
            }

            String fileName = Paths.get(originalFileName)
                    .getFileName()
                    .toString();

            Path targetLocation = ticketDirectory
                    .resolve(fileName)
                    .normalize();

            // Prevent path traversal
            if (!targetLocation.startsWith(ticketDirectory)) {

                throw new IllegalArgumentException(
                        "Invalid file path"
                );
            }

            Files.copy(
                    file.getInputStream(),
                    targetLocation,
                    StandardCopyOption.REPLACE_EXISTING
            );

            return targetLocation.toString();

        } catch (IOException exception) {

            throw new RuntimeException(
                    "Could not store file: "
                            + exception.getMessage(),
                    exception
            );
        }
    }


    // =========================================================
    // Load File
    // =========================================================

    public byte[] loadFile(String filePath) {

        try {

            Path path = Paths.get(filePath)
                    .toAbsolutePath()
                    .normalize();

            if (!Files.exists(path)) {

                throw new ResourceNotFoundException(
                        "Attachment file not found"
                );
            }

            return Files.readAllBytes(path);

        } catch (ResourceNotFoundException exception) {

            throw exception;

        } catch (IOException exception) {

            throw new RuntimeException(
                    "Could not read file: "
                            + exception.getMessage(),
                    exception
            );
        }
    }


    // =========================================================
    // Delete File
    // =========================================================

    public void deleteFile(String filePath) {

        try {

            Path path = Paths.get(filePath)
                    .toAbsolutePath()
                    .normalize();

            Files.deleteIfExists(path);

        } catch (IOException exception) {

            throw new RuntimeException(
                    "Could not delete file: "
                            + exception.getMessage(),
                    exception
            );
        }
    }
}