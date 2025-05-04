package com.martial.leave_service.dto;

import com.martial.leave_service.model.LeaveStatus;
import lombok.Data;

@Data
public class LeaveStatusUpdateRequest {
    private LeaveStatus status;
    private String comment;
}