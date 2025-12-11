package edu.famu.cop3060.resources.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class RestExceptionAdvice {

  private Map<String,Object> body(HttpServletRequest req, int status, String error, String message) {
    return Map.of(
        "timestamp", Instant.now().toString(),
        "status", status,
        "error", error,
        "message", message,
        "path", req.getRequestURI()
    );
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<?> notFound(NotFoundException ex, HttpServletRequest req) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(body(req, 404, "Not Found", ex.getMessage()));
  }

  @ExceptionHandler(InvalidReferenceException.class)
  public ResponseEntity<?> badRef(InvalidReferenceException ex, HttpServletRequest req) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(body(req, 400, "Invalid Reference", ex.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<?> beanValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
    var msg = ex.getBindingResult().getFieldErrors().stream()
        .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
        .findFirst().orElse("Validation failed");
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(body(req, 400, "Validation Error", msg));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> fallback(Exception ex, HttpServletRequest req) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(body(req, 500, "Internal Server Error", ex.getMessage()));
  }
}
