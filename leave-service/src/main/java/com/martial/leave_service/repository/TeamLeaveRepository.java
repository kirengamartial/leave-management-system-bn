package com.martial.leave_service.repository;

import com.martial.leave_service.model.Leave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TeamLeaveRepository extends JpaRepository<Leave, String> {
    @Query("SELECT l FROM Leave l WHERE l.userId IN " +
            "(SELECT u.id FROM User u WHERE u.department = :department) " +
            "AND ((l.startDate BETWEEN :startDate AND :endDate) " +
            "OR (l.endDate BETWEEN :startDate AND :endDate))")
    List<Leave> findTeamLeavesByDepartment(
            @Param("department") String department,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}