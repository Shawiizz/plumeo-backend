package fr.shawiizz.plumeo.controller;

import fr.shawiizz.plumeo.dto.LoginRequest;
import fr.shawiizz.plumeo.dto.LoginResponse;
import fr.shawiizz.plumeo.dto.RegisterRequest;
import fr.shawiizz.plumeo.dto.RegisterResponse;
import fr.shawiizz.plumeo.entity.User;
import fr.shawiizz.plumeo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API for user authentication management")
public class SecurityController {

    private final UserService userService;

    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Allows a new user to create an account with email, email and password"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RegisterResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data (email already used, password too short, etc.)",
                    content = @Content
            )
    })
    public RegisterResponse register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.registerUser(
                request.username().trim(),
                request.email().trim(),
                request.password()
        );

        return new RegisterResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }

    @PostMapping("/login")
    @Operation(
            summary = "Authenticate user",
            description = "Authenticate user with email and password and return JWT token"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User authenticated successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content
            )
    })
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        String jwtToken = userService.loginUser(request);

        return new LoginResponse(jwtToken);
    }
}
