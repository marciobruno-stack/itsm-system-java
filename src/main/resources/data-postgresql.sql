-- ============================================
-- LIMPAR DADOS EXISTENTES (ordem correta para PostgreSQL)
-- ============================================
TRUNCATE TABLE comentario RESTART IDENTITY CASCADE;
TRUNCATE TABLE ticket RESTART IDENTITY CASCADE;
TRUNCATE TABLE tecnico_competencia RESTART IDENTITY CASCADE;
TRUNCATE TABLE tecnico RESTART IDENTITY CASCADE;
TRUNCATE TABLE utilizador RESTART IDENTITY CASCADE;
TRUNCATE TABLE ativo RESTART IDENTITY CASCADE;
TRUNCATE TABLE competencia RESTART IDENTITY CASCADE;

-- ============================================
-- RESET SEQUENCES (PostgreSQL)
-- ============================================
ALTER SEQUENCE utilizador_id_seq RESTART WITH 1;
ALTER SEQUENCE tecnico_id_seq RESTART WITH 1;
ALTER SEQUENCE ticket_id_seq RESTART WITH 1;
ALTER SEQUENCE ativo_id_seq RESTART WITH 1;
ALTER SEQUENCE competencia_id_seq RESTART WITH 1;
ALTER SEQUENCE comentario_id_seq RESTART WITH 1;

-- ============================================
-- UTILIZADORES (password: password123)
-- BCrypt: $2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG
-- ============================================
INSERT INTO utilizador (id, username, password_hash, nome, email, role, created_at) VALUES
                                                                                        (1, 'admin', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Administrador', 'admin@itsm.com', 'ADMIN', NOW()),
                                                                                        (2, 'joao_tec', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'João Técnico', 'joao@itsm.com', 'TECNICO', NOW()),
                                                                                        (3, 'maria_tec', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Maria Silva', 'maria@itsm.com', 'TECNICO', NOW()),
                                                                                        (4, 'carlos_user', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Carlos User', 'carlos@itsm.com', 'UTILIZADOR', NOW());

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
-- TÉCNICOS
-- ============================================
INSERT INTO tecnico (id, utilizador_id, disponibilidade, carga_trabalho_atual) VALUES
                                                                                   (1, 2, '{"segunda":["09:00-18:00"],"terca":["09:00-18:00"],"quarta":["09:00-18:00"],"quinta":["09:00-18:00"],"sexta":["09:00-18:00"]}', 0),
                                                                                   (2, 3, '{"segunda":["09:00-18:00"],"terca":["09:00-18:00"],"quarta":["09:00-18:00"],"quinta":["09:00-18:00"],"sexta":["09:00-18:00"]}', 0);

-- ============================================
-- TÉCNICO-COMPETÊNCIAS
-- ============================================
INSERT INTO tecnico_competencia (tecnico_id, competencia_id) VALUES
                                                                 (1, 1), -- João → Redes
                                                                 (1, 2), -- João → Windows Server
                                                                 (2, 3), -- Maria → Linux
                                                                 (2, 4); -- Maria → SQL

-- ============================================
-- ATIVOS
-- ============================================
INSERT INTO ativo (id, tipo, nome, estado, data_aquisicao, especificacoes, created_at) VALUES
                                                                                           (1, 'SERVIDOR', 'Servidor Base de Dados', 'OPERACIONAL', '2023-01-10', '32GB RAM, 4 vCPUs, 1TB SSD', NOW()),
                                                                                           (2, 'COMPUTADOR', 'Portátil João', 'OPERACIONAL', '2024-02-15', '16GB RAM, 512GB SSD, i7', NOW()),
                                                                                           (3, 'SWITCH', 'Switch Core', 'OPERACIONAL', '2023-06-01', '48 portas Gigabit', NOW());

-- ============================================
-- TICKETS (com estado ABERTO para teste de atribuição)
-- ============================================
INSERT INTO ticket (id, titulo, descricao, prioridade, tipo, estado, data_abertura, data_fecho, tecnico_id, aberto_por_id, ativo_id) VALUES
                                                                                                                                         (1, 'Problema de rede', 'Acesso à internet intermitente no setor administrativo', 'ALTA', 'INCIDENTE', 'ABERTO', NOW(), NULL, NULL, 4, 3),
                                                                                                                                         (2, 'Instalar software', 'Instalar Microsoft Office no computador do João', 'MEDIA', 'PEDIDO', 'ABERTO', NOW(), NULL, NULL, 4, 2),
                                                                                                                                         (3, 'Servidor lento', 'Servidor de base de dados está com performance reduzida', 'CRITICA', 'INCIDENTE', 'ABERTO', NOW(), NULL, NULL, 4, 1);