package hr.smit.gls.web;

import hr.smit.gls.exception.GlsApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

/**
 * Turns failures into a response a caller (Pantheon connector or the manual test
 * controller) can actually act on, instead of Spring Boot's generic 500 page.
 */
@Slf4j
@RestControllerAdvice
public class GlsExceptionHandler {

    @ExceptionHandler(GlsApiException.class)
    public ResponseEntity<Map<String, String>> handleGlsApiException(GlsApiException e) {
        log.error("MyGLS API poziv nije uspio", e);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(Map.of("error", "Poziv prema MyGLS servisu nije uspio.", "detail", e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleNotConfigured(IllegalStateException e) {
        log.error("GLS middleware nije ispravno konfiguriran", e);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException e) {
        List<String> details = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .toList();
        return ResponseEntity.badRequest().body(Map.of("error", "Neispravan zahtjev.", "details", details));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleUnexpected(Exception e) {
        log.error("Neočekivana greška u GLS middleware-u", e);
        return ResponseEntity.internalServerError().body(Map.of("error", "Neočekivana greška."));
    }
}
