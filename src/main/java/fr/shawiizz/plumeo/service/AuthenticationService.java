package fr.shawiizz.plumeo.service;

import fr.shawiizz.plumeo.entity.User;
import fr.shawiizz.plumeo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service to get information about the currently authenticated user.
 */
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;

    /**
     * Get the email of the currently authenticated user.
     *
     * @return Optional containing the email if user is authenticated, empty otherwise
     */
    public Optional<String> getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal())) {

            if (authentication.getPrincipal() instanceof UserDetails userDetails) {
                return Optional.of(userDetails.getUsername());
            } else if (authentication.getPrincipal() instanceof String username) {
                return Optional.of(username);
            }
        }

        return Optional.empty();
    }

    /**
     * Get the currently authenticated user entity.
     *
     * @return Optional containing the User entity if authenticated, empty otherwise
     */
    public Optional<User> getCurrentUser() {
        return getCurrentUsername()
                .flatMap(userRepository::findByEmail);
    }

    /**
     * Check if there is a currently authenticated user.
     *
     * @return true if user is authenticated, false otherwise
     */
    public boolean isAuthenticated() {
        return getCurrentUsername().isPresent();
    }

    /**
     * Get the currently authenticated user entity or throw exception.
     *
     * @return User entity
     * @throws RuntimeException if no user is authenticated
     */
    public User getCurrentUserOrThrow() {
        return getCurrentUser()
                .orElseThrow(() -> new RuntimeException("No authenticated user found"));
    }
}