-- ============================================
-- SCHEMA - SISTEMA ITSM (PostgreSQL)
-- ============================================

-- ============================================
-- TABELA: UTILIZADOR
-- ============================================
CREATE TABLE IF NOT EXISTS utilizador (
                                          id BIGSERIAL PRIMARY KEY,
                                          username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
    );

-- ============================================
-- TABELA: TECNICO
-- ============================================
CREATE TABLE IF NOT EXISTS tecnico (
                                       id BIGSERIAL PRIMARY KEY,
                                       utilizador_id BIGINT UNIQUE NOT NULL,
                                       disponibilidade TEXT,
                                       carga_trabalho_atual INTEGER DEFAULT 0,
                                       CONSTRAINT fk_tecnico_utilizador FOREIGN KEY (utilizador_id)
    REFERENCES utilizador(id) ON DELETE CASCADE
    );

-- ============================================
-- TABELA: COMPETENCIA
-- ============================================
CREATE TABLE IF NOT EXISTS competencia (
                                           id BIGSERIAL PRIMARY KEY,
                                           nome VARCHAR(50) UNIQUE NOT NULL
    );

-- ============================================
-- TABELA: TECNICO_COMPETENCIA (N:N)
-- ============================================
CREATE TABLE IF NOT EXISTS tecnico_competencia (
                                                   tecnico_id BIGINT NOT NULL,
                                                   competencia_id BIGINT NOT NULL,
                                                   PRIMARY KEY (tecnico_id, competencia_id),
    CONSTRAINT fk_tc_tecnico FOREIGN KEY (tecnico_id)
    REFERENCES tecnico(id) ON DELETE CASCADE,
    CONSTRAINT fk_tc_competencia FOREIGN KEY (competencia_id)
    REFERENCES competencia(id) ON DELETE CASCADE
    );

-- ============================================
-- TABELA: ATIVO
-- ============================================
CREATE TABLE IF NOT EXISTS ativo (
                                     id BIGSERIAL PRIMARY KEY,
                                     tipo VARCHAR(50) NOT NULL,
    nome VARCHAR(100) NOT NULL,
    estado VARCHAR(30) NOT NULL,
    data_aquisicao DATE,
    especificacoes TEXT,
    created_at TIMESTAMP DEFAULT NOW()
    );

-- ============================================
-- TABELA: TICKET
-- ============================================
CREATE TABLE IF NOT EXISTS ticket (
                                      id BIGSERIAL PRIMARY KEY,
                                      titulo VARCHAR(100) NOT NULL,
    descricao TEXT NOT NULL,
    prioridade VARCHAR(20) NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    estado VARCHAR(30) NOT NULL,
    data_abertura TIMESTAMP NOT NULL,
    data_fecho TIMESTAMP,
    tecnico_id BIGINT,
    aberto_por_id BIGINT NOT NULL,
    ativo_id BIGINT,
    CONSTRAINT fk_ticket_tecnico FOREIGN KEY (tecnico_id)
    REFERENCES tecnico(id) ON DELETE SET NULL,
    CONSTRAINT fk_ticket_aberto_por FOREIGN KEY (aberto_por_id)
    REFERENCES utilizador(id) ON DELETE CASCADE,
    CONSTRAINT fk_ticket_ativo FOREIGN KEY (ativo_id)
    REFERENCES ativo(id) ON DELETE SET NULL
    );

-- ============================================
-- TABELA: COMENTARIO
-- ============================================
CREATE TABLE IF NOT EXISTS comentario (
                                          id BIGSERIAL PRIMARY KEY,
                                          texto TEXT NOT NULL,
                                          data_hora TIMESTAMP NOT NULL,
                                          ticket_id BIGINT NOT NULL,
                                          tecnico_id BIGINT NOT NULL,
                                          CONSTRAINT fk_comentario_ticket FOREIGN KEY (ticket_id)
    REFERENCES ticket(id) ON DELETE CASCADE,
    CONSTRAINT fk_comentario_tecnico FOREIGN KEY (tecnico_id)
    REFERENCES tecnico(id) ON DELETE CASCADE
    );

-- ============================================
-- ÍNDICES PARA PERFORMANCE
-- ============================================
CREATE INDEX idx_ticket_estado ON ticket(estado);
CREATE INDEX idx_ticket_prioridade ON ticket(prioridade);
CREATE INDEX idx_ticket_tecnico_id ON ticket(tecnico_id);
CREATE INDEX idx_ticket_aberto_por_id ON ticket(aberto_por_id);
CREATE INDEX idx_ticket_data_abertura ON ticket(data_abertura);
CREATE INDEX idx_tecnico_carga ON tecnico(carga_trabalho_atual);
CREATE INDEX idx_comentario_ticket_id ON comentario(ticket_id);