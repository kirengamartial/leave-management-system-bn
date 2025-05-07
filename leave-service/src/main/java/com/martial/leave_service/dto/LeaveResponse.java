package com.martial.leave_service.dto;

import com.martial.leave_service.model.LeaveStatus;
import com.martial.leave_service.model.LeaveType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveResponse {
    private String id;
    private String userId;
    private String employeeName;
    private String employeeEmail;
    private LeaveType leaveType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int days;
    private String reason;
    private LeaveStatus status;
    private String comment;
    private Double balance; // Add balance field
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}