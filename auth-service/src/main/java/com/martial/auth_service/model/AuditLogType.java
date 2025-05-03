package com.martial.auth_service.model;

public enum AuditLogType {
    ACCOUNT_CREATION,
    ACCOUNT_UPDATE,
    LOGIN,
    LOGOUT,
    PASSWORD_CHANGE,
    EMAIL_VERIFICATION,
    SECURITY_EVENT,
    OAUTH2_LOGIN
}