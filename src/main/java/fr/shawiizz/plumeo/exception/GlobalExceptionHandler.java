package fr.shawiizz.plumeo.exception;

import fr.shawiizz.plumeo.constant.ErrorCodes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        Map<String, String> fieldErrors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        errorResponse.put("message", ErrorCodes.VALIDATION_FAILED);
        errorResponse.put("errors", fieldErrors);

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        log.error("Runtime exception caught: ", ex);
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", ex.getMessage());

        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleAuthenticationException(AuthenticationException ex) {
        log.warn("Authentication exception: {}", ex.getMessage());
        Map<String, String> errorResponse = new HashMap<>();
        
        // Map Spring Security exceptions to custom error codes
        if (ex.getMessage().contains("Bad credentials") || ex.getMessage().contains("Les identifications sont erronées")) {
            errorResponse.put("message", ErrorCodes.AUTH_BAD_CREDENTIALS);
        } else if (ex.getMessage().contains("User not found")) {
            errorResponse.put("message", ErrorCodes.AUTH_USER_NOT_FOUND);
        } else {
            errorResponse.put("message", ErrorCodes.AUTH_FAILED);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", ErrorCodes.ACCESS_DENIED);

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) throws Exception {
        // Don't handle Spring Security exceptions here - let them bubble up
        if (ex instanceof AuthenticationException || ex instanceof AccessDeniedException) {
            throw ex;
        }

        log.error("Generic exception caught: ", ex);
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", ErrorCodes.INTERNAL_SERVER_ERROR);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}