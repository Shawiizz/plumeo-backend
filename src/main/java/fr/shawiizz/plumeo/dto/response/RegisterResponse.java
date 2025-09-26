package fr.shawiizz.plumeo.dto.response;

import java.time.Instant;

public record RegisterResponse(
        Long id,
        String username,
        String email,
        Instant createdAt
) {
}