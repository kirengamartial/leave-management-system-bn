package com.martial.leave_service.dto;

import com.martial.leave_service.model.LeaveStatus;
import lombok.Data;

@Data
public class LeaveStatusUpdateDTO {
    private LeaveStatus status;
    private String approverComment;
    private String comment; // Add this field
}