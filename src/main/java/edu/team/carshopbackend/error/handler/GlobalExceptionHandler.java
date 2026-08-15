package edu.team.carshopbackend.error.handler;

import edu.team.carshopbackend.error.ErrorResponse;
import edu.team.carshopbackend.error.exception.*;
import jakarta.persistence.EntityExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse NotFoundEntityException(NotFoundException exception) {
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), exception.getMessage());
    }

    @ExceptionHandler(value = EntityExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse userAlreadyExists(EntityExistsException exception) {
        return new ErrorResponse(HttpStatus.CONFLICT.value(), exception.getMessage());
    }

    @ExceptionHandler(value = RatingRangeException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ErrorResponse ratingRangeException(RatingRangeException exception) {
        return new ErrorResponse(HttpStatus.NOT_ACCEPTABLE.value(), exception.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalState(Exception e) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(),e.getMessage());
    }

    @ExceptionHandler(ChangePasswordException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse changePasswordException(ChangePasswordException e) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(),e.getMessage());
    }

    @ExceptionHandler(RefreshTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse refreshTokenException(RefreshTokenException e) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(),e.getMessage());
    }

    @ExceptionHandler(PhotoUploadException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse refreshTokenException(PhotoUploadException e) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(),e.getMessage());
    }

    @ExceptionHandler(EmailSendingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse emailSendingException(EmailSendingException e) {
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse validationException(MethodArgumentNotValidException e) {

        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> Objects.requireNonNullElse(
                        error.getDefaultMessage(),
                        "Validation failed"
                ))
                .findFirst()
                .orElse("Validation failed");

        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse serverException(Exception e) {
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage());
    }

}
