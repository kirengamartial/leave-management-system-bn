package com.martial.leave_service.service.impl;

import com.martial.leave_service.dto.LeaveRequest;
import com.martial.leave_service.dto.LeaveResponse;
import com.martial.leave_service.dto.LeaveStatusUpdateRequest;
import com.martial.leave_service.model.Leave;
import com.martial.leave_service.model.LeaveStatus;
import com.martial.leave_service.model.LeaveType;
import com.martial.leave_service.model.User;
import com.martial.leave_service.repository.LeaveRepository;
import com.martial.leave_service.service.LeaveService;
import com.martial.leave_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRepository leaveRepository;
    private final UserService userService;

    @Override
    @Transactional
    public LeaveResponse createLeave(LeaveRequest request) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByEmail(currentUserEmail);
        String currentUserId = user.getId();

        Leave leave = Leave.builder()
                .userId(currentUserId)
                .leaveType(LeaveType.valueOf(request.getLeaveType()))
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .days(calculateDays(request.getStartDate(), request.getEndDate()))
                .reason(request.getReason())
                .status(LeaveStatus.PENDING)
                .build();

        Leave savedLeave = leaveRepository.save(leave);
        return convertToLeaveResponse(savedLeave, user);
    }

    @Override
    public List<LeaveResponse> getMyLeaves() {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByEmail(currentUserEmail);
        List<Leave> leaves = leaveRepository.findByUserId(user.getId());
        return leaves.stream()
                .map(leave -> convertToLeaveResponse(leave, user))
                .collect(Collectors.toList());
    }

    @Override
    public LeaveResponse getLeaveById(String id) {
        Leave leave = leaveRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave not found"));
        User user = userService.getUserByEmail(leave.getUserId());
        return convertToLeaveResponse(leave, user);
    }

    @Override
    @Transactional
    public LeaveResponse updateLeaveStatus(String leaveId, LeaveStatusUpdateRequest request) {
        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave not found"));

        leave.setStatus(request.getStatus());
        leave.setComment(request.getComment());

        Leave updatedLeave = leaveRepository.save(leave);
        User user = userService.getUser(leave.getUserId());
        return convertToLeaveResponse(updatedLeave, user);
    }

    @Override
    public List<LeaveResponse> getAllLeaves(LeaveType leaveType, LeaveStatus status) {
        // Instead of using Example, use a more direct approach
        List<Leave> leaves;

        if (leaveType != null && status != null) {
            leaves = leaveRepository.findByLeaveTypeAndStatus(leaveType, status);
        } else if (leaveType != null) {
            leaves = leaveRepository.findByLeaveType(leaveType);
        } else if (status != null) {
            leaves = leaveRepository.findByStatus(status);
        } else {
            leaves = leaveRepository.findAll();
        }

        // Add debug log
        System.out.println("Found " + leaves.size() + " leave records");

        return leaves.stream()
                .map(leave -> {
                    User user = userService.getUser(leave.getUserId());
                    return convertToLeaveResponse(leave, user);
                })
                .collect(Collectors.toList());
    }

    private LeaveResponse convertToLeaveResponse(Leave leave, User user) {
        return LeaveResponse.builder()
                .id(leave.getId())
                .userId(leave.getUserId())
                .employeeName(user.getFirstName() + " " + user.getLastName())
                .employeeEmail(user.getEmail())
                .leaveType(leave.getLeaveType())
                .startDate(leave.getStartDate())
                .endDate(leave.getEndDate())
                .days(leave.getDays())
                .reason(leave.getReason())
                .status(leave.getStatus())
                .comment(leave.getComment())
                .createdAt(leave.getCreatedAt())
                .updatedAt(leave.getUpdatedAt())
                .build();
    }

    private int calculateDays(LocalDateTime startDate, LocalDateTime endDate) {
        return (int) java.time.Duration.between(startDate, endDate).toDays() + 1;
    }

    @Override
    public List<LeaveResponse> getLeavesByUserId(String userId) {
        List<Leave> leaves = leaveRepository.findByUserId(userId);
        return leaves.stream()
                .map(leave -> {
                    User user = userService.getUserByEmail(leave.getUserId());
                    return convertToLeaveResponse(leave, user);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<LeaveResponse> getCurrentLeaves() {
        LocalDateTime now = LocalDateTime.now();
        return leaveRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqual(now, now)
                .stream()
                .map(leave -> {
                    User user = userService.getUserByEmail(leave.getUserId());
                    return convertToLeaveResponse(leave, user);
                })
                .collect(Collectors.toList());
    }

    @Override
    public double getLeaveBalance(String userId, LeaveType leaveType) {
        return leaveRepository.calculateLeaveBalance(userId, leaveType.name());
    }

    @Override
    public LeaveResponse createLeaveRequest(LeaveRequest request) {
        return createLeave(request);
    }

    @Override
    public List<LeaveResponse> getTeamLeaves(String department, LocalDateTime startDate, LocalDateTime endDate) {
        return leaveRepository.findByDepartmentIdAndDateRange(department, startDate, endDate)
                .stream()
                .map(leave -> {
                    User user = userService.getUserByEmail(leave.getUserId());
                    return convertToLeaveResponse(leave, user);
                })
                .collect(Collectors.toList());
    }

    @Override
    public LeaveResponse applyForLeave(LeaveRequest request) {
        return createLeave(request);
    }

    @Override
    public List<LeaveResponse> getTeamLeaves() {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByEmail(currentUserEmail);
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);
        return getTeamLeaves(user.getDepartment(), startOfMonth, endOfMonth);
    }

    @Override
    public LeaveResponse approveLeave(String leaveId, String comment) {
        LeaveStatusUpdateRequest request = new LeaveStatusUpdateRequest();
        request.setStatus(LeaveStatus.APPROVED);
        request.setComment(comment);
        return updateLeaveStatus(leaveId, request);
    }

    @Override
    public LeaveResponse rejectLeave(String leaveId, String comment) {
        LeaveStatusUpdateRequest request = new LeaveStatusUpdateRequest();
        request.setStatus(LeaveStatus.REJECTED);
        request.setComment(comment);
        return updateLeaveStatus(leaveId, request);
    }

    @Override
    public User getUserByEmail(String email) {
        return userService.getUserByEmail(email);
    }

    @Override
    public Map<String, Double> getMyLeaveBalance() {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByEmail(currentUserEmail);
        Map<String, Double> balances = new HashMap<>();

        for (LeaveType type : LeaveType.values()) {
            balances.put(type.name(), getLeaveBalance(user.getId(), type));
        }

        return balances;
    }

    @Override
    public byte[] generateLeaveReport(String startDate, String endDate, String department) {
        // For now, return empty byte array as this feature is not needed
        return new byte[0];
    }

    @Override
    public String uploadSupportingDocument(MultipartFile file) {
        // For now, return empty string as this feature is not needed
        return "";
    }
}