package app.exception;

import app.model.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // validation errors from @Valid on @RequestBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest req
    ) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                fieldErrors.toString(),
                req
        );
    }

    // Validation errors from @Valid on path params / request params
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest req
    ) {
        Map<String, String> violations = new HashMap<>();
        ex.getConstraintViolations().forEach(v ->
                violations.put(v.getPropertyPath().toString(), v.getMessage())
        );

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Validation failed",
                violations.toString(),
                req
        );
    }

    // Specific invalid credentials case (username/password)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDTO> handleBadCredentials(
            BadCredentialsException ex,
            HttpServletRequest req
    ) {
        return buildResponse(
                HttpStatus.UNAUTHORIZED,
                "Invalid credentials",
                "Username or password is incorrect",
                req
        );
    }

    // Authentication / JWT errors (other auth errors)
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDTO> handleAuthentication(
            AuthenticationException ex,
            HttpServletRequest req
    ) {
        return buildResponse(
                HttpStatus.UNAUTHORIZED,
                "Authentication failed",
                ex.getMessage(),
                req
        );
    }

    // Access denied (valid token but not enough permissions)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest req
    ) {
        return buildResponse(
                HttpStatus.FORBIDDEN,
                "Access denied",
                ex.getMessage(),
                req
        );
    }

    // Resource not found – uses your new custom exception
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest req
    ) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                "Not Found",
                ex.getMessage(),
                req
        );
    }

    // Business / common runtime errors
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDTO> handleRuntime(
            RuntimeException ex,
            HttpServletRequest req
    ) {
        if (ex.getMessage() != null && ex.getMessage().contains("Email already in use")) {
            return buildResponse(
                    HttpStatus.CONFLICT,
                    "Conflict",
                    ex.getMessage(),
                    req
            );
        }

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Bad request",
                ex.getMessage(),
                req
        );
    }

    // Fallback for everything else
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGeneric(
            Exception ex,
            HttpServletRequest req
    ) {
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal server error",
                ex.getMessage(),
                req
        );
    }

    private ResponseEntity<ErrorResponseDTO> buildResponse(
            HttpStatus status,
            String error,
            String message,
            HttpServletRequest req
    ) {
        ErrorResponseDTO body = new ErrorResponseDTO(
                Instant.now().toString(),
                status.value(),
                error,
                message,
                req.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }
}
