package com.grabfood.menu.exception;

import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(MenuNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    Map<String,Object> notFound(RuntimeException e) { return body(404, e.getMessage()); }
    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    Map<String,Object> badRequest(Exception e) {
        String message = e instanceof MethodArgumentNotValidException v
                ? v.getBindingResult().getAllErrors().get(0).getDefaultMessage() : e.getMessage();
        return body(400, message);
    }
    private Map<String,Object> body(int status, String message) {
        return Map.of("timestamp", Instant.now().toString(), "status", status, "message", message);
    }
}
