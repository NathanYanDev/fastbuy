package dev.nathanyan.fastbuy.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<Void> handleBadCredentials(BadCredentialsException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
  }

  @ExceptionHandler(InvalidTokenException.class)
  public ResponseEntity<Void> handleInvalidToken(InvalidTokenException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
  }

  @ExceptionHandler(UsernameNotFoundException.class)
  public ResponseEntity<Void> handleUsernameNotFound(UsernameNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
  }

  @ExceptionHandler(UserAlreadyExistsException.class)
  public ResponseEntity<Void> handleUserAlreadyExists(UserAlreadyExistsException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).build();
  }

  @ExceptionHandler(MissingRequestHeaderException.class)
  public ResponseEntity<Void> handleMissingRequestHeader(MissingRequestHeaderException ex) {
    if (ex.getHeaderName().equals("Authorization")) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
  }
}
