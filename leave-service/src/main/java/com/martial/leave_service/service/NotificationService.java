package com.martial.leave_service.service;

import com.martial.leave_service.model.Leave;
import com.martial.leave_service.model.Notification;
import com.martial.leave_service.model.NotificationType;
import com.martial.leave_service.model.User;
import com.martial.leave_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final JavaMailSender mailSender;
    private final NotificationRepository notificationRepository;
    
    public void sendApproverNotification(Leave leave, User approver) {
        // Send email notification
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(approver.getEmail());
        message.setSubject("New Leave Request Pending Approval");
        message.setText(String.format(
            "A new leave request from %s is pending your approval.\nStart Date: %s\nEnd Date: %s",
            leave.getUserId(),
            leave.getStartDate(),
            leave.getEndDate()
        ));
        mailSender.send(message);
        
        // Create in-app notification
        Notification notification = Notification.builder()
            .userId(approver.getId())
            .type(NotificationType.LEAVE_APPROVAL_REQUIRED)
            .message("New leave request pending approval")
            .referenceId(leave.getId())
            .isRead(false)
            .createdAt(LocalDateTime.now())
            .build();
            
        notificationRepository.save(notification);
    }
    
    public void sendLeaveStatusNotification(Leave leave, User requester) {
        // Similar implementation for leave status notifications
    }
}