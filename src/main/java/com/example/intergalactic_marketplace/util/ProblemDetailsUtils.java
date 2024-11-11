package com.example.intergalactic_marketplace.util;

import com.example.intergalactic_marketplace.web.exception.ParamsViolationDetails;
import java.net.URI;
import java.util.List;
import java.util.Map;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

@UtilityClass
public class ProblemDetailsUtils {
  public Map<String, Object> getValidationErrorsBody(
      List<ParamsViolationDetails> validationResponse, String path) {
    Map<String, Object> response =
        Map.of(
            "status",
            HttpStatus.BAD_REQUEST,
            "error",
            URI.create("validation-error"),
            "path",
            path,
            "invalidParams",
            validationResponse);
    return response;
  }

  public ResponseEntity<Map<String, Object>> getErrorResponseEntity(
      HttpStatus status, String errorType, WebRequest request, String message) {
    String path = request.getDescription(false).replace("uri=", "");
    Map<String, Object> response =
        Map.of("status", status.value(), "error", errorType, "path", path, "message", message);
    return ResponseEntity.status(status).body(response);
  }
}
