# Arquitetura do Sistema ITSM

## Visão Geral

O sistema é uma aplicação Spring Boot que implementa um sistema de gestão de infraestruturas e incidentes (ITSM). Foi desenvolvido com foco em escalabilidade, segurança e facilidade de manutenção.

---

## Tecnologias Utilizadas

| Tecnologia | Descrição |
|------------|-----------|
| **Java 21 LTS** | Linguagem de programação |
| **Spring Boot 3.5.14** | Framework principal |
| **Spring Data JPA (Hibernate)** | Persistência de dados |
| **Spring Security + JWT** | Autenticação e autorização |
| **H2 Database** | Base de dados em memória (desenvolvimento) |
| **PostgreSQL** | Base de dados (produção) |
| **Maven** | Gestão de dependências |
| **Swagger / OpenAPI** | Documentação da API |
| **JUnit 5 + Mockito** | Testes unitários |
| **SLF4J + Logback** | Logs e auditoria |
| **Lombok** | Redução de código boilerplate |
| **Thymeleaf** | Templates HTML (interface web) |
| **Bootstrap** | Estilização da interface web |

---

## Estrutura de Camadas

```
┌─────────────────────────────────────────────────────────────┐
│                    Camada de Apresentação                   │
│  ┌─────────────┐  ┌─────────────┐  ┌───────────────────┐ │
│  │   REST API  │  │   Consola   │  │   Interface Web   │ │
│  │ (Controllers)│  │   (CLI)    │  │   (Thymeleaf)    │ │
│  └─────────────┘  └─────────────┘  └───────────────────┘ │
├─────────────────────────────────────────────────────────────┤
│                     Camada de Serviço                       │
│  ┌─────────────┐  ┌─────────────┐  ┌───────────────────┐ │
│  │ TicketServ. │  │  CacheServ. │  │   AuditService   │ │
│  │ AssignServ. │  │  JwtService │  │   TecnicoServ.   │ │
│  └─────────────┘  └─────────────┘  └───────────────────┘ │
├─────────────────────────────────────────────────────────────┤
│                     Camada de Dados                         │
│  ┌─────────────┐  ┌─────────────┐  ┌───────────────────┐ │
│  │  Repositó-  │  │  Entidades  │  │  H2 / PostgreSQL  │ │
│  │   rios JPA  │  │   JPA      │  │                   │ │
│  └─────────────┘  └─────────────┘  └───────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

---

## Componentes Principais

### 1. Controladores (REST API)

| Controller | Endpoint Base | Descrição |
|------------|---------------|-----------|
| `AuthController` | `/api/auth` | Autenticação (login / register) |
| `TicketController` | `/api/tickets` | Gestão de tickets |
| `TecnicoController` | `/api/tecnicos` | Gestão de técnicos |
| `AtivoController` | `/api/ativos` | Gestão de ativos |
| `CompetenciaController` | `/api/competencias` | Gestão de competências |
| `WebController` | `/`, `/admin`, `/tecnico`, `/utilizador` | Interface web |

### 2. Serviços (Lógica de Negócio)

| Serviço | Responsabilidade |
|---------|------------------|
| `TicketService` | CRUD de tickets, atribuição automática |
| `TicketAssignmentService` | Algoritmo de atribuição com PriorityQueue |
| `CacheService` | Cache em memória (técnicos e tickets) |
| `AuditService` | Logs de auditoria |
| `JwtService` | Geração e validação de tokens JWT |
| `TecnicoService` | CRUD de técnicos |
| `AtivoService` | CRUD de ativos |
| `CompetenciaService` | CRUD de competências |
| `UtilizadorService` | CRUD de utilizadores |

### 3. Repositórios (Acesso a Dados)

| Repositório | Entidade |
|-------------|----------|
| `TicketRepository` | Ticket |
| `TecnicoRepository` | Tecnico |
| `AtivoRepository` | Ativo |
| `CompetenciaRepository` | Competencia |
| `UtilizadorRepository` | Utilizador |

### 4. Entidades (Modelo de Dados)

| Entidade | Descrição |
|----------|-----------|
| `Utilizador` | Utilizador do sistema |
| `Tecnico` | Técnico (relação 1:1 com Utilizador) |
| `Ticket` | Ticket de incidente / pedido |
| `Ativo` | Ativo de infraestrutura |
| `Competencia` | Competência de um técnico |
| `Comentario` | Comentários em tickets |

---

## Fluxo de Criação de Ticket

```
1. Utilizador cria ticket (API / Consola / Web)
   ↓
2. Ticket é persistido com estado "ABERTO"
   ↓
3. Algoritmo de atribuição é executado:
   ├── Busca técnicos em cache (CacheService)
   ├── Filtra por competências (matching de palavras-chave)
   ├── Filtra por disponibilidade horária
   └── Usa PriorityQueue (Heap) para escolher técnico com menor carga
   ↓
4. Ticket é atualizado para "ATRIBUIDO" e associado ao técnico
   ↓
5. Log de auditoria é registado (AuditService)
```

---

## Segurança (Spring Security + JWT)

```
┌─────────────────────────────────────────────────────┐
│                   Segurança                         │
│  ┌─────────────────────────────────────────────────┐│
│  │              JWT Authentication Filter          ││
│  │     (Valida token em cada requisição)          ││
│  └─────────────────────────────────────────────────┘│
│  ┌─────────────────────────────────────────────────┐│
│  │              SecurityConfig                     ││
│  │  - Configuração de permissões por role         ││
│  │  - BCrypt para encriptação de passwords        ││
│  │  - Stateless (sem sessão)                     ││
│  └─────────────────────────────────────────────────┘│
│  ┌─────────────────────────────────────────────────┐│
│  │              Roles                              ││
│  │  - ADMIN   → Acesso total                      ││
│  │  - TECNICO → Acesso a tickets e técnicos       ││
│  │  - UTILIZADOR → Apenas seus próprios tickets   ││
│  └─────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────┘
```

---

## Interface Web (Thymeleaf + Bootstrap)

| Perfil | Páginas | Funcionalidades |
|--------|---------|-----------------|
| **ADMIN** | Dashboard, Utilizadores, Técnicos, Tickets | Gestão completa do sistema |
| **TECNICO** | Dashboard, Meus Tickets | Visualização e atualização de tickets atribuídos |
| **UTILIZADOR** | Dashboard, Meus Tickets, Novo Ticket | Criação e acompanhamento de tickets |

---

## Diagrama de Classes (Simplificado)

```
┌────────────────┐          ┌────────────────┐
│   Utilizador   │ 1      1 │    Tecnico     │
├────────────────┤─────────├────────────────┤
│ id: Long       │         │ id: Long       │
│ username: Str  │         │ utilizador: U  │
│ password: Str  │         │ disponibilidade│
│ role: Str      │         │ cargaTrabalho  │
│ nome: Str      │         │ competencias   │
│ email: Str     │         └────────────────┘
└────────────────┘                  ↑
↑                           │
│                           │
│ 1                         │ N
┌───────┴────────┐          ┌──────┴────────────┐
│    Ticket      │ N      1 │   Competencia     │
├────────────────┤─────────├────────────────────┤
│ id: Long       │         │ id: Long           │
│ titulo: Str    │         │ nome: Str          │
│ descricao: Str │         └────────────────────┘
│ prioridade     │
│ tipo           │
│ estado         │
│ dataAbertura   │
│ dataFecho      │
│ tecnico: T     │
│ abertoPor: U   │
│ ativo: A       │
└────────────────┘
```

---

## Endpoints da API (Principais)

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/auth/login` | Login (retorna JWT) |
| POST | `/api/auth/register` | Registar utilizador |
| GET | `/api/tickets` | Listar todos os tickets |
| POST | `/api/tickets` | Criar ticket |
| GET | `/api/tecnicos` | Listar técnicos |
| GET | `/api/tecnicos/ordenados-carga` | Técnicos por carga de trabalho |
| GET | `/api/ativos` | Listar ativos |
| GET | `/api/competencias` | Listar competências |

---

## Diagrama de Sequência (Atribuição de Ticket)

```
Utilizador    Controller    TicketService    AssignmentService    CacheService    Repository
│             │              │                  │                 │             │
│──POST /tickets→│              │                  │                 │             │
│             │──createTicket──→│                  │                 │             │
│             │              │──saveTicket───────→│                 │             │
│             │              │                  │                 │             │
│             │              │──assignTechnician→│                 │             │
│             │              │                  │──getAllTecnicos──→│             │
│             │              │                  │                 │             │
│             │              │                  │──filtrar competências             │
│             │              │                  │──PriorityQueue   │             │
│             │              │                  │                 │             │
│             │              │←──tecnico escolhido─│                 │             │
│             │              │                  │                 │             │
│             │              │──updateTicket────→│                 │             │
│             │              │                  │──updateCache────→│             │
│             │              │                  │                 │             │
│             │←──ticket criado─│                  │                 │             │
│←──Resposta  │              │                  │                 │             │
```

---

## Otimizações Implementadas

| Otimização | Descrição | Benefício |
|------------|-----------|-----------|
| **Cache em Memória** | `CacheService` com `ConcurrentHashMap` | Reduz consultas à BD |
| **PriorityQueue (Heap)** | Algoritmo de atribuição O(log n) | Escolha eficiente do técnico |
| **Lazy Loading** | `@ManyToMany(fetch = FetchType.LAZY)` | Reduz uso de memória |
| **JPA Query Methods** | Métodos derivados (`findByEstado`) | Consultas SQL otimizadas |
| **Logs Assíncronos** | Logback com `AsyncAppender` | Melhor performance de escrita |

---

## Resumo dos Módulos

| Módulo | Descrição | Estado |
|--------|-----------|--------|
| 1 | Análise e requisitos | ✅ Concluído |
| 2 | Modelação e desenho | ✅ Concluído |
| 3 | Infraestrutura e algoritmos | ✅ Concluído |
| 4 | Implementação funcional | ✅ Concluído |
| 5 | Segurança e testes | ✅ Concluído |
| 6 | Otimização e documentação | ✅ Concluído |
