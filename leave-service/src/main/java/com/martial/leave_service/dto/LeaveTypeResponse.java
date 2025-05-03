package com.martial.leave_service.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class LeaveTypeResponse {
    private String id;
    private String name;
    private Double defaultDays;
    private String description;
    private Boolean carryForwardAllowed;
    private Double maxCarryForwardDays;
    private Boolean active;
}