package fr.shawiizz.plumeo.aspect;

import fr.shawiizz.plumeo.annotation.Authenticated;
import fr.shawiizz.plumeo.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * Aspect to handle @Authenticated annotation.
 * Checks if the user is authenticated before allowing access to protected endpoints.
 */
@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AuthenticationAspect {
    private final AuthenticationService authenticationService;

    @Around("@annotation(authenticated) || @within(authenticated)")
    public Object checkAuthentication(ProceedingJoinPoint joinPoint, Authenticated authenticated) throws Throwable {
        if (!authenticationService.isAuthenticated()) {
            log.warn("Unauthorized access attempt to protected endpoint: {}", joinPoint.getSignature().getName());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        
        log.debug("Authenticated user accessing: {}", joinPoint.getSignature().getName());
        return joinPoint.proceed();
    }
}