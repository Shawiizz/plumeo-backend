package fr.shawiizz.plumeo.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FileUploadResponse(
        String id,
        @JsonProperty("original_file_name") 
        String originalFileName,
        @JsonProperty("file_extension") 
        String fileExtension,
        @JsonProperty("mime_type") 
        String mimeType,
        @JsonProperty("file_size") 
        Long fileSize,
        @JsonProperty("is_private") 
        Boolean isPrivate,
        @JsonProperty("created_at") 
        Long createdAt
) {
}