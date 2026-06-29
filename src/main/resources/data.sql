-- ============================================
-- LIMPAR DADOS EXISTENTES
-- ============================================
TRUNCATE TABLE comentario RESTART IDENTITY CASCADE;
TRUNCATE TABLE ticket RESTART IDENTITY CASCADE;
TRUNCATE TABLE tecnico_competencia RESTART IDENTITY CASCADE;
TRUNCATE TABLE tecnico RESTART IDENTITY CASCADE;
TRUNCATE TABLE utilizador RESTART IDENTITY CASCADE;
TRUNCATE TABLE ativo RESTART IDENTITY CASCADE;
TRUNCATE TABLE competencia RESTART IDENTITY CASCADE;

-- ============================================
-- RESET SEQUENCES
-- ============================================
ALTER SEQUENCE utilizador_id_seq RESTART WITH 9;
ALTER SEQUENCE tecnico_id_seq RESTART WITH 15;
ALTER SEQUENCE ticket_id_seq RESTART WITH 1;
ALTER SEQUENCE ativo_id_seq RESTART WITH 1;
ALTER SEQUENCE competencia_id_seq RESTART WITH 1;

-- ============================================
-- UTILIZADORES
-- ============================================
INSERT INTO utilizador (id, username, password_hash, nome, email, role, created_at) VALUES
                                                                                        (9, 'admin', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Administrador', 'admin@itsm.com', 'ADMIN', NOW()),
                                                                                        (12, 'tecnico1', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Joao Tecnico', 'joao@itsm.com', 'TECNICO', NOW()),
                                                                                        (13, 'tecnico2', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Maria Tecnico', 'maria@itsm.com', 'TECNICO', NOW()),
                                                                                        (14, 'tecnico3', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Carlos Tecnico', 'carlos@itsm.com', 'TECNICO', NOW());

-- ============================================
-- COMPETÊNCIAS
-- ============================================
INSERT INTO competencia (id, nome) VALUES
                                       (1, 'Redes'),
                                       (2, 'Windows Server'),
                                       (3, 'Linux'),
                                       (4, 'SQL'),
                                       (5, 'Java');

-- ============================================
-- TÉCNICOS (IDs alinhados com utilizadores)
-- ============================================
INSERT INTO tecnico (id, utilizador_id, disponibilidade, carga_trabalho_atual) VALUES
                                                                                   (15, 12, '{"segunda":["09:00-18:00"],"terca":["09:00-18:00"],"quarta":["09:00-18:00"],"quinta":["09:00-18:00"],"sexta":["09:00-18:00"]}', 0),
                                                                                   (16, 13, '{"segunda":["09:00-18:00"],"terca":["09:00-18:00"],"quarta":["09:00-18:00"],"quinta":["09:00-18:00"],"sexta":["09:00-18:00"]}', 0),
                                                                                   (17, 14, '{"segunda":["09:00-18:00"],"terca":["09:00-18:00"],"quarta":["09:00-18:00"],"quinta":["09:00-18:00"],"sexta":["09:00-18:00"]}', 0);

-- ============================================
-- TÉCNICO-COMPETÊNCIAS
-- ============================================
INSERT INTO tecnico_competencia (tecnico_id, competencia_id) VALUES
                                                                 (15, 1), -- Joao → Redes
                                                                 (15, 2), -- Joao → Windows Server
                                                                 (16, 3), -- Maria → Linux
                                                                 (16, 4), -- Maria → SQL
                                                                 (17, 5); -- Carlos → Java

-- ============================================
-- ATIVOS
-- ============================================
INSERT INTO ativo (id, tipo, nome, estado, data_aquisicao, especificacoes, created_at) VALUES
                                                                                           (1, 'SERVIDOR', 'Servidor Base de Dados', 'OPERACIONAL', '2023-01-10', '32GB RAM, 4 vCPUs, 1TB SSD', NOW()),
                                                                                           (2, 'COMPUTADOR', 'Portatil Joao', 'OPERACIONAL', '2024-02-15', '16GB RAM, 512GB SSD, i7', NOW()),
                                                                                           (3, 'SWITCH', 'Switch Core', 'OPERACIONAL', '2023-06-01', '48 portas Gigabit', NOW());

-- ============================================
-- TICKETS (com estado ABERTO)
-- ============================================
INSERT INTO ticket (id, titulo, descricao, prioridade, tipo, estado, data_abertura, data_fecho, tecnico_id, aberto_por_id, ativo_id) VALUES
    (1, 'Problema de rede', 'Acesso à internet intermitente no setor administrativo', 'ALTA', 'INCIDENTE', 'ABERTO', NOW(), NULL, NULL, 9, 3);