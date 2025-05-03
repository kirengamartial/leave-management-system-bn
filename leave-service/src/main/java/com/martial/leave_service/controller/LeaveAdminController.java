package com.martial.leave_service.controller;

import com.martial.leave_service.dto.LeaveTypeRequest;
import com.martial.leave_service.dto.LeaveTypeResponse;
import com.martial.leave_service.service.LeaveAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/leaves/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class LeaveAdminController {

    private final LeaveAdminService leaveAdminService;

    @PostMapping("/types")
    public ResponseEntity<LeaveTypeResponse> createLeaveType(@RequestBody LeaveTypeRequest request) {
        return ResponseEntity.ok(leaveAdminService.createLeaveType(request));
    }

    @PutMapping("/types/{typeId}")
    public ResponseEntity<LeaveTypeResponse> updateLeaveType(
            @PathVariable String typeId,
            @RequestBody LeaveTypeRequest request) {
        return ResponseEntity.ok(leaveAdminService.updateLeaveType(typeId, request));
    }

    @DeleteMapping("/types/{typeId}")
    public ResponseEntity<Void> deleteLeaveType(@PathVariable String typeId) {
        leaveAdminService.deleteLeaveType(typeId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/balance/adjust")
    public ResponseEntity<Map<String, Double>> adjustLeaveBalance(
            @RequestParam String userId,
            @RequestParam String leaveType,
            @RequestParam Double days,
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(leaveAdminService.adjustLeaveBalance(userId, leaveType, days, reason));
    }
}