package com.notificationbridge.controller;

import com.notificationbridge.dto.NotificationResponse;
import com.notificationbridge.model.LeadEvent;
import com.notificationbridge.service.LeadNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/leads")
@RequiredArgsConstructor
@Tag(name = "Leads", description = "Lead notification API")
public class LeadNotificationController {

    private final LeadNotificationService leadNotificationService;

    @PostMapping(value = "/notify", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Notify channels for a lead", description = "Accepts a lead event. Only HIGH priority leads trigger Slack and SMS notifications.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Processed (notifications sent, partial failure, or skipped)",
                    content = @Content(schema = @Schema(implementation = NotificationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid API key")
    })
    public ResponseEntity<NotificationResponse> notify(@Valid @RequestBody LeadEvent lead) {
        NotificationResponse response = leadNotificationService.notify(lead);
        return ResponseEntity.ok(response);
    }
}
