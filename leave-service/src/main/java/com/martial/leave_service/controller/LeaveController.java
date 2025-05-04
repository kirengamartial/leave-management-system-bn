package com.martial.leave_service.controller;

import com.martial.leave_service.dto.LeaveRequest;
import com.martial.leave_service.dto.LeaveResponse;
import com.martial.leave_service.dto.LeaveStatusUpdateRequest;
import com.martial.leave_service.model.LeaveStatus;
import com.martial.leave_service.model.LeaveType;
import com.martial.leave_service.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    // Endpoints accessible by all authenticated users (STAFF, MANAGER, ADMIN)
    @PostMapping(value = "/apply", consumes = "multipart/form-data")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
    public ResponseEntity<LeaveResponse> applyForLeave(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam("leaveType") String leaveType,
            @RequestParam(value = "reason", required = false) String reason,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        LeaveRequest request = new LeaveRequest();
        request.setStartDate(LocalDateTime.parse(startDate));
        request.setEndDate(LocalDateTime.parse(endDate));
        request.setLeaveType(leaveType);
        request.setReason(reason);
        if (file != null) {
            String fileUrl = leaveService.uploadSupportingDocument(file);
            request.setSupportingDocuments(List.of(fileUrl));
        }
        return ResponseEntity.ok(leaveService.applyForLeave(request));
    }

    @GetMapping("/my-leaves")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
    public ResponseEntity<List<LeaveResponse>> getMyLeaves() {
        return ResponseEntity.ok(leaveService.getMyLeaves());
    }

    @GetMapping("/balance")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN', 'HR')")
    public ResponseEntity<Map<String, Double>> getMyLeaveBalance() {
        return ResponseEntity.ok(leaveService.getMyLeaveBalance());
    }

    // Endpoints accessible by MANAGER and ADMIN only
    @GetMapping("/team")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<List<LeaveResponse>> getTeamLeaves() {
        return ResponseEntity.ok(leaveService.getTeamLeaves());
    }

    @PutMapping("/{leaveId}/approve")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<LeaveResponse> approveLeave(
            @PathVariable String leaveId,
            @RequestParam(required = false) String comment) {
        return ResponseEntity.ok(leaveService.approveLeave(leaveId, comment));
    }

    @PutMapping("/{leaveId}/reject")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<LeaveResponse> rejectLeave(
            @PathVariable String leaveId,
            @RequestParam(required = false) String comment) {
        return ResponseEntity.ok(leaveService.rejectLeave(leaveId, comment));
    }

    // Endpoints accessible by ADMIN only
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LeaveResponse>> getAllLeaves(
            @RequestParam(required = false) LeaveType leaveType,
            @RequestParam(required = false) LeaveStatus status) {
        return ResponseEntity.ok(leaveService.getAllLeaves(leaveType, status));
    }

    @GetMapping("/reports")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> generateLeaveReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String department) {
        return ResponseEntity.ok()
                .header("Content-Type", "application/vnd.ms-excel")
                .header("Content-Disposition", "attachment; filename=leave-report.xlsx")
                .body(leaveService.generateLeaveReport(startDate, endDate, department));
    }

    // New endpoint for uploading supporting documents
    @PostMapping("/upload-document")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
    public ResponseEntity<String> uploadSupportingDocument(@RequestParam("file") MultipartFile file) {
        String fileUrl = leaveService.uploadSupportingDocument(file);
        return ResponseEntity.ok(fileUrl);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
    public ResponseEntity<LeaveResponse> createLeave(@RequestBody LeaveRequest request) {
        return ResponseEntity.ok(leaveService.createLeave(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'MANAGER', 'ADMIN')")
    public ResponseEntity<LeaveResponse> getLeaveById(@PathVariable String id) {
        return ResponseEntity.ok(leaveService.getLeaveById(id));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<LeaveResponse> updateLeaveStatus(
            @PathVariable String id,
            @RequestBody LeaveStatusUpdateRequest request) {
        return ResponseEntity.ok(leaveService.updateLeaveStatus(id, request));
    }
}