package fr.shawiizz.plumeo.constant;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public final class ErrorCodes {
    
    // Authentication errors
    public static final String AUTH_BAD_CREDENTIALS = "error.auth.badcredentials";
    public static final String AUTH_USER_NOT_FOUND = "error.auth.usernotfound";
    public static final String AUTH_FAILED = "error.auth.failed";
    public static final String AUTH_TOKEN_EXPIRED = "error.auth.tokenexpired";
    public static final String AUTH_TOKEN_INVALID = "error.auth.tokeninvalid";
    
    // Validation errors
    public static final String VALIDATION_FAILED = "error.validation.failed";
    public static final String VALIDATION_EMAIL_REQUIRED = "error.validation.email.required";
    public static final String VALIDATION_PASSWORD_REQUIRED = "error.validation.password.required";
    
    // User errors
    public static final String USER_ALREADY_EXISTS = "error.user.alreadyexists";
    public static final String USER_NOT_AUTHENTICATED = "error.user.notauthenticated";
    
    // General errors
    public static final String INTERNAL_SERVER_ERROR = "error.internal.server";
    public static final String ACCESS_DENIED = "error.access.denied";
}