package com.itsm.incidentmanagement;

import com.itsm.incidentmanagement.service.AtivoService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class IncidentManagementApplicationTests {

    @MockBean
    private AtivoService ativoService;  // ✅ ADICIONADO

    @Test
    void contextLoads() {
        // Teste que verifica se o contexto da aplicação carrega corretamente
    }
}