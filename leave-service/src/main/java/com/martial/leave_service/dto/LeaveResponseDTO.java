package com.martial.leave_service.dto;

import com.martial.leave_service.model.LeaveStatus;
import com.martial.leave_service.model.LeaveType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class LeaveResponseDTO {
    private String id;
    private String userId;
    private LeaveType leaveType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String reason;
    private LeaveStatus status;
    private List<String> supportingDocuments;
    private String approverComment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}