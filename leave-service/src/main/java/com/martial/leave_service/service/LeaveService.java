package com.martial.leave_service.service;

import com.martial.leave_service.dto.LeaveRequest;
import com.martial.leave_service.dto.LeaveResponse;
import com.martial.leave_service.dto.LeaveStatusUpdateRequest;
import com.martial.leave_service.model.LeaveStatus;
import com.martial.leave_service.model.LeaveType;
import com.martial.leave_service.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface LeaveService {
    LeaveResponse createLeave(LeaveRequest request);

    List<LeaveResponse> getMyLeaves();

    LeaveResponse getLeaveById(String id);

    LeaveResponse updateLeaveStatus(String leaveId, LeaveStatusUpdateRequest request);

    List<LeaveResponse> getAllLeaves(LeaveType leaveType, LeaveStatus status);

    List<LeaveResponse> getLeavesByUserId(String userId);

    List<LeaveResponse> getCurrentLeaves();

    double getLeaveBalance(String userId, LeaveType leaveType);

    LeaveResponse createLeaveRequest(LeaveRequest request);

    List<LeaveResponse> getTeamLeaves(String department, LocalDateTime startDate, LocalDateTime endDate);

    LeaveResponse applyForLeave(LeaveRequest request);

    List<LeaveResponse> getTeamLeaves();

    LeaveResponse approveLeave(String leaveId, String comment);

    LeaveResponse rejectLeave(String leaveId, String comment);

    User getUserByEmail(String email);

    Map<String, Double> getMyLeaveBalance();

    byte[] generateLeaveReport(String startDate, String endDate, String department);

    String uploadSupportingDocument(MultipartFile file);
}