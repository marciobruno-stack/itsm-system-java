package com.itsm.incidentmanagement;

import com.itsm.incidentmanagement.model.entity.Ticket;
import com.itsm.incidentmanagement.model.entity.Utilizador;
import com.itsm.incidentmanagement.service.TicketService;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.stereotype.Component;

// @Component  ← COMENTADO PARA NÃO INTERFERIR COM OS TESTES
public class TestRunner { // implements CommandLineRunner {

    // private final TicketService ticketService;

    // public TestRunner(TicketService ticketService) {
    //     this.ticketService = ticketService;
    // }

    // @Override
    // public void run(String... args) {
    //     Utilizador utilizador = new Utilizador();
    //     utilizador.setId(4L);

    //     Ticket novoTicket = Ticket.builder()
    //             .titulo("Problema de rede")
    //             .descricao("Acesso à internet intermitente")
    //             .prioridade("ALTA")
    //             .tipo("INCIDENTE")
    //             .abertoPor(utilizador)
    //             .build();

    //     Ticket saved = ticketService.createTicket(novoTicket);
    //     System.out.println("\n=== TESTE ===");
    //     System.out.println("Ticket criado com ID: " + saved.getId());
    //     System.out.println("Técnico atribuído: " +
    //         (saved.getTecnico() != null ? saved.getTecnico().getUtilizador().getNome() : "Nenhum técnico disponível"));
    //     System.out.println("Estado final: " + saved.getEstado());
    // }
}