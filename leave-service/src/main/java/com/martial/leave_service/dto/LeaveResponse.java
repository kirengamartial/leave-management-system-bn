package com.martial.leave_service.dto;

import lombok.Data;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

import com.martial.leave_service.model.LeaveStatus;
import com.martial.leave_service.model.LeaveType;

@Data
@Builder
public class LeaveResponse {
    private String id;
    private String userId;
    private LeaveType leaveType; // Changed from String to LeaveType
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String reason;
    private LeaveStatus status; // Changed from String to LeaveStatus
    private List<String> supportingDocuments;
    private String approverComment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}