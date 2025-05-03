package com.martial.leave_service.repository;

import com.martial.leave_service.model.Leave;
import com.martial.leave_service.model.LeaveType;
import com.martial.leave_service.model.User; // Add this import
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

public interface LeaveRepository extends JpaRepository<Leave, String> {
        List<Leave> findByUserId(String userId);

        @Query("SELECT l FROM Leave l WHERE l.startDate <= :endDate AND l.endDate >= :startDate")
        List<Leave> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Query("SELECT l FROM Leave l WHERE l.userId IN (SELECT u.id FROM User u WHERE u.department = :departmentId) " +
                        "AND (l.startDate <= :endDate AND l.endDate >= :startDate)")
        List<Leave> findByDepartmentIdAndDateRange(
                        @Param("departmentId") String departmentId,
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        // Updated for PostgreSQL
        @Query(value = "SELECT COALESCE(SUM(CASE WHEN status = 'APPROVED' THEN " +
                        "EXTRACT(DAY FROM (end_date - start_date)) + 1 ELSE 0 END), 0) " +
                        "FROM leaves WHERE user_id = :userId AND leave_type = :leaveType", nativeQuery = true)
        double calculateLeaveBalance(@Param("userId") String userId, @Param("leaveType") String leaveType);

        @Modifying
        @Transactional
        @Query("UPDATE Leave l SET l.balance = l.balance + :amount WHERE l.userId = :userId")
        void addAccruedLeave(@Param("userId") String userId, @Param("amount") double amount);

        @Modifying
        @Transactional
        @Query("UPDATE Leave l SET l.balance = LEAST(l.balance, :maxCarryOver) WHERE l.userId = :userId")
        void processYearEndBalance(@Param("userId") String userId, @Param("maxCarryOver") double maxCarryOver);

        // Add the missing method
        @Query("SELECT l FROM Leave l WHERE " +
                        "(l.startDate BETWEEN :startDate AND :endDate) OR " +
                        "(l.endDate BETWEEN :startDateTwo AND :endDateTwo)")
        List<Leave> findByStartDateBetweenOrEndDateBetween(
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate,
                        @Param("startDateTwo") LocalDateTime startDateTwo,
                        @Param("endDateTwo") LocalDateTime endDateTwo);
}