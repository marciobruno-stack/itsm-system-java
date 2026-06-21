package com.itsm.incidentmanagement.console;

import com.itsm.incidentmanagement.model.entity.*;
import com.itsm.incidentmanagement.service.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

@Component
public class ConsoleApplication implements CommandLineRunner {

    private final TicketService ticketService;
    private final TecnicoService tecnicoService;
    private final AtivoService ativoService;
    private final CompetenciaService competenciaService;
    private final UtilizadorService utilizadorService;

    public ConsoleApplication(TicketService ticketService,
                              TecnicoService tecnicoService,
                              AtivoService ativoService,
                              CompetenciaService competenciaService,
                              UtilizadorService utilizadorService) {
        this.ticketService = ticketService;
        this.tecnicoService = tecnicoService;
        this.ativoService = ativoService;
        this.competenciaService = competenciaService;
        this.utilizadorService = utilizadorService;
    }

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("=".repeat(60));
        System.out.println("  SISTEMA ITSM - GESTÃO DE INFRAESTRUTURAS E INCIDENTES");
        System.out.println("=".repeat(60));

        while (running) {
            System.out.println("\n--- MENU PRINCIPAL ---");
            System.out.println("1. Listar Técnicos");
            System.out.println("2. Listar Tickets");
            System.out.println("3. Criar Ticket (com atribuição automática)");
            System.out.println("4. Atualizar Estado de Ticket");
            System.out.println("5. Listar Ativos");
            System.out.println("6. Listar Competências");
            System.out.println("7. Criar Técnico");
            System.out.println("8. Sair");
            System.out.print("Opção: ");

            String option = scanner.nextLine().trim();

            switch (option) {
                case "1" -> listarTecnicos();
                case "2" -> listarTickets();
                case "3" -> criarTicket(scanner);
                case "4" -> atualizarEstadoTicket(scanner);
                case "5" -> listarAtivos();
                case "6" -> listarCompetencias();
                case "7" -> criarTecnico(scanner);
                case "8" -> {
                    running = false;
                    System.out.println("Saindo...");
                }
                default -> System.out.println("Opção inválida!");
            }
        }
        scanner.close();
    }

    private void listarTecnicos() {
        System.out.println("\n--- TÉCNICOS ---");
        List<Tecnico> tecnicos = tecnicoService.findAll();
        if (tecnicos.isEmpty()) {
            System.out.println("Nenhum técnico registado.");
            return;
        }
        tecnicos.forEach(t -> System.out.printf(
                "ID: %d | Nome: %s | Carga: %d | Competências: %d%n",
                t.getId(),
                t.getUtilizador().getNome(),
                t.getCargaTrabalhoAtual(),
                t.getCompetencias().size()
        ));
    }

    private void listarTickets() {
        System.out.println("\n--- TICKETS ---");
        List<Ticket> tickets = ticketService.findAll();
        if (tickets.isEmpty()) {
            System.out.println("Nenhum ticket registado.");
            return;
        }
        tickets.forEach(t -> System.out.printf(
                "ID: %d | Título: %s | Estado: %s | Prioridade: %s | Técnico: %s%n",
                t.getId(),
                t.getTitulo(),
                t.getEstado(),
                t.getPrioridade(),
                t.getTecnico() != null ? t.getTecnico().getUtilizador().getNome() : "Não atribuído"
        ));
    }

    private void criarTicket(Scanner scanner) {
        System.out.println("\n--- NOVO TICKET ---");

        System.out.print("Título: ");
        String titulo = scanner.nextLine();
        if (titulo.isBlank()) {
            System.out.println("❌ Título é obrigatório!");
            return;
        }

        System.out.print("Descrição: ");
        String descricao = scanner.nextLine();
        if (descricao.isBlank()) {
            System.out.println("❌ Descrição é obrigatória!");
            return;
        }

        System.out.print("Prioridade (BAIXA/MEDIA/ALTA/CRITICA): ");
        String prioridade = scanner.nextLine().trim().toUpperCase();
        if (prioridade.isBlank()) {
            System.out.println("❌ Prioridade é obrigatória!");
            return;
        }
        if (!prioridade.matches("BAIXA|MEDIA|ALTA|CRITICA")) {
            System.out.println("❌ Prioridade inválida! Use: BAIXA, MEDIA, ALTA ou CRITICA");
            return;
        }

        System.out.print("Tipo (INCIDENTE/PEDIDO): ");
        String tipo = scanner.nextLine().trim().toUpperCase();
        if (tipo.isBlank()) {
            System.out.println("❌ Tipo é obrigatório!");
            return;
        }
        if (!tipo.matches("INCIDENTE|PEDIDO")) {
            System.out.println("❌ Tipo inválido! Use: INCIDENTE ou PEDIDO");
            return;
        }

        System.out.print("ID do Utilizador que abre (ex: 4 para Carlos User): ");
        String idUtilizadorStr = scanner.nextLine().trim();
        if (idUtilizadorStr.isBlank()) {
            System.out.println("❌ ID do utilizador é obrigatório!");
            return;
        }
        Long utilizadorId = Long.parseLong(idUtilizadorStr);

        System.out.print("ID do Ativo (opcional, 0 para nenhum): ");
        String idAtivoStr = scanner.nextLine().trim();
        Long ativoId = idAtivoStr.isBlank() ? 0 : Long.parseLong(idAtivoStr);

        Utilizador utilizador = new Utilizador();
        utilizador.setId(utilizadorId);

        Ticket ticket = Ticket.builder()
                .titulo(titulo)
                .descricao(descricao)
                .prioridade(prioridade)
                .tipo(tipo)
                .abertoPor(utilizador)
                .build();

        if (ativoId > 0) {
            Ativo ativo = new Ativo();
            ativo.setId(ativoId);
            ticket.setAtivo(ativo);
        }

        Ticket created = ticketService.createTicket(ticket);
        System.out.println("\n✅ Ticket criado com ID: " + created.getId());
        System.out.println("   Estado: " + created.getEstado());
        System.out.println("   Técnico: " + (created.getTecnico() != null ?
                created.getTecnico().getUtilizador().getNome() : "Nenhum técnico disponível"));
    }

    private void atualizarEstadoTicket(Scanner scanner) {
        System.out.println("\n--- ATUALIZAR ESTADO DO TICKET ---");
        System.out.print("ID do Ticket: ");
        String idStr = scanner.nextLine().trim();
        if (idStr.isBlank()) {
            System.out.println("❌ ID é obrigatório!");
            return;
        }
        Long id = Long.parseLong(idStr);

        Ticket ticket = ticketService.findById(id);
        if (ticket == null) {
            System.out.println("❌ Ticket não encontrado!");
            return;
        }

        System.out.print("Novo estado (ABERTO/ATRIBUIDO/EM_CURSO/RESOLVIDO/FECHADO): ");
        String estado = scanner.nextLine().trim().toUpperCase();
        if (estado.isBlank()) {
            System.out.println("❌ Estado é obrigatório!");
            return;
        }

        try {
            Ticket updated = ticketService.updateEstado(id, estado);
            System.out.println("✅ Estado atualizado para: " + updated.getEstado());
        } catch (RuntimeException e) {
            System.out.println("❌ " + e.getMessage());
        }
    }

    private void listarAtivos() {
        System.out.println("\n--- ATIVOS ---");
        List<Ativo> ativos = ativoService.findAll();
        if (ativos.isEmpty()) {
            System.out.println("Nenhum ativo registado.");
            return;
        }
        ativos.forEach(a -> System.out.printf(
                "ID: %d | Nome: %s | Tipo: %s | Estado: %s%n",
                a.getId(), a.getNome(), a.getTipo(), a.getEstado()
        ));
    }

    private void listarCompetencias() {
        System.out.println("\n--- COMPETÊNCIAS ---");
        List<Competencia> competencias = competenciaService.findAll();
        if (competencias.isEmpty()) {
            System.out.println("Nenhuma competência registada.");
            return;
        }
        competencias.forEach(c -> System.out.printf(
                "ID: %d | Nome: %s%n", c.getId(), c.getNome()
        ));
    }

    /**
     * NOVO MÉTODO: Criar técnico
     */
    private void criarTecnico(Scanner scanner) {
        System.out.println("\n--- CRIAR TÉCNICO ---");

        // Listar utilizadores disponíveis
        System.out.println("\n📋 Utilizadores disponíveis:");
        List<Utilizador> utilizadores = utilizadorService.findAll();
        utilizadores.forEach(u -> System.out.printf(
                "ID: %d | Nome: %s | Role: %s%n",
                u.getId(), u.getNome(), u.getRole()
        ));

        System.out.print("\nID do Utilizador para associar ao técnico: ");
        String idUtilizadorStr = scanner.nextLine().trim();
        if (idUtilizadorStr.isBlank()) {
            System.out.println("❌ ID do utilizador é obrigatório!");
            return;
        }
        Long utilizadorId = Long.parseLong(idUtilizadorStr);

        Utilizador utilizador = utilizadorService.findById(utilizadorId);
        if (utilizador == null) {
            System.out.println("❌ Utilizador não encontrado!");
            return;
        }

        if (!"TECNICO".equals(utilizador.getRole())) {
            System.out.println("⚠️  Este utilizador não tem role TECNICO. O técnico será criado na mesma, mas o role não está correto.");
        }

        System.out.print("Disponibilidade (JSON, ex: {\"segunda\":[\"09:00-18:00\"],\"terca\":[\"09:00-18:00\"]}): ");
        String disponibilidade = scanner.nextLine().trim();
        if (disponibilidade.isBlank()) {
            disponibilidade = "{}"; // JSON vazio
        }

        // Listar competências disponíveis
        System.out.println("\n📋 Competências disponíveis:");
        List<Competencia> competencias = competenciaService.findAll();
        competencias.forEach(c -> System.out.printf(
                "ID: %d | Nome: %s%n", c.getId(), c.getNome()
        ));

        System.out.print("\nIDs das Competências (separados por vírgula, ex: 1,2,3) - Enter para nenhuma: ");
        String competenciasStr = scanner.nextLine().trim();

        // Criar técnico
        Tecnico tecnico = new Tecnico();
        tecnico.setUtilizador(utilizador);
        tecnico.setDisponibilidade(disponibilidade);
        tecnico.setCargaTrabalhoAtual(0);

        // Adicionar competências
        Set<Competencia> competenciasSet = new HashSet<>();
        if (!competenciasStr.isBlank()) {
            String[] ids = competenciasStr.split(",");
            for (String idStr : ids) {
                try {
                    Long id = Long.parseLong(idStr.trim());
                    Competencia comp = competenciaService.findById(id);
                    if (comp != null) {
                        competenciasSet.add(comp);
                    } else {
                        System.out.println("⚠️  Competência ID " + id + " não encontrada, ignorando.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("⚠️  ID inválido: '" + idStr + "', ignorando.");
                }
            }
        }
        tecnico.setCompetencias(competenciasSet);

        try {
            Tecnico created = tecnicoService.create(tecnico);
            System.out.println("\n✅ Técnico criado com sucesso!");
            System.out.println("   ID: " + created.getId());
            System.out.println("   Nome: " + created.getUtilizador().getNome());
            System.out.println("   Competências: " + created.getCompetencias().size());
        } catch (Exception e) {
            System.out.println("❌ Erro ao criar técnico: " + e.getMessage());
        }
    }
}