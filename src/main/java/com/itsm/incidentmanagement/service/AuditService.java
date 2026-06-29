package com.itsm.incidentmanagement.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AuditService {

    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");

    public void logLogin(String username, boolean success, String ip) {
        auditLogger.info("LOGIN | Username: {} | Success: {} | IP: {}", username, success, ip);
    }

    public void logTicketCreation(Long ticketId, String titulo, Long userId, String status) {
        auditLogger.info("TICKET_CREATE | TicketId: {} | Titulo: {} | UserId: {} | Status: {}",
                ticketId, titulo, userId, status);
    }

    public void logTicketAssignment(Long ticketId, Long tecnicoId, Long userId) {
        auditLogger.info("TICKET_ASSIGN | TicketId: {} | TecnicoId: {} | AssignedBy: {}",
                ticketId, tecnicoId, userId);
    }

    public void logTicketStateChange(Long ticketId, String oldState, String newState, Long userId) {
        auditLogger.info("TICKET_STATE_CHANGE | TicketId: {} | OldState: {} | NewState: {} | UserId: {}",
                ticketId, oldState, newState, userId);
    }

    public void logUserCreation(String username, String role, Long createdBy) {
        auditLogger.info("USER_CREATE | Username: {} | Role: {} | CreatedBy: {}", username, role, createdBy);
    }

    public void logTecnicoCreation(Long tecnicoId, String nome, Long userId) {
        auditLogger.info("TECNICO_CREATE | TecnicoId: {} | Nome: {} | UserId: {}", tecnicoId, nome, userId);
    }

    public void logError(String operation, String message, String details) {
        auditLogger.error("ERROR | Operation: {} | Message: {} | Details: {}", operation, message, details);
    }
}