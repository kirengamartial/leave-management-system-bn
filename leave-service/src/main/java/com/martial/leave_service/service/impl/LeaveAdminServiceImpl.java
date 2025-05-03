package com.martial.leave_service.service.impl;

import com.martial.leave_service.dto.LeaveTypeRequest;
import com.martial.leave_service.dto.LeaveTypeResponse;
import com.martial.leave_service.service.LeaveAdminService;
import com.martial.leave_service.model.LeaveTypeUser;
import com.martial.leave_service.repository.LeaveTypeRepository;
import com.martial.leave_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LeaveAdminServiceImpl implements LeaveAdminService {

    private final LeaveTypeRepository leaveTypeRepository;
    private final UserRepository userRepository;

    @Override
    public LeaveTypeResponse createLeaveType(LeaveTypeRequest request) {
        LeaveTypeUser leaveType = LeaveTypeUser.builder()
                .name(request.getName())
                .defaultDays(request.getDefaultDays())
                .description(request.getDescription())
                .build();

        LeaveTypeUser savedType = leaveTypeRepository.save(leaveType);
        return convertToResponse(savedType);
    }

    @Override
    public LeaveTypeResponse updateLeaveType(String typeId, LeaveTypeRequest request) {
        LeaveTypeUser leaveType = leaveTypeRepository.findById(typeId)
                .orElseThrow(() -> new RuntimeException("Leave type not found"));

        leaveType.setName(request.getName());
        leaveType.setDefaultDays(request.getDefaultDays());
        leaveType.setDescription(request.getDescription());

        LeaveTypeUser updatedType = leaveTypeRepository.save(leaveType);
        return convertToResponse(updatedType);
    }

    @Override
    public void deleteLeaveType(String typeId) {
        leaveTypeRepository.deleteById(typeId);
    }

    @Override
    public Map<String, Double> adjustLeaveBalance(String userId, String leaveType, Double days, String reason) {
        // Implement the logic to adjust leave balance
        // This is a placeholder implementation
        Map<String, Double> result = new HashMap<>();
        result.put(leaveType, days);
        return result;
    }

    private LeaveTypeResponse convertToResponse(LeaveTypeUser leaveType) {
        return LeaveTypeResponse.builder()
                .id(leaveType.getId())
                .name(leaveType.getName())
                .defaultDays(leaveType.getDefaultDays())
                .description(leaveType.getDescription())
                .build();
    }
}