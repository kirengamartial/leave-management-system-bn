package com.martial.auth_service.service;

import com.martial.auth_service.model.AuditLog;
import com.martial.auth_service.model.AuditLogType;
import com.martial.auth_service.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditService {
    private final AuditLogRepository auditLogRepository;
    private final HttpServletRequest request;
    
    public void logEvent(String userId, String action, String details, AuditLogType type) {
        AuditLog log = AuditLog.builder()
            .userId(userId)
            .action(action)
            .details(details)
            .ipAddress(request.getRemoteAddr())
            .timestamp(LocalDateTime.now())
            .type(type)
            .build();
            
        auditLogRepository.save(log);
    }
}