package com.itsm.incidentmanagement.service;

import com.itsm.incidentmanagement.service.AuditService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @InjectMocks
    private AuditService auditService;

    // ✅ TESTE 1: Log de login
    @Test
    void testLogLogin() {
        assertDoesNotThrow(() -> {
            auditService.logLogin("admin", true, "127.0.0.1");
        });
    }

    // ✅ TESTE 2: Log de criação de ticket
    @Test
    void testLogTicketCreation() {
        assertDoesNotThrow(() -> {
            auditService.logTicketCreation(1L, "Problema de rede", 9L, "ABERTO");
        });
    }

    // ✅ TESTE 3: Log de atribuição de ticket
    @Test
    void testLogTicketAssignment() {
        assertDoesNotThrow(() -> {
            auditService.logTicketAssignment(1L, 1L, 9L);
        });
    }

    // ✅ TESTE 4: Log de mudança de estado
    @Test
    void testLogTicketStateChange() {
        assertDoesNotThrow(() -> {
            auditService.logTicketStateChange(1L, "ABERTO", "EM_CURSO", 9L);
        });
    }

    // ✅ TESTE 5: Log de erro
    @Test
    void testLogError() {
        assertDoesNotThrow(() -> {
            auditService.logError("ATRIBUICAO", "Erro ao atribuir", "Detalhes do erro");
        });
    }
}