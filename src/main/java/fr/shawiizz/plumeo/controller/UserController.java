package fr.shawiizz.plumeo.controller;

import fr.shawiizz.plumeo.annotation.Authenticated;
import fr.shawiizz.plumeo.entity.User;
import fr.shawiizz.plumeo.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "API for user management")
public class UserController {
    private final AuthenticationService authenticationService;

    @GetMapping("/info")
    @Authenticated
    @Operation(
        summary = "Get user info",
        description = "Get basic information about the currently authenticated user"
    )
    public Map<String, Object> getUserInfo() {
        User user = authenticationService.getCurrentUserOrThrow();
        
        return Map.of(
            "id", user.getId(),
            "username", user.getUsername(),
            "email", user.getEmail(),
            "createdAt", user.getCreatedAt(),
            "message", "You are successfully authenticated!"
        );
    }
}