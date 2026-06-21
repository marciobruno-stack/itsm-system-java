-- ======================================================
-- LIMPAR DADOS EXISTENTES (evita violação de chaves)
-- ======================================================
DELETE FROM comentario;
DELETE FROM ticket;
DELETE FROM tecnico_competencia;
DELETE FROM tecnico;
DELETE FROM utilizador;
DELETE FROM competencia;
DELETE FROM ativo;

-- ======================================================
-- INSERIR UTILIZADORES
-- ======================================================
INSERT INTO utilizador (username, password_hash, nome, email, role) VALUES
                                                                        ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMy.Mrq9ZqWU4lKJvJXcQvLQ5c6v7M8n9O', 'Administrador', 'admin@itsm.com', 'ADMIN'),
                                                                        ('joao_tec', '$2a$10$N9qo8uLOickgx2ZMRZoMy.Mrq9ZqWU4lKJvJXcQvLQ5c6v7M8n9O', 'João Técnico', 'joao@itsm.com', 'TECNICO'),
                                                                        ('maria_tec', '$2a$10$N9qo8uLOickgx2ZMRZoMy.Mrq9ZqWU4lKJvJXcQvLQ5c6v7M8n9O', 'Maria Silva', 'maria@itsm.com', 'TECNICO'),
                                                                        ('carlos_user', '$2a$10$N9qo8uLOickgx2ZMRZoMy.Mrq9ZqWU4lKJvJXcQvLQ5c6v7M8n9O', 'Carlos User', 'carlos@itsm.com', 'UTILIZADOR');

-- ======================================================
-- INSERIR TÉCNICOS (com disponibilidade incluindo DOMINGO)
-- ======================================================
INSERT INTO tecnico (utilizador_id, disponibilidade, carga_trabalho_atual) VALUES
                                                                               (2, '{"segunda":["09:00-18:00"],"terca":["09:00-18:00"],"quarta":["09:00-18:00"],"domingo":["09:00-18:00"]}', 0),
                                                                               (3, '{"segunda":["10:00-19:00"],"quinta":["10:00-19:00"],"sexta":["10:00-19:00"],"domingo":["10:00-16:00"]}', 0);

-- ======================================================
-- INSERIR COMPETÊNCIAS
-- ======================================================
INSERT INTO competencia (nome) VALUES ('Redes'), ('Windows Server'), ('Linux'), ('SQL'), ('Java');

-- ======================================================
-- ASSOCIAR COMPETÊNCIAS A TÉCNICOS
-- ======================================================
INSERT INTO tecnico_competencia (tecnico_id, competencia_id) VALUES
                                                                 (1, 1), (1, 2),   -- João: Redes, Windows Server
                                                                 (2, 3), (2, 4), (2, 5); -- Maria: Linux, SQL, Java

-- ======================================================
-- INSERIR ATIVOS
-- ======================================================
INSERT INTO ativo (tipo, nome, estado, data_aquisicao, especificacoes) VALUES
                                                                           ('SERVIDOR', 'Servidor de Base de Dados', 'OPERACIONAL', '2023-01-10', '32GB RAM, 4 vCPUs'),
                                                                           ('COMPUTADOR', 'Portátil João', 'OPERACIONAL', '2023-05-20', '16GB RAM, SSD 512GB'),
                                                                           ('SWITCH', 'Switch Core', 'AVARIADO', '2022-11-01', '48 portas gigabit');

-- ======================================================
-- INSERIR TICKETS INICIAIS
-- ======================================================
INSERT INTO ticket (titulo, descricao, prioridade, tipo, estado, data_abertura, tecnico_id, aberto_por_id, ativo_id) VALUES
                                                                                                                         ('Problema de rede', 'Acesso à internet intermitente', 'ALTA', 'INCIDENTE', 'ATRIBUIDO', CURRENT_TIMESTAMP, 1, 4, 3),
                                                                                                                         ('Instalar software', 'Necessito do pacote Office no PC', 'MEDIA', 'PEDIDO', 'ABERTO', CURRENT_TIMESTAMP, NULL, 4, 2),
                                                                                                                         ('Servidor lento', 'Base de dados está com resposta lenta', 'CRITICA', 'INCIDENTE', 'EM_CURSO', CURRENT_TIMESTAMP, 2, 1, 1);

-- ======================================================
-- INSERIR COMENTÁRIO INICIAL
-- ======================================================
INSERT INTO comentario (texto, data_hora, ticket_id, tecnico_id) VALUES
    ('Estou a analisar a configuração do switch', CURRENT_TIMESTAMP, 1, 1);