package com.sentinel.gateway.exception;

import com.sentinel.common.api.ApiErrorResponse;
import java.util.Map;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;

@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Throwable error = getError(request);
        HttpStatus status = determineHttpStatus(error);

        String message;
        if (error instanceof ResponseStatusException rse && rse.getReason() != null) {
            message = rse.getReason();
        } else if (error != null && error.getMessage() != null && !error.getMessage().isBlank()) {
            message = error.getMessage();
        } else {
            message = "An unexpected error occurred at the gateway edge";
        }

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
        if (error instanceof ResponseStatusException rse) {
            return HttpStatus.valueOf(rse.getStatusCode().value());
        }
        String errorName = error.getClass().getSimpleName();
        if (errorName.contains("NotFound")) {
            return HttpStatus.NOT_FOUND;
        }
        if (errorName.contains("ConnectException") || errorName.contains("TimeoutException")) {
            return HttpStatus.SERVICE_UNAVAILABLE;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
