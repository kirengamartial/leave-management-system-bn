package com.martial.leave_service.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
public class LeaveTypeRequest {
    @NotBlank(message = "Leave type name is required")
    private String name;

    @NotNull(message = "Default days allocation is required")
    @Positive(message = "Default days allocation must be positive")
    private Double defaultDays;

    private String description;

    @NotNull(message = "Carry forward allowed flag is required")
    private Boolean carryForwardAllowed;

    @Positive(message = "Maximum carry forward days must be positive")
    private Double maxCarryForwardDays;
}