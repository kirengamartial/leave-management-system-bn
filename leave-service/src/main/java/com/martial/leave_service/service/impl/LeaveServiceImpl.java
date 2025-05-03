package com.martial.leave_service.service.impl;

import com.martial.leave_service.dto.LeaveRequest;
import com.martial.leave_service.dto.LeaveResponse;
import com.martial.leave_service.dto.LeaveStatusUpdateDTO;
import com.martial.leave_service.model.Leave;
import com.martial.leave_service.model.LeaveStatus;
import com.martial.leave_service.model.LeaveType;
import com.martial.leave_service.model.User;
import com.martial.leave_service.repository.LeaveRepository;
import com.martial.leave_service.service.LeaveService;
import com.martial.leave_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

// At the top of the file, add:
import java.util.HashMap;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRepository leaveRepository;
    private final UserService userService;

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    @Override
    public LeaveResponse createLeave(LeaveRequest request) {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByEmail(currentUserEmail);
        String currentUserId = user.getId();
        Leave leave = Leave.builder()
                .userId(currentUserId)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .leaveType(LeaveType.valueOf(request.getLeaveType())) // Convert String to LeaveType enum
                .reason(request.getReason())
                .status(LeaveStatus.PENDING) // Use enum instead of String
                .supportingDocuments(request.getSupportingDocuments())
                .createdAt(LocalDateTime.now())
                .build();

        Leave savedLeave = leaveRepository.save(leave);
        return convertToLeaveResponse(savedLeave);
    }

    @Override
    public LeaveResponse createLeaveRequest(LeaveRequest request) {
        return createLeave(request); // This is an alias for createLeave
    }

    @Override
    public LeaveResponse getLeaveById(String leaveId) {
        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave not found with id: " + leaveId));
        return convertToLeaveResponse(leave);
    }

    @Override
    public List<LeaveResponse> getLeavesByUserId(String userId) {
        List<Leave> leaves = leaveRepository.findByUserId(userId);
        return leaves.stream()
                .map(this::convertToLeaveResponse)
                .toList();
    }

    @Override
    public List<LeaveResponse> getCurrentLeaves() {
        LocalDateTime now = LocalDateTime.now();
        return leaveRepository.findByStartDateLessThanEqualAndEndDateGreaterThanEqual(now, now)
                .stream()
                .map(this::convertToLeaveResponse)
                .toList();
    }

    @Override
    public List<LeaveResponse> getTeamLeaves(String departmentId, LocalDateTime startDate, LocalDateTime endDate) {
        return leaveRepository.findByDepartmentIdAndDateRange(departmentId, startDate, endDate)
                .stream()
                .map(this::convertToLeaveResponse)
                .toList();
    }

    @Override
    public double getLeaveBalance(String userId, LeaveType leaveType) {
        // Pass enum name to match the repository method
        return leaveRepository.calculateLeaveBalance(userId, leaveType.name());
    }

    @Override
    public LeaveResponse updateLeaveStatus(String leaveId, LeaveStatusUpdateDTO updateDTO) {
        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave not found with id: " + leaveId));

        leave.setStatus(updateDTO.getStatus());
        leave.setApproverComment(updateDTO.getComment());
        leave.setUpdatedAt(LocalDateTime.now());

        Leave updatedLeave = leaveRepository.save(leave);
        return convertToLeaveResponse(updatedLeave);
    }

    @Override
    public void processMonthlyAccrual() {
        // This should run monthly to add accrued leave days
        // Typically 1.66 days for annual leave (20 days / 12 months)
        userService.getAllUsers().forEach(user -> {
            double accrualAmount = 1.66; // This should be configurable per leave type
            leaveRepository.addAccruedLeave(user.getId(), accrualAmount);
        });
    }

    @Override
    public void processYearEndCarryOver() {
        // This should run at year end to:
        // 1. Calculate remaining balance
        // 2. Apply carry-over limit (e.g., 5 days)
        // 3. Expire excess days
        userService.getAllUsers().forEach(user -> {
            double maxCarryOver = 5.0; // This should be configurable
            leaveRepository.processYearEndBalance(user.getId(), maxCarryOver);
        });
    }

    private LeaveResponse convertToLeaveResponse(Leave leave) {
        return LeaveResponse.builder()
                .id(leave.getId())
                .userId(leave.getUserId())
                .leaveType(leave.getLeaveType())
                .startDate(leave.getStartDate())
                .endDate(leave.getEndDate())
                .reason(leave.getReason())
                .status(leave.getStatus())
                .supportingDocuments(leave.getSupportingDocuments())
                .approverComment(leave.getApproverComment())
                .createdAt(leave.getCreatedAt())
                .updatedAt(leave.getUpdatedAt())
                .build();
    }

    @Override
    public LeaveResponse applyForLeave(LeaveRequest request) {
        return createLeave(request);
    }

    @Override
    public List<LeaveResponse> getMyLeaves() {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByEmail(currentUserEmail);
        String currentUserId = user.getId();
        return getLeavesByUserId(currentUserId);
    }

    @Override
    public Map<String, Double> getMyLeaveBalance() {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByEmail(currentUserEmail);
        String currentUserId = user.getId();
        Map<String, Double> balances = new HashMap<>();
        for (LeaveType type : LeaveType.values()) {
            balances.put(type.name(), getLeaveBalance(currentUserId, type));
        }
        return balances;
    }

    @Override
    public List<LeaveResponse> getTeamLeaves() {
        String currentUserEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByEmail(currentUserEmail);
        String currentUserId = user.getId();
        LocalDateTime startOfMonth = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0);
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);
        return getTeamLeaves(user.getDepartment(), startOfMonth, endOfMonth);
    }

    @Override
    public LeaveResponse approveLeave(String leaveId, String comment) {
        LeaveStatusUpdateDTO updateDTO = new LeaveStatusUpdateDTO();
        updateDTO.setStatus(LeaveStatus.APPROVED);
        updateDTO.setComment(comment);
        return updateLeaveStatus(leaveId, updateDTO);
    }

    @Override
    public LeaveResponse rejectLeave(String leaveId, String comment) {
        LeaveStatusUpdateDTO updateDTO = new LeaveStatusUpdateDTO();
        updateDTO.setStatus(LeaveStatus.REJECTED);
        updateDTO.setComment(comment);
        return updateLeaveStatus(leaveId, updateDTO);
    }

    @Override
    public List<LeaveResponse> getAllLeaves() {
        return leaveRepository.findAll().stream()
                .map(this::convertToLeaveResponse)
                .toList();
    }

    @Override
    public byte[] generateLeaveReport(String startDate, String endDate, String department) {
        // Parse dates
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        LocalDateTime start = LocalDateTime.parse(startDate, formatter);
        LocalDateTime end = LocalDateTime.parse(endDate, formatter);

        // Get leaves based on criteria
        List<Leave> leaves;
        if (department != null && !department.isEmpty()) {
            leaves = leaveRepository.findByDepartmentIdAndDateRange(department, start, end);
        } else {
            leaves = leaveRepository.findByStartDateBetweenOrEndDateBetween(start, end, start, end);
        }

        // Create workbook
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Leave Report");

            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] columns = { "ID", "User ID", "Leave Type", "Start Date", "End Date", "Status", "Reason" };
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            // Create data rows
            int rowNum = 1;
            for (Leave leave : leaves) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(leave.getId());
                row.createCell(1).setCellValue(leave.getUserId());
                row.createCell(2).setCellValue(leave.getLeaveType().name());
                row.createCell(3).setCellValue(leave.getStartDate().toString());
                row.createCell(4).setCellValue(leave.getEndDate().toString());
                row.createCell(5).setCellValue(leave.getStatus().name());
                row.createCell(6).setCellValue(leave.getReason());
            }

            // Auto-size columns
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Convert workbook to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate report", e);
        }
    }

    @Override
    public User getUserByEmail(String email) {
        return userService.getUserByEmail(email);
    }

    @Override
    public String uploadSupportingDocument(MultipartFile file) {
        try {
            Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                    "cloud_name", cloudName,
                    "api_key", apiKey,
                    "api_secret", apiSecret));

            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            return (String) uploadResult.get("url");
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload file: " + e.getMessage());
        }
    }
}