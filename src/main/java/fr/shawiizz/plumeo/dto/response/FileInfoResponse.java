package fr.shawiizz.plumeo.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FileInfoResponse(
        String id,
        @JsonProperty("file_name") 
        String fileName,
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
        @JsonProperty("author_id") 
        String authorId,
        @JsonProperty("created_at") 
        Long createdAt,
        @JsonProperty("updated_at") 
        Long updatedAt
) {
}