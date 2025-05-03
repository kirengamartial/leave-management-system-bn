package com.martial.leave_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notification_preferences")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreference {
    @Id
    private String userId;

    private boolean emailNotifications;
    private boolean inAppNotifications;
    private boolean leaveStatusUpdates;
    private boolean leaveApprovalRequests;
    private boolean leaveComments;
}