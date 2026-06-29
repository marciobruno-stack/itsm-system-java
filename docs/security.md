# Segurança - Sistema ITSM

## Autenticação
- Spring Security com BCrypt
- Login: /login
- Credenciais: admin/admin123

## Autorização (Roles)
| Role | Permissões |
|------|------------|
| ADMIN | Gestão total (utilizadores, técnicos, tickets, ativos) |
| TECNICO | Gerir tickets atribuídos, atualizar estado |
| UTILIZADOR | Abrir tickets, consultar |

## Proteções
- CSRF desativado (para simplificar API)
- Passwords com hash BCrypt
- Sessões HTTP

## Vulnerabilidades Identificadas
1. CSRF: Desativado por simplicidade (API REST)
2. XSS: Prevenção com Thymeleaf (escaping automático)
3. SQL Injection: Prevenção com JPA/JPQL
4. Exposição de dados sensíveis: Logs em modo desenvolvimento

## Melhorias Futuras
1. JWT para autenticação stateless
2. Rate limiting para prevenir ataques de força bruta
3. HTTPS em produção
4. Auditoria detalhada com Spring Data Envers