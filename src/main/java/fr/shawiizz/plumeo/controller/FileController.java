package fr.shawiizz.plumeo.controller;

import fr.shawiizz.plumeo.annotation.Authenticated;
import fr.shawiizz.plumeo.dto.response.FileUploadResponse;
import fr.shawiizz.plumeo.entity.File;
import fr.shawiizz.plumeo.entity.User;
import fr.shawiizz.plumeo.service.AuthenticationService;
import fr.shawiizz.plumeo.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "File Management", description = "API for file upload and management")
@SecurityRequirement(name = "bearerAuth")
public class FileController {

    private final FileService fileService;
    private final AuthenticationService authenticationService;

    @PostMapping("/upload")
    @Authenticated
    @Operation(
            summary = "Upload a file",
            description = "Upload a single file for the authenticated user. The file will be stored securely with a unique identifier."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File uploaded successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = FileUploadResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid file or request parameters"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
            @ApiResponse(responseCode = "413", description = "File too large"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<FileUploadResponse> uploadFile(
            @Parameter(description = "File to upload", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "Whether the file should be private (default: false)")
            @RequestParam(value = "isPrivate", required = false, defaultValue = "false") Boolean isPrivate) {
        
        User user = authenticationService.getCurrentUserOrThrow();
        
        FileUploadResponse response = fileService.uploadFile(file, user, isPrivate);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{fileId}")
    @Authenticated
    @Operation(
            summary = "Get file content",
            description = "Get the binary content of a file owned by the authenticated user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File content retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "File not found or access denied"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    public ResponseEntity<Resource> getFileContent(
            @Parameter(description = "File ID", required = true)
            @PathVariable String fileId) throws IOException {
        
        User user = authenticationService.getCurrentUserOrThrow();
        
        File file = fileService.getFileByIdAndAuthor(fileId, user)
                .orElseThrow(() -> new RuntimeException("File not found or access denied"));
        
        Resource resource = new FileSystemResource(file.getFilePath());
        
        if (!resource.exists()) {
            throw new RuntimeException("File not found on disk");
        }

        String contentType = Files.probeContentType(Paths.get(file.getFilePath()));
        if (contentType == null) {
            contentType = file.getMimeType();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    @GetMapping("/{fileId}/download")
    @Authenticated
    @Operation(
            summary = "Download a file",
            description = "Download a file owned by the authenticated user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File downloaded successfully"),
            @ApiResponse(responseCode = "404", description = "File not found or access denied"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    public ResponseEntity<Resource> downloadFile(
            @Parameter(description = "File ID", required = true)
            @PathVariable String fileId) throws IOException {
        
        User user = authenticationService.getCurrentUserOrThrow();
        
        File file = fileService.getFileByIdAndAuthor(fileId, user)
                .orElseThrow(() -> new RuntimeException("File not found or access denied"));
        
        Resource resource = new FileSystemResource(file.getFilePath());
        
        if (!resource.exists()) {
            throw new RuntimeException("File not found on disk");
        }

        String contentType = Files.probeContentType(Paths.get(file.getFilePath()));
        if (contentType == null) {
            contentType = file.getMimeType();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"" + file.getOriginalFileName() + "\"")
                .body(resource);
    }

    @DeleteMapping("/{fileId}")
    @Authenticated
    @Operation(
            summary = "Delete a file",
            description = "Delete a file owned by the authenticated user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File deleted successfully"),
            @ApiResponse(responseCode = "404", description = "File not found or access denied"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
    })
    public ResponseEntity<String> deleteFile(
            @Parameter(description = "File ID", required = true)
            @PathVariable String fileId) {
        
        User user = authenticationService.getCurrentUserOrThrow();
        
        boolean deleted = fileService.deleteFile(fileId, user);
        
        if (deleted) {
            return ResponseEntity.ok("File deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}