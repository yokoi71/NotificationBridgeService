package com.notificationbridge.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeadEvent {

    @NotBlank(message = "leadId is required")
    @JsonProperty("leadId")
    private String leadId;

    @NotBlank(message = "name is required")
    private String name;

    private String company;

    private String email;

    private String phone;

    @NotNull(message = "priority is required")
    private LeadPriority priority;

    @JsonProperty("createdAt")
    private Instant createdAt;

    public enum LeadPriority {
        HIGH,
        MEDIUM,
        LOW
    }
}
