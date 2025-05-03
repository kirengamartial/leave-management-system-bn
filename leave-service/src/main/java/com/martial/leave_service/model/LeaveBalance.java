package com.martial.leave_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "leave_balances")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveBalance {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    private String userId;
    
    @Enumerated(EnumType.STRING)
    private LeaveType leaveType;
    
    private double totalDays;
    private double usedDays;
    private double carryOverDays;
    private int year;
}