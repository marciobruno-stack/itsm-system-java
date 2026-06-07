package com.itsm.incidentmanagement;

import com.itsm.incidentmanagement.model.entity.Ticket;
import com.itsm.incidentmanagement.model.entity.Utilizador;
import com.itsm.incidentmanagement.service.TicketService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TestRunner implements CommandLineRunner {

    private final TicketService ticketService;

    public TestRunner(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @Override
    public void run(String... args) throws Exception {
        // Verificar se existe um utilizador com id 4 (Carlos User) no banco
        // Como é só um teste, criamos um ticket associado a esse utilizador
        Utilizador utilizador = new Utilizador();
        utilizador.setId(4L);  // Carlos User

        Ticket novoTicket = Ticket.builder()
                .titulo("Problema na impressora")
                .descricao("Não consigo imprimir documentos")
                .prioridade("MEDIA")
                .tipo("INCIDENTE")
                .estado("ABERTO")   // será alterado no serviço, mas pode definir
                .abertoPor(utilizador)
                .build();

        Ticket saved = ticketService.createTicket(novoTicket);
        System.out.println("=== TESTE ===");
        System.out.println("Ticket criado com ID: " + saved.getId());
        System.out.println("Técnico atribuído: " +
                (saved.getTecnico() != null ? saved.getTecnico().getUtilizador().getNome() : "Nenhum técnico disponível"));
        System.out.println("Estado final: " + saved.getEstado());
    }
}