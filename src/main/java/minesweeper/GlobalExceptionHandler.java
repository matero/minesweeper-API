/*
 The MIT License (MIT)

 Copyright (c) 2020 Juan José GIL - matero@gmail.com

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */
package minesweeper;

import minesweeper.games.AlreadyFinished;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Map;

@ControllerAdvice
class GlobalExceptionHandler extends ResponseEntityExceptionHandler
{
  private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  private static final int JSON_ERROR_DESCRIPTION_START = "JSON parse error:".length();
  private static final String TYPE_NAME_START = " of type `";

  @ExceptionHandler(ConstraintViolationException.class) @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY) @ResponseBody @NonNull
  Map<String, Object> onConstraintValidationException(final ConstraintViolationException e)
  {
    final var constraintViolations = e.getConstraintViolations();
    final var errors = new ArrayList<String>(constraintViolations.size());
    for (final var constraintViolation : constraintViolations) {
      errors.add(constraintViolation.getPropertyPath() + " " + constraintViolation.getMessage());
    }
    return Map.of("errors", errors);
  }

  @Override @NonNull
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      @NonNull final HttpMessageNotReadableException ex,
      @NonNull final HttpHeaders headers,
      @NonNull final HttpStatus status,
      @NonNull final WebRequest request)
  {
    return new ResponseEntity<>(Map.of("errors", sanitizeJsonErrorMessage(ex.getMessage())), status);
  }

  /**
   * Avoids to show internal info to API clients forcing the error.
   *
   * @param message the original error message
   * @return the message without internal type structure.
   */
  private String sanitizeJsonErrorMessage(final String message)
  {
    final var typeNameStart = message.indexOf(TYPE_NAME_START);
    int typeNameEnd = -1;

    if (typeNameStart != -1) {
      typeNameEnd = message.indexOf('`', typeNameStart + TYPE_NAME_START.length());
    }

    final int descriptionEnd = message.indexOf(':', JSON_ERROR_DESCRIPTION_START);

    if (typeNameEnd == -1 && descriptionEnd == -1) {
      return message;
    } else {
      return message.substring(0, typeNameStart) + message.substring(typeNameEnd + 1, descriptionEnd);
    }
  }

  // error handle for @Valid
  @Override @NonNull protected ResponseEntity<Object> handleMethodArgumentNotValid(
      final MethodArgumentNotValidException ex,
      @NonNull final HttpHeaders headers,
      @NonNull final HttpStatus status,
      @NonNull final WebRequest request)
  {
    final var globalErrors = ex.getBindingResult().getGlobalErrors();
    final var fieldErrors = ex.getBindingResult().getFieldErrors();
    final var errors = new ArrayList<String>(fieldErrors.size() + globalErrors.size());

    for (final var fieldError : fieldErrors) {
      errors.add(fieldError.getField() + " " + fieldError.getDefaultMessage());
    }
    for (final ObjectError objectError : globalErrors) {
      errors.add(objectError.getDefaultMessage());
    }

    // TODO: errors could have more structure. I.e: separate the field from the message in an object.
    final var body = Map.of("errors", errors);

    return new ResponseEntity<>(body, headers, status);
  }

  @ExceptionHandler(NotFound.class) @ResponseStatus(HttpStatus.NOT_FOUND) @ResponseBody @NonNull
  Map<String, Object> onNotFound(final NotFound e)
  {
    return Map.of("errors", e.getMessage());
  }

  @ExceptionHandler(AlreadyFinished.class) @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY) @ResponseBody @NonNull
  Map<String, Object> onAlreadyFinished(final AlreadyFinished e)
  {
    return Map.of("errors", e.getMessage());
  }

  @ExceptionHandler(IllegalArgumentException.class) @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY) @ResponseBody @NonNull
  Map<String, Object> onIllegalArgument(final IllegalArgumentException e)
  {
    return Map.of("errors", e.getMessage());
  }

  @ExceptionHandler(IllegalStateException.class) @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) @ResponseBody @NonNull
  Map<String, Object> onIllegalState(final IllegalStateException e)
  {
    return Map.of("errors", e.getMessage());
  }
}

