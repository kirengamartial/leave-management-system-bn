package com.martial.leave_service.repository;

import com.martial.leave_service.model.LeaveTypeUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveTypeRepository extends JpaRepository<LeaveTypeUser, String> {
}