package com.martial.leave_service.repository;

import com.martial.leave_service.model.LeaveBalance;
import com.martial.leave_service.model.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveBalanceRepository extends JpaRepository<LeaveBalance, String> {
    Optional<LeaveBalance> findByUserIdAndLeaveTypeAndYear(String userId, LeaveType leaveType, int year);

    List<LeaveBalance> findByUserId(String userId);

    List<LeaveBalance> findByYear(int year);
}