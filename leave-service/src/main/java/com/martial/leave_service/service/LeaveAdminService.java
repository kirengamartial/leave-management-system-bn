package com.martial.leave_service.service;

import com.martial.leave_service.dto.LeaveTypeRequest;
import com.martial.leave_service.dto.LeaveTypeResponse;
import java.util.Map;

public interface LeaveAdminService {
    LeaveTypeResponse createLeaveType(LeaveTypeRequest request);

    LeaveTypeResponse updateLeaveType(String typeId, LeaveTypeRequest request);

    void deleteLeaveType(String typeId);

    Map<String, Double> adjustLeaveBalance(String userId, String leaveType, Double days, String reason);
}