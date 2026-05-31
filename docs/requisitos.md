# Requisitos Funcionais e Não-Funcionais – Sistema ITSM

## Requisitos Funcionais (RF)

1. **RF01 – Gestão de Ativos**  
   Permitir registar, editar, listar e remover ativos de hardware/software (computadores, servidores, licenças, etc.). Cada ativo deve ter tipo, nome, estado (operacional, avariado, em manutenção) e especificações técnicas.

2. **RF02 – Gestão de Técnicos**  
   Permitir registar, editar, listar e remover técnicos. Cada técnico deve ter nome, email, competências (lista de tecnologias), disponibilidade horária (dias e horas de trabalho) e carga de trabalho atual (número de tickets ativos).

3. **RF03 – Abertura de Tickets**  
   Qualquer utilizador autenticado pode abrir um ticket, fornecendo título, descrição, prioridade (baixa, média, alta, crítica), tipo (incidente ou pedido de suporte) e ativo afetado (opcional). O sistema regista data/hora de abertura.

4. **RF04 – Atribuição Automática de Técnico**  
   No momento da criação do ticket, o sistema deve selecionar automaticamente o técnico mais adequado com base em três critérios:
    - **Competências**: o técnico deve possuir pelo menos uma competência correspondente à descrição do ticket.
    - **Disponibilidade horária**: o técnico deve estar disponível no horário atual (ou no próximo horário de trabalho).
    - **Carga de trabalho atual**: deve ser escolhido o técnico com menor carga de trabalho (heap mínimo).  
      A atribuição é registada no ticket.

5. **RF05 – Atualização do Estado do Ticket**  
   O técnico responsável pode alterar o estado do ticket (atribuído, em curso, resolvido, fechado) e adicionar comentários com a solução aplicada.

6. **RF06 – Listagem e Pesquisa**  
   Permitir listar tickets com filtros (por técnico, estado, data, prioridade) e ordenar por data ou prioridade. Permitir listar técnicos ordenados por carga de trabalho.

7. **RF07 – Persistência de Dados**  
   Todas as entidades (ativos, técnicos, tickets, comentários) devem ser armazenadas numa base de dados relacional (PostgreSQL) com integridade referencial.

8. **RF08 – API REST Documentada**  
   Expor endpoints para todas as operações CRUD e para a atribuição automática. A API deve ser documentada automaticamente via OpenAPI (Swagger UI).

9. **RF09 – Interface Operacional**  
   O sistema deve fornecer uma interface que permita executar todas as funcionalidades. Pode ser a própria API através do Swagger UI ou uma aplicação de consola (CLI).

## Requisitos Não-Funcionais (RNF)

1. **RNF01 – Desempenho**  
   O algoritmo de atribuição de técnico deve executar em menos de 1 segundo para até 1000 tickets abertos e 100 técnicos cadastrados.

2. **RNF02 – Segurança**  
   Implementar autenticação (login com email e palavra-passe) e autorização baseada em roles: ADMIN (gestão total), TECNICO (gerir tickets atribuídos), UTILIZADOR (apenas abrir tickets e consultar). As palavras-passe devem ser guardadas com hash BCrypt.

3. **RNF03 – Fiabilidade**  
   Os testes unitários devem cobrir pelo menos 70% das principais funcionalidades (atribuição, CRUD de tickets e técnicos). O sistema deve tratar exceções de forma global e retornar respostas HTTP apropriadas (400, 404, 500, etc.).

4. **RNF04 – Manutenibilidade**  
   O código deve seguir uma arquitetura de camadas (controller, service, repository) e respeitar princípios SOLID e Clean Code.

5. **RNF05 – Registo de Operações (Logs)**  
   Todas as atribuições de técnicos, alterações de estado e tentativas de acesso não autorizadas devem ser registadas em ficheiro de log com timestamp e nível (INFO, WARN, ERROR).

6. **RNF06 – Documentação**  
   O projeto deve incluir um manual de instalação e utilização (passo a passo para compilar, executar e testar) e documentação técnica da API e da arquitetura.