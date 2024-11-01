package com.example.intergalactic_marketplace.web;

import com.example.intergalactic_marketplace.service.exception.CustomerHasNoRulesToDeleteProductException;
import com.example.intergalactic_marketplace.service.exception.CustomerHasNoRulesToUpdateProductException;
import com.example.intergalactic_marketplace.service.exception.CustomerNotFoundException;
import com.example.intergalactic_marketplace.service.exception.ProductAlreadyExistsException;
import com.example.intergalactic_marketplace.service.exception.ProductNotFoundException;
import com.example.intergalactic_marketplace.service.exception.ProductsNotFoundException;
import com.example.intergalactic_marketplace.util.ProblemDetailsUtils;
import com.example.intergalactic_marketplace.web.exception.ParamsViolationDetails;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class ExceptionTranslator extends ResponseEntityExceptionHandler {

  @ExceptionHandler(ProductNotFoundException.class)
  ResponseEntity<Map<String, Object>> handleProductNotFoundException(
      ProductNotFoundException ex, WebRequest request) {
    log.info("Product not found exception raised");
    return ProblemDetailsUtils.getErrorResponseEntity(
        HttpStatus.NOT_FOUND, "product-not-found", request, ex.getMessage());
  }

  @ExceptionHandler(ProductsNotFoundException.class)
  ResponseEntity<Map<String, Object>> handleProductsNotFoundException(
      ProductsNotFoundException ex, WebRequest request) {
    log.info("Products not found exception raised");
    return ProblemDetailsUtils.getErrorResponseEntity(
        HttpStatus.NOT_FOUND, "products-not-found", request, ex.getMessage());
  }

  @ExceptionHandler(ProductAlreadyExistsException.class)
  ResponseEntity<Map<String, Object>> handleProductAlreadyExistsException(
      ProductAlreadyExistsException ex, WebRequest request) {
    log.info("Product already exists exception raised");
    return ProblemDetailsUtils.getErrorResponseEntity(
        HttpStatus.CONFLICT, "product-already-exists", request, ex.getMessage());
  }

  @ExceptionHandler(CustomerHasNoRulesToDeleteProductException.class)
  ResponseEntity<Map<String, Object>> handleCustomerHasNoRulesToDeleteProduct(
      CustomerHasNoRulesToDeleteProductException ex, WebRequest request) {
    log.info("Customer has no rules to delete product exception raised");
    return ProblemDetailsUtils.getErrorResponseEntity(
        HttpStatus.FORBIDDEN, "customer-has-no-rules", request, ex.getMessage());
  }

  @ExceptionHandler(CustomerNotFoundException.class)
  ResponseEntity<Map<String, Object>> handleCustomerNotFoundException(
      CustomerNotFoundException ex, WebRequest request) {
    log.info("Customer not found exception raised");
    return ProblemDetailsUtils.getErrorResponseEntity(
        HttpStatus.NOT_FOUND, "customer-not-found", request, ex.getMessage());
  }

  @ExceptionHandler(CustomerHasNoRulesToUpdateProductException.class)
  ResponseEntity<Map<String, Object>> handleCustomerHasNoRulesToUpdateProductException(
      CustomerHasNoRulesToUpdateProductException ex, WebRequest request) {
    log.info("Customer has no rules to update product exception raised");
    return ProblemDetailsUtils.getErrorResponseEntity(
        HttpStatus.FORBIDDEN, "customer-has-no-rules", request, ex.getMessage());
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    log.info("Input params validation failed");
    List<FieldError> errors = ex.getBindingResult().getFieldErrors();
    List<ParamsViolationDetails> validationResponse =
        errors.stream()
            .map(
                err ->
                    ParamsViolationDetails.builder()
                        .reason(err.getDefaultMessage())
                        .fieldName(err.getField())
                        .build())
            .toList();
    String path = request.getDescription(false).replace("uri=", "");
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(ProblemDetailsUtils.getValidationErrorsBody(validationResponse, path));
  }
}
