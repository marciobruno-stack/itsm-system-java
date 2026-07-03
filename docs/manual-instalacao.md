# Manual de Instalação e Utilização — Sistema ITSM

## Índice

1. Pré-requisitos
2. Instalação
3. Configuração
4. Execução
5. Uso (Web / API / Consola)
6. Credenciais de teste
7. Resolução de problemas
8. Suporte

---

## 1. Pré-requisitos

Antes de começar, instale as ferramentas abaixo:

- Java JDK 21 (ou superior)
- Maven 3.8+ (ou use o wrapper `./mvnw` incluído)
- Git 2.x
- PostgreSQL 15+ (opcional — recomendado para produção)
- IDE (opcional): IntelliJ IDEA, VS Code, etc.

Verifique as versões:

```bash
java -version        # Ex.: openjdk version "21.0.x"
mvn -version         # Ex.: Apache Maven 3.8.x
git --version        # Ex.: git version 2.x
```

Se usar o wrapper do projeto, prefira `./mvnw` (Linux/Mac) ou `mvnw.cmd` (Windows).

---

## 2. Instalação

1. Clone o repositório:

```bash
git clone https://github.com/marciobruno-stack/itsm-system-java.git
cd itsm-system-java
```

2. Compile o projeto:

```bash
# Unix / macOS
./mvnw clean compile

# Windows (PowerShell ou CMD)
# mvnw.cmd clean compile
```

3. (Opcional) Executar testes:

```bash
./mvnw test
```

4. (Opcional) Gerar JAR executável:

```bash
./mvnw package
```

O JAR será criado em `target/` (ex.: `incidentmanagement-0.0.1-SNAPSHOT.jar`).

---

## 3. Configuração

O ficheiro principal é `src/main/resources/application.properties`.

Exemplos de configuração:

### 3.1 Desenvolvimento (H2 — em memória)

```properties
# Datasource H2
spring.datasource.url=jdbc:h2:mem:itsm_db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# H2 console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JWT (exemplo — use variável de ambiente em produção)
jwt.secret=CHANGE_ME_POR_FAVOR
jwt.expiration=86400000
```

### 3.2 Produção (PostgreSQL)

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/itsm_db
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.username=itsm_user
spring.datasource.password=your_password

spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# JWT
jwt.secret=${JWT_SECRET:CHANGE_ME_POR_FAVOR}
jwt.expiration=86400000
```

Recomendações de segurança:

- Nunca comite `jwt.secret` real no repositório. Use variáveis de ambiente ou cofres (Vault, AWS Secrets Manager).
- Configure `spring.jpa.hibernate.ddl-auto=update` ou use migrations (Flyway/Liquibase) em produção.

### 3.3 Criar base de dados PostgreSQL (exemplo)

```sql
CREATE DATABASE itsm_db;
CREATE USER itsm_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE itsm_db TO itsm_user;
```

---

## 4. Execução

### 4.1 Com Maven (desenvolvimento)

```bash
./mvnw spring-boot:run
```

### 4.2 Usando o JAR

```bash
java -jar target/incidentmanagement-0.0.1-SNAPSHOT.jar
```

### 4.3 No IntelliJ

- Abra o projeto
- Localize a classe com `@SpringBootApplication` (ex.: `IncidentManagementApplication`)
- Execute como aplicação Java

### 4.4 Porta e variáveis

- Porta padrão: 8080
- Para alterar: `server.port=8081` no `application.properties` ou exportar `SERVER_PORT`.
- Para definir `jwt.secret` em Linux/macOS:

```bash
export JWT_SECRET="valor_secreto_seguro"
```

No Windows (PowerShell):

```powershell
setx JWT_SECRET "valor_secreto_seguro"
```

---

## 5. Uso

### 5.1 Interfaces disponíveis

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- H2 Console (desenvolvimento): `http://localhost:8080/h2-console`
- Interface web: `http://localhost:8080/` (página de login)

### 5.2 Exemplos com cURL

Login (retorna JWT):

```bash
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"joao_tec","password":"password123"}'
```

Criar ticket (substitua SEU_TOKEN_AQUI):

```bash
curl -s -X POST http://localhost:8080/api/tickets \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  -d '{
    "titulo": "Problema de rede",
    "descricao": "Acesso à internet intermitente",
    "prioridade": "ALTA",
    "tipo": "INCIDENTE",
    "abertoPor": {"id": 4}
  }'
```

Listar tickets:

```bash
curl -s -X GET http://localhost:8080/api/tickets \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```

### 5.3 Consola interativa

A aplicação pode expor um menu de consola para operações básicas (listar técnicos, criar ticket, etc.). Use conforme descrito no ecrã ao arrancar a aplicação.

---

## 6. Credenciais de teste (exemplo)

| Utilizador   | Password       | Perfil     |
|--------------|----------------|------------|
| admin        | password123    | ADMIN      |
| joao_tec     | password123    | TECNICO    |
| maria_tec    | password123    | TECNICO    |
| carlos_user  | password123    | UTILIZADOR |

> Estes utilizadores são apenas para testes. Altere as passwords e não use em produção.

---

## 7. Resolução de problemas

- Credenciais inválidas
  - Verifique se o utilizador existe no H2 ou na BD de produção.
  - Reinicie e verifique `data.sql` ou inicializadores.

- Porta 8080 em uso
  - Alterar `server.port` no `application.properties` ou terminar o processo que usa a porta:

```bash
# Linux / macOS
sudo lsof -i :8080
kill -9 <PID>

# Windows (CMD)
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

- Cache sem técnicos
  - Verifique que os dados iniciais foram carregados (`src/main/resources/data.sql`)
  - Confirme propriedades: `spring.sql.init.mode=always` e `spring.jpa.defer-datasource-initialization=true`

- Token JWT inválido
  - Token expirado: faça login novamente
  - Verifique relógio da máquina/servidor
  - Verifique `jwt.secret`

---

## 8. Suporte

Para questões, reporte um issue no repositório ou contacte o autor:

- Email: marcio.bruno.stack@gmail.com
- GitHub: https://github.com/marciobruno-stack

---

**Fim do manual**
