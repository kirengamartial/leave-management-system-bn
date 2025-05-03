package com.martial.leave_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "leaves")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Leave {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String userId;

    @Enumerated(EnumType.STRING)
    private LeaveType leaveType;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String reason;

    @Enumerated(EnumType.STRING)
    private LeaveStatus status;

    @ElementCollection
    private List<String> supportingDocuments;
    private String approverComment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Add the missing balance field
    private double balance;
}