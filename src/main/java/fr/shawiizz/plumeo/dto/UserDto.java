package fr.shawiizz.plumeo.dto;

import fr.shawiizz.plumeo.entity.User;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

/**
 * DTO for {@link User}
 */
public record UserDto(Long id, String username, String email, String password, List<UserDto> followers,
                      List<UserDto> following, List<UserDto> blockedUsers, String passwordChangeToken,
                      Instant passwordChangeTokenCreatedAt, String accountVerifyToken, String language,
                      Instant lastLogin, Instant createdAt) implements Serializable {
}

