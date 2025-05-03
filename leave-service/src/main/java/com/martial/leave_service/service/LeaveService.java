package com.martial.leave_service.service;

import com.martial.leave_service.dto.LeaveRequest;
import com.martial.leave_service.dto.LeaveResponse;
import com.martial.leave_service.dto.LeaveStatusUpdateDTO;
import com.martial.leave_service.model.LeaveType;
import com.martial.leave_service.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface LeaveService {
    LeaveResponse createLeave(LeaveRequest leaveRequest);

    LeaveResponse updateLeaveStatus(String leaveId, LeaveStatusUpdateDTO updateRequest);

    LeaveResponse getLeaveById(String leaveId);

    List<LeaveResponse> getLeavesByUserId(String userId);

    List<LeaveResponse> getCurrentLeaves();

    void processMonthlyAccrual();

    void processYearEndCarryOver();

    double getLeaveBalance(String userId, LeaveType leaveType); // Changed from Double to double

    LeaveResponse createLeaveRequest(LeaveRequest request);

    List<LeaveResponse> getTeamLeaves(String department, LocalDateTime startDate, LocalDateTime endDate);
    // Remove this line:
    // void syncLeaveWithGoogleCalendar(String leaveId);

    LeaveResponse applyForLeave(LeaveRequest request);

    List<LeaveResponse> getMyLeaves();

    Map<String, Double> getMyLeaveBalance();

    List<LeaveResponse> getTeamLeaves();

    LeaveResponse approveLeave(String leaveId, String comment);

    LeaveResponse rejectLeave(String leaveId, String comment);

    List<LeaveResponse> getAllLeaves();

    byte[] generateLeaveReport(String startDate, String endDate, String department);

    User getUserByEmail(String email);

    String uploadSupportingDocument(MultipartFile file);
}