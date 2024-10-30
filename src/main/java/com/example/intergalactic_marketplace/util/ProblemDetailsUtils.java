package com.example.intergalactic_marketplace.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import com.example.intergalactic_marketplace.web.exception.ParamsViolationDetails;
import java.util.List;
import java.util.Map;
import java.net.URI;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ProblemDetailsUtils {
    public ProblemDetail getValidationErrorsProblemDetail(List<ParamsViolationDetails> validationResponse, String path){
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problemDetail.setProperties(Map.of(
            "error", URI.create("validation-error"),
            "path", path,
            "invalidParams", validationResponse
        ));
        return problemDetail;
    }
}
