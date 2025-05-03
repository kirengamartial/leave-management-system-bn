package com.martial.leave_service.dto;

import com.martial.leave_service.model.LeaveType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequestDTO {
    @NotNull(message = "Start date cannot be null")
    @FutureOrPresent(message = "Start date must be today or in the future")
    private LocalDateTime startDate;
    
    @NotNull(message = "End date cannot be null")
    @Future(message = "End date must be in the future")
    private LocalDateTime endDate;
    
    @NotNull(message = "Leave type cannot be null")
    private LeaveType leaveType;
    
    @NotBlank(message = "User ID cannot be blank")
    private String userId;
    
    private String reason;
    
    @Size(max = 5, message = "Maximum 5 supporting documents allowed")
    private List<String> supportingDocuments;
    
    @AssertTrue(message = "End date must be after start date")
    public boolean isEndDateAfterStartDate() {
        return endDate == null || startDate == null || endDate.isAfter(startDate);
    }
    
    @AssertTrue(message = "Leave duration cannot exceed 30 days")
    public boolean isValidDuration() {
        if (startDate == null || endDate == null) return true;
        return ChronoUnit.DAYS.between(startDate, endDate) <= 30;
    }
}