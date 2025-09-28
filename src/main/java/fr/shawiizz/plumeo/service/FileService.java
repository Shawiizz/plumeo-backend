package fr.shawiizz.plumeo.service;

import fr.shawiizz.plumeo.dto.response.FileUploadResponse;
import fr.shawiizz.plumeo.entity.File;
import fr.shawiizz.plumeo.entity.User;
import fr.shawiizz.plumeo.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final FileRepository fileRepository;

    @Value("${file.upload.dir:/app/plumeo}")
    private String uploadDir;

    public FileUploadResponse uploadFile(MultipartFile file, User author, Boolean isPrivate) {
        try {
            // Validation du fichier
            validateFile(file);

            // Génération de l'ID unique
            String fileId = UUID.randomUUID().toString();
            
            // Extraction de l'extension originale
            String originalFileName = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFileName);
            
            // Détection du type MIME
            String mimeType = detectMimeType(file, originalFileName);
            
            // Création du répertoire utilisateur
            Path userDir = createUserDirectory(author.getId());
            
            // Sauvegarde physique du fichier (renommé avec l'ID)
            String physicalFileName = fileId + (fileExtension.isEmpty() ? "" : "." + fileExtension);
            Path filePath = userDir.resolve(physicalFileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Sauvegarde en base de données
            File fileEntity = new File();
            fileEntity.setId(fileId);
            fileEntity.setAuthor(author);
            fileEntity.setFileName(physicalFileName);
            fileEntity.setOriginalFileName(originalFileName);
            fileEntity.setFilePath(filePath.toString());
            fileEntity.setFileExtension(fileExtension);
            fileEntity.setMimeType(mimeType);
            fileEntity.setFileSize(file.getSize());
            fileEntity.setIsPrivate(isPrivate != null ? isPrivate : false);
            fileEntity.setCreatedAt(Instant.now());

            File savedFile = fileRepository.save(fileEntity);

            log.info("File uploaded successfully: {} by user {}", fileId, author.getId());

            return new FileUploadResponse(
                    savedFile.getId(),
                    savedFile.getOriginalFileName(),
                    savedFile.getFileExtension(),
                    savedFile.getMimeType(),
                    savedFile.getFileSize(),
                    savedFile.getIsPrivate(),
                    savedFile.getCreatedAt().toEpochMilli()
            );

        } catch (IOException e) {
            log.error("Error uploading file for user {}: {}", author.getId(), e.getMessage());
            throw new RuntimeException("Failed to upload file: " + e.getMessage());
        }
    }

    public Optional<File> getFileById(String fileId) {
        return fileRepository.findById(fileId);
    }

    public Optional<File> getFileByIdAndAuthor(String fileId, User author) {
        return fileRepository.findByIdAndAuthor(fileId, author);
    }

    public List<File> getUserFiles(User author) {
        return fileRepository.findByAuthor(author);
    }

    public List<File> getUserFiles(User author, Boolean isPrivate) {
        return fileRepository.findByAuthorAndIsPrivate(author, isPrivate);
    }

    public boolean deleteFile(String fileId, User author) {
        Optional<File> fileOpt = fileRepository.findByIdAndAuthor(fileId, author);
        if (fileOpt.isPresent()) {
            File file = fileOpt.get();
            try {
                // Suppression du fichier physique
                Path filePath = Paths.get(file.getFilePath());
                Files.deleteIfExists(filePath);
                
                // Suppression de la base de données
                fileRepository.delete(file);
                
                log.info("File deleted successfully: {} by user {}", fileId, author.getId());
                return true;
            } catch (IOException e) {
                log.error("Error deleting file {}: {}", fileId, e.getMessage());
                throw new RuntimeException("Failed to delete file: " + e.getMessage());
            }
        }
        return false;
    }



    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }
        
        if (file.getOriginalFilename() == null || file.getOriginalFilename().trim().isEmpty()) {
            throw new RuntimeException("File name is required");
        }
        
        // Vérification de la taille (10MB max par défaut)
        long maxSize = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > maxSize) {
            throw new RuntimeException("File size exceeds maximum allowed size");
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == fileName.length() - 1) {
            return "";
        }
        
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }

    private String detectMimeType(MultipartFile file, String originalFileName) {
        // Priorité au Content-Type fourni par le client
        String clientMimeType = file.getContentType();
        if (clientMimeType != null && !clientMimeType.equals("application/octet-stream")) {
            return clientMimeType;
        }
        
        // Détection basée sur l'extension avec une map simple
        if (originalFileName != null) {
            String extension = getFileExtension(originalFileName);
            return getMimeTypeFromExtension(extension);
        }
        
        return "application/octet-stream";
    }

    private String getMimeTypeFromExtension(String extension) {
        Map<String, String> mimeTypes = new HashMap<>();
        
        // Images
        mimeTypes.put("jpg", "image/jpeg");
        mimeTypes.put("jpeg", "image/jpeg");
        mimeTypes.put("png", "image/png");
        mimeTypes.put("gif", "image/gif");
        mimeTypes.put("bmp", "image/bmp");
        mimeTypes.put("webp", "image/webp");
        mimeTypes.put("svg", "image/svg+xml");
        
        // Documents
        mimeTypes.put("pdf", "application/pdf");
        mimeTypes.put("doc", "application/msword");
        mimeTypes.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        mimeTypes.put("xls", "application/vnd.ms-excel");
        mimeTypes.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        mimeTypes.put("ppt", "application/vnd.ms-powerpoint");
        mimeTypes.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        
        // Texte
        mimeTypes.put("txt", "text/plain");
        mimeTypes.put("html", "text/html");
        mimeTypes.put("css", "text/css");
        mimeTypes.put("js", "application/javascript");
        mimeTypes.put("json", "application/json");
        mimeTypes.put("xml", "application/xml");
        
        // Audio
        mimeTypes.put("mp3", "audio/mpeg");
        mimeTypes.put("wav", "audio/wav");
        mimeTypes.put("ogg", "audio/ogg");
        
        // Vidéo
        mimeTypes.put("mp4", "video/mp4");
        mimeTypes.put("avi", "video/x-msvideo");
        mimeTypes.put("mov", "video/quicktime");
        
        // Archives
        mimeTypes.put("zip", "application/zip");
        mimeTypes.put("rar", "application/x-rar-compressed");
        mimeTypes.put("7z", "application/x-7z-compressed");
        
        return mimeTypes.getOrDefault(extension.toLowerCase(), "application/octet-stream");
    }

    private Path createUserDirectory(String userId) throws IOException {
        Path userDir = Paths.get(uploadDir, "users", userId);
        
        if (!Files.exists(userDir)) {
            Files.createDirectories(userDir);
            log.info("Created user directory: {}", userDir);
        }
        
        return userDir;
    }
}