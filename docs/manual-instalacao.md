Aqui está o **manual de instalação e utilização** completo e bem formatado:

---

## 📁 `docs/manual-instalacao.md` – Manual de Instalação e Utilização

```markdown
# Manual de Instalação e Utilização – Sistema ITSM

## Índice

1. [Pré-requisitos](#pré-requisitos)
2. [Instalação](#instalação)
3. [Configuração](#configuração)
4. [Execução](#execução)
5. [Utilização](#utilização)
6. [Credenciais de Teste](#credenciais-de-teste)
7. [Resolução de Problemas](#resolução-de-problemas)

---

## Pré-requisitos

Antes de instalar o sistema, certifique-se de que tem os seguintes softwares instalados:

| Software | Versão | Descrição |
|----------|-------|-----------|
| **Java JDK** | 21 LTS ou superior | Ambiente de execução |
| **Maven** | 3.8+ | Gestão de dependências |
| **Git** | 2.0+ | Controlo de versões |
| **PostgreSQL** (opcional) | 15+ | Base de dados em produção |
| **IntelliJ IDEA** (opcional) | 2023+ | IDE recomendada |

### Verificar instalações

```bash
# Verificar Java
java -version
# Deve mostrar: openjdk version "21.0.x"

# Verificar Maven
mvn -version
# Deve mostrar: Apache Maven 3.8.x

# Verificar Git
git --version
# Deve mostrar: git version 2.x
```

---

## Instalação

### 1. Clonar o repositório

```bash
git clone https://github.com/marciobruno-stack/itsm-system-java.git
cd itsm-system-java
```

### 2. Compilar o projeto

```bash
./mvnw clean compile
```

### 3. Executar os testes (opcional)

```bash
./mvnw test
```

### 4. Criar o ficheiro JAR (opcional)

```bash
./mvnw package
```

O ficheiro JAR será gerado em `target/incidentmanagement-0.0.1-SNAPSHOT.jar`.

---

## Configuração

### Ficheiro `application.properties`

O ficheiro de configuração está em `src/main/resources/application.properties`.

#### Configuração para Desenvolvimento (H2)

```properties
# H2 Database Configuration
spring.datasource.url=jdbc:h2:mem:itsm_db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# H2 Console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JWT Configuration
jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
jwt.expiration=86400000
```

#### Configuração para Produção (PostgreSQL)

```properties
# PostgreSQL Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/itsm_db
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.username=itsm_user
spring.datasource.password=your_password

# JPA Configuration
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# JWT Configuration
jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
jwt.expiration=86400000
```

### Criar base de dados PostgreSQL (produção)

```sql
CREATE DATABASE itsm_db;
CREATE USER itsm_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE itsm_db TO itsm_user;
```

---

## Execução

### Opção 1 – Executar com Maven

```bash
./mvnw spring-boot:run
```

### Opção 2 – Executar o JAR

```bash
java -jar target/incidentmanagement-0.0.1-SNAPSHOT.jar
```

### Opção 3 – Executar no IntelliJ

1. Abrir o projeto no IntelliJ IDEA
2. Localizar a classe `IncidentManagementApplication`
3. Clicar no botão **Run** (▶)

### Verificar se a aplicação está ativa

Abrir no navegador:

- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **H2 Console**: `http://localhost:8080/h2-console`
- **Página de Login**: `http://localhost:8080/login`

---

## Utilização

### Interface Web

Após iniciar a aplicação, aceda a `http://localhost:8080/login`:

```
┌─────────────────────────────────────────────────────┐
│  ┌─────────────────────────────────────────────┐   │
│  │           Sistema ITSM                      │   │
│  │  Gestão de Infraestruturas e Incidentes    │   │
│  ├─────────────────────────────────────────────┤   │
│  │  Utilizador: [joao_tec_______________]     │   │
│  │  Password:  [••••••••••••••••••••]         │   │
│  │                                             │   │
│  │  [Entrar]                                   │   │
│  ├─────────────────────────────────────────────┤   │
│  │  [Criar conta]                             │   │
│  └─────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────┘
```

### Dashboard por Perfil

#### ADMIN

```
┌─────────────────────────────────────────────────────┐
│  Dashboard Administrativo                          │
│  Bem-vindo, admin!                                 │
│                                                    │
│  ┌──────┐ ┌──────┐ ┌──────┐ ┌──────┐            │
│  │Total  │ │Abertos│ │Técni-│ │Resol-│            │
│  │Tickets│ │       │ │ cos  │ │vidos │            │
│  │  15   │ │   5   │ │  3   │ │  7   │            │
│  └──────┘ └──────┘ └──────┘ └──────┘            │
│                                                    │
│  [Utilizadores] [Técnicos] [Tickets]              │
│                                                    │
│  Tickets Recentes:                                 │
│  ┌────┬────────────┬────────┬────────┬────────┐   │
│  │ ID │ Título     │ Estado │Priorid.│Técnico │   │
│  ├────┼────────────┼────────┼────────┼────────┤   │
│  │ 1  │Problema... │ATRIBUID│ ALTA   │João    │   │
│  │ 2  │Instalar... │ABERTO  │ MEDIA  │ -      │   │
│  └────┴────────────┴────────┴────────┴────────┘   │
└─────────────────────────────────────────────────────┘
```

#### TECNICO

```
┌─────────────────────────────────────────────────────┐
│  Dashboard do Técnico                             │
│  Bem-vindo, joao_tec!                             │
│                                                    │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐          │
│  │Meus      │ │Em Aberto │ │Resolvidos│          │
│  │Tickets   │ │          │ │          │          │
│  │   8      │ │    3     │ │    5     │          │
│  └──────────┘ └──────────┘ └──────────┘          │
│                                                    │
│  [Meus Tickets]                                    │
└─────────────────────────────────────────────────────┘
```

#### UTILIZADOR

```
┌─────────────────────────────────────────────────────┐
│  Dashboard do Utilizador                          │
│  Bem-vindo, carlos_user!                          │
│                                                    │
│  ┌──────────┐ ┌─────────────────────────┐         │
│  │Meus      │ │  [Criar Novo Ticket]    │         │
│  │Tickets   │ │                         │         │
│  │   4      │ │                         │         │
│  └──────────┘ └─────────────────────────┘         │
└─────────────────────────────────────────────────────┘
```

### API REST (Postman / cURL)

#### 1. Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"joao_tec","password":"password123"}'
```

#### 2. Criar Ticket

```bash
curl -X POST http://localhost:8080/api/tickets \
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

#### 3. Listar Tickets

```bash
curl -X GET http://localhost:8080/api/tickets \
  -H "Authorization: Bearer SEU_TOKEN_AQUI"
```

### Consola Interativa

Ao executar a aplicação, a consola apresenta um menu:

```
============================================================
  SISTEMA ITSM - GESTÃO DE INFRAESTRUTURAS E INCIDENTES
============================================================

--- MENU PRINCIPAL ---
1. Listar Técnicos
2. Listar Tickets
3. Criar Ticket (com atribuição automática)
4. Atualizar Estado de Ticket
5. Listar Ativos
6. Listar Competências
7. Criar Técnico
8. Sair
Opção:
```

---

## Credenciais de Teste

| Utilizador | Password | Perfil |
|------------|----------|--------|
| `admin` | `password123` | ADMIN |
| `joao_tec` | `password123` | TECNICO |
| `maria_tec` | `password123` | TECNICO |
| `carlos_user` | `password123` | UTILIZADOR |

### Dados de Teste Iniciais

| Tabela | Registos |
|--------|----------|
| **Utilizadores** | 4 (admin, 2 técnicos, 1 user) |
| **Técnicos** | 2 (João, Maria) |
| **Competências** | 5 (Redes, Windows Server, Linux, SQL, Java) |
| **Ativos** | 3 (Servidor, Portátil, Switch) |
| **Tickets** | 3 (iniciais) |

---

## Resolução de Problemas

### Erro: "Credenciais inválidas"

**Causa:** Password incorreta ou utilizador não existe.

**Solução:**
1. Verificar no H2 console:
   ```sql
   SELECT username FROM utilizador;
   ```
2. Se o utilizador não existir, registar um novo:
   ```bash
   curl -X POST http://localhost:8080/api/auth/register \
     -H "Content-Type: application/json" \
     -d '{"username":"novo","password":"123456","nome":"Novo","email":"novo@itsm.com","role":"UTILIZADOR"}'
   ```

### Erro: "Porta 8080 já está em uso"

**Causa:** Outra aplicação está a usar a porta 8080.

**Solução 1:** Alterar a porta no `application.properties`:
```properties
server.port=8081
```

**Solução 2:** Parar o processo que está a usar a porta:
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
sudo lsof -i :8080
kill -9 <PID>
```

### Erro: "Cache carregado: 0 técnicos"

**Causa:** Os dados não foram inseridos na base de dados.

**Solução:**
1. Verificar o ficheiro `data.sql` em `src/main/resources/`
2. Garantir que a configuração está correta:
   ```properties
   spring.sql.init.mode=always
   spring.jpa.defer-datasource-initialization=true
   ```
3. Reiniciar a aplicação

### Erro: "Token JWT inválido"

**Causa:** Token expirado ou inválido.

**Solução:**
1. Fazer login novamente para obter novo token
2. Verificar a data/hora do sistema (token é baseado no timestamp)

### Aceder à H2 Console

URL: `http://localhost:8080/h2-console`

Configuração:
- **JDBC URL**: `jdbc:h2:mem:itsm_db`
- **User Name**: `sa`
- **Password**: (deixar vazio)

---

## Suporte

Para questões técnicas, contactar o desenvolvedor:

- **Email**: marcio.bruno.stack@gmail.com
- **GitHub**: https://github.com/marciobruno-stack

---

**Fim do Manual de Instalação e Utilização**
```

---

Agora a documentação está **100% completa**! 🚀

## 📁 Resumo dos ficheiros de documentação

| Ficheiro | Conteúdo | Estado |
|----------|----------|--------|
| `docs/api.md` | Documentação da API REST | ✅ Completo |
| `docs/arquitetura.md` | Documentação da arquitetura | ✅ Completo |
| `docs/manual-instalacao.md` | Manual de instalação e utilização | ✅ **Completo** |







