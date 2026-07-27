package com.sentinel.gateway.exception;

import com.sentinel.common.api.ApiErrorResponse;
import java.util.Map;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Throwable error = getError(request);
        HttpStatus status = determineHttpStatus(error);

        String message = error != null && error.getMessage() != null && !error.getMessage().isBlank()
                ? error.getMessage()
                : "An unexpected error occurred at the gateway edge";

        ApiErrorResponse response = ApiErrorResponse.of(
                status.value(),
                status.getReasonPhrase(),
                message,
                request.path()
        );

        return Map.of(
                "success", response.success(),
                "timestamp", response.timestamp().toString(),
                "status", response.status(),
                "error", response.error(),
                "message", response.message(),
                "path", response.path()
        );
    }

    private HttpStatus determineHttpStatus(Throwable error) {
        if (error == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        String errorName = error.getClass().getSimpleName();
        if (errorName.contains("NotFound") || errorName.contains("ResponseStatusException")) {
            return HttpStatus.NOT_FOUND;
        }
        if (errorName.contains("ConnectException") || errorName.contains("TimeoutException")) {
            return HttpStatus.SERVICE_UNAVAILABLE;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
