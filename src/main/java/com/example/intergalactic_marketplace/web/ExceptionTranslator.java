package com.example.intergalactic_marketplace.web;

import org.springframework.boot.actuate.autoconfigure.metrics.MetricsProperties.Web;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.util.Map;
import java.net.URI;

import com.example.intergalactic_marketplace.service.exception.CustomerHasNoRulesToDeleteProductException;
import com.example.intergalactic_marketplace.service.exception.CustomerNotFoundException;
import com.example.intergalactic_marketplace.service.exception.ProductAlreadyExistsException;
import com.example.intergalactic_marketplace.service.exception.ProductNotFoundException;
import com.example.intergalactic_marketplace.service.exception.ProductsNotFoundException;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class ExceptionTranslator extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    ProblemDetail handleProductNotFoundException(ProductNotFoundException ex, WebRequest request) {
        log.info("Product not found exception raised");
        String path = request.getDescription(false).replace("uri=", "");
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setProperties(Map.of(
            "error", URI.create("product-not-found"),
            "path", path,
            "message", ex.getMessage()
        ));
        return problemDetail;
    }

    @ExceptionHandler(ProductsNotFoundException.class)
    ProblemDetail handleProductsNotFoundException(ProductsNotFoundException ex, WebRequest request) {
        log.info("Products not found exception raised");
        String path = request.getDescription(false).replace("uri=", "");
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setProperties(Map.of(
            "error", URI.create("products-not-found"),
            "path", path,
            "message", ex.getMessage()
        ));
        return problemDetail;
    }

    @ExceptionHandler(ProductAlreadyExistsException.class)
    ProblemDetail handleProductAlreadyExistsException(ProductAlreadyExistsException ex, WebRequest request) {
        log.info("Product already exists exception raised");
        String path = request.getDescription(false).replace("uri=", "");
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problemDetail.setProperties(Map.of(
            "error", URI.create("product-already-exists"),
            "path", path,
            "message", ex.getMessage()
        ));
        return problemDetail;
    }

    @ExceptionHandler(CustomerHasNoRulesToDeleteProductException.class)
    ProblemDetail handleCustomerHasNoRulesToDeleteProduct(CustomerHasNoRulesToDeleteProductException ex, WebRequest request) {
        log.info("Customer has no rules to delete product exception raised");
        String path = request.getDescription(false).replace("uri=", "");
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        problemDetail.setProperties(Map.of(
            "error", URI.create("customer-has-no-rules"),
            "path", path,
            "message", ex.getMessage()
        ));
        return problemDetail;
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    ProblemDetail handleCustomerNotFoundException(CustomerNotFoundException ex, WebRequest request) {
        log.info("Customer not found exception raised");
        String path = request.getDescription(false).replace("uri=", "");
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setProperties(Map.of(
            "error", URI.create("customer-not-found"),
            "path", path,
            "message", ex.getMessage()
        ));
        return problemDetail;
    }
}
