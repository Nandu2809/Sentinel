package com.sentinel.alert.controller;

import com.sentinel.alert.dto.NotificationPreferenceResponse;
import com.sentinel.alert.dto.UpdatePreferenceRequest;
import com.sentinel.alert.service.NotificationPreferenceService;
import com.sentinel.common.api.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications/preferences")
public class NotificationPreferenceController {
    private final NotificationPreferenceService preferenceService;

    public NotificationPreferenceController(NotificationPreferenceService preferenceService) {
        this.preferenceService = preferenceService;
    }

    @GetMapping
    public ApiResponse<NotificationPreferenceResponse> getPreference(@RequestParam String userId) {
        return ApiResponse.success(HttpStatus.OK.value(), "Notification preferences retrieved", preferenceService.getPreferenceForUser(userId));
    }

    @PutMapping
    public ApiResponse<NotificationPreferenceResponse> updatePreference(@Valid @RequestBody UpdatePreferenceRequest request) {
        return ApiResponse.success(HttpStatus.OK.value(), "Notification preferences updated", preferenceService.updatePreference(request));
    }
}
