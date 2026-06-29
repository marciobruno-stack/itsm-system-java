
---

## 📁 `docs/api.md` – Documentação da API REST 

```markdown
# Documentação da API REST – Sistema ITSM

## Base URL
```
http://localhost:8080/api
```

## Autenticação

Todas as requisições (exceto `/auth/login` e `/auth/register`) requerem um token JWT no cabeçalho `Authorization`.

**Formato do cabeçalho:**
```
Authorization: Bearer <seu-token-jwt>
```

---

## 🔐 Endpoints de Autenticação

### POST `/api/auth/login`

Autentica um utilizador e retorna um token JWT.

**Request Body:**
```json
{
  "username": "joao_tec",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2FvX3RlYyIsImlhdCI6MTc0MDAwMDAwMCwiZXhwIjoxNzQwMDg2NDAwfQ..."
}
```

**Response (400 Bad Request):**
```json
{
  "error": "Erro de validação",
  "message": "Credenciais inválidas",
  "timestamp": "2026-06-28T16:00:00",
  "status": 400
}
```

---

### POST `/api/auth/register`

Regista um novo utilizador no sistema.

**Request Body:**
```json
{
  "username": "novo_user",
  "password": "minha_senha",
  "nome": "Novo Utilizador",
  "email": "novo@itsm.com",
  "role": "UTILIZADOR"
}
```

**Valores permitidos para `role`:**
- `ADMIN`
- `TECNICO`
- `UTILIZADOR`

**Response (201 Created):**
```json
{
  "message": "Utilizador criado com sucesso",
  "userId": "5"
}
```

---

## 🎫 Endpoints de Tickets

### GET `/api/tickets`

Lista todos os tickets.

**Headers:**
```
Authorization: Bearer <seu-token>
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "titulo": "Problema de rede",
    "descricao": "Acesso à internet intermitente",
    "prioridade": "ALTA",
    "tipo": "INCIDENTE",
    "estado": "ATRIBUIDO",
    "dataAbertura": "2026-06-28T15:49:21.264857",
    "dataFecho": null,
    "tecnico": {
      "id": 4,
      "utilizador": {
        "id": 6,
        "nome": "João Técnico"
      }
    },
    "abertoPor": {
      "id": 8,
      "nome": "Carlos User"
    },
    "ativo": {
      "id": 6,
      "nome": "Switch Core"
    }
  }
]
```

---

### GET `/api/tickets/{id}`

Busca um ticket específico por ID.

**Headers:**
```
Authorization: Bearer <seu-token>
```

**Response (200 OK):**
```json
{
  "id": 1,
  "titulo": "Problema de rede",
  "descricao": "Acesso à internet intermitente",
  "prioridade": "ALTA",
  "tipo": "INCIDENTE",
  "estado": "ATRIBUIDO",
  "dataAbertura": "2026-06-28T15:49:21.264857",
  "dataFecho": null,
  "tecnico": {
    "id": 4,
    "utilizador": {
      "id": 6,
      "nome": "João Técnico"
    }
  },
  "abertoPor": {
    "id": 8,
    "nome": "Carlos User"
  },
  "ativo": null
}
```

**Response (404 Not Found):**
```json
{
  "error": "Erro de validação",
  "message": "Ticket não encontrado",
  "timestamp": "2026-06-28T16:00:00",
  "status": 404
}
```

---

### GET `/api/tickets/estado/{estado}`

Lista tickets filtrados por estado.

**Headers:**
```
Authorization: Bearer <seu-token>
```

**Valores permitidos para `estado`:**
- `ABERTO`
- `ATRIBUIDO`
- `EM_CURSO`
- `RESOLVIDO`
- `FECHADO`

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "titulo": "Problema de rede",
    "estado": "ATRIBUIDO"
  }
]
```

---

### POST `/api/tickets`

Cria um novo ticket com atribuição automática de técnico.

**Headers:**
```
Authorization: Bearer <seu-token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "titulo": "Problema de rede",
  "descricao": "Acesso à internet intermitente",
  "prioridade": "ALTA",
  "tipo": "INCIDENTE",
  "abertoPor": {
    "id": 4
  }
}
```

**Valores permitidos:**

| Campo | Valores |
|-------|---------|
| `prioridade` | `BAIXA`, `MEDIA`, `ALTA`, `CRITICA` |
| `tipo` | `INCIDENTE`, `PEDIDO` |
| `abertoPor.id` | ID de um utilizador existente |

**Response (201 Created):**
```json
{
  "id": 10,
  "titulo": "Problema de rede",
  "estado": "ATRIBUIDO",
  "tecnico": {
    "id": 4,
    "utilizador": {
      "id": 6,
      "nome": "João Técnico"
    }
  },
  "abertoPor": {
    "id": 4,
    "nome": "Carlos User"
  }
}
```

**Response (400 Bad Request – sem técnico disponível):**
```json
{
  "id": 11,
  "titulo": "Problema na impressora",
  "estado": "ABERTO",
  "tecnico": null,
  "abertoPor": {
    "id": 4,
    "nome": "Carlos User"
  }
}
```

---

### PUT `/api/tickets/{id}`

Atualiza um ticket existente.

**Headers:**
```
Authorization: Bearer <seu-token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "titulo": "Novo título",
  "descricao": "Nova descrição",
  "prioridade": "CRITICA",
  "tipo": "INCIDENTE"
}
```

**Response (200 OK):**
```json
{
  "id": 1,
  "titulo": "Novo título",
  "prioridade": "CRITICA"
}
```

---

### PATCH `/api/tickets/{id}/estado`

Atualiza o estado de um ticket.

**Headers:**
```
Authorization: Bearer <seu-token>
```

**Query Parameter:**
```
?estado=FECHADO
```

**Valores permitidos:**
- `ABERTO`
- `ATRIBUIDO`
- `EM_CURSO`
- `RESOLVIDO`
- `FECHADO`

**Response (200 OK):**
```json
{
  "id": 1,
  "estado": "FECHADO",
  "dataFecho": "2026-06-28T16:00:00"
}
```

---

### DELETE `/api/tickets/{id}`

Remove um ticket.

**Headers:**
```
Authorization: Bearer <seu-token>
```

**Response (204 No Content):**
*(sem corpo)*

---

## 👨‍🔧 Endpoints de Técnicos

### GET `/api/tecnicos`

Lista todos os técnicos.

**Headers:**
```
Authorization: Bearer <seu-token>
```

**Response (200 OK):**
```json
[
  {
    "id": 4,
    "utilizador": {
      "id": 6,
      "nome": "João Técnico"
    },
    "disponibilidade": "{\"segunda\":[\"09:00-18:00\"],\"domingo\":[\"09:00-18:00\"]}",
    "cargaTrabalhoAtual": 0,
    "competencias": [
      {"id": 6, "nome": "Redes"},
      {"id": 7, "nome": "Windows Server"}
    ]
  }
]
```

---

### GET `/api/tecnicos/ordenados-carga`

Lista técnicos ordenados por carga de trabalho (menor para maior).

**Headers:**
```
Authorization: Bearer <seu-token>
```

**Response (200 OK):**
```json
[
  {
    "id": 5,
    "utilizador": {"nome": "Maria Silva"},
    "cargaTrabalhoAtual": 0
  },
  {
    "id": 4,
    "utilizador": {"nome": "João Técnico"},
    "cargaTrabalhoAtual": 2
  }
]
```

---

### GET `/api/tecnicos/{id}`

Busca um técnico por ID.

**Response (200 OK):**
```json
{
  "id": 4,
  "utilizador": {
    "id": 6,
    "nome": "João Técnico"
  },
  "disponibilidade": "{\"segunda\":[\"09:00-18:00\"],\"domingo\":[\"09:00-18:00\"]}",
  "cargaTrabalhoAtual": 0,
  "competencias": [
    {"id": 6, "nome": "Redes"},
    {"id": 7, "nome": "Windows Server"}
  ]
}
```

---

### POST `/api/tecnicos`

Cria um novo técnico.

**Headers:**
```
Authorization: Bearer <seu-token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "utilizador": {
    "id": 6
  },
  "disponibilidade": "{\"segunda\":[\"09:00-18:00\"],\"domingo\":[\"09:00-18:00\"]}",
  "competencias": [
    {"id": 6},
    {"id": 7}
  ]
}
```

**Response (201 Created):**
```json
{
  "id": 6,
  "utilizador": {
    "id": 6,
    "nome": "João Técnico"
  },
  "cargaTrabalhoAtual": 0,
  "competencias": [
    {"id": 6, "nome": "Redes"},
    {"id": 7, "nome": "Windows Server"}
  ]
}
```

---

### PUT `/api/tecnicos/{id}`

Atualiza um técnico.

**Request Body:**
```json
{
  "disponibilidade": "{\"segunda\":[\"10:00-19:00\"]}",
  "competencias": [
    {"id": 6},
    {"id": 8}
  ]
}
```

---

### DELETE `/api/tecnicos/{id}`

Remove um técnico.

**Response (204 No Content)**

---

## 🖥️ Endpoints de Ativos

### GET `/api/ativos`

Lista todos os ativos.

**Response (200 OK):**
```json
[
  {
    "id": 4,
    "tipo": "SERVIDOR",
    "nome": "Servidor de Base de Dados",
    "estado": "OPERACIONAL",
    "dataAquisicao": "2023-01-10",
    "especificacoes": "32GB RAM, 4 vCPUs"
  }
]
```

---

### GET `/api/ativos/estado/{estado}`

Lista ativos por estado.

**Valores permitidos para `estado`:**
- `OPERACIONAL`
- `AVARIADO`
- `MANUTENCAO`

---

### POST `/api/ativos`

Cria um novo ativo.

**Request Body:**
```json
{
  "tipo": "SERVIDOR",
  "nome": "Servidor de Aplicações",
  "estado": "OPERACIONAL",
  "dataAquisicao": "2025-01-15",
  "especificacoes": "64GB RAM, 8 vCPUs"
}
```

**Valores permitidos para `tipo`:**
- `SERVIDOR`
- `COMPUTADOR`
- `SWITCH`
- `LICENCA_SOFTWARE`

---

### PUT `/api/ativos/{id}`

Atualiza um ativo.

### DELETE `/api/ativos/{id}`

Remove um ativo.

---

## 🏷️ Endpoints de Competências

### GET `/api/competencias`

Lista todas as competências.

**Response (200 OK):**
```json
[
  {"id": 6, "nome": "Redes"},
  {"id": 7, "nome": "Windows Server"},
  {"id": 8, "nome": "Linux"},
  {"id": 9, "nome": "SQL"},
  {"id": 10, "nome": "Java"}
]
```

---

### POST `/api/competencias`

Cria uma nova competência.

**Request Body:**
```json
{
  "nome": "Docker"
}
```

**Response (201 Created):**
```json
{
  "id": 11,
  "nome": "Docker"
}
```

---

### PUT `/api/competencias/{id}`

Atualiza uma competência.

### DELETE `/api/competencias/{id}`

Remove uma competência.

---

## 📋 Resumo dos Códigos de Status

| Código | Significado |
|--------|-------------|
| 200 | OK – Requisição bem-sucedida |
| 201 | Created – Recurso criado |
| 204 | No Content – Recurso removido |
| 400 | Bad Request – Erro de validação |
| 401 | Unauthorized – Não autenticado |
| 403 | Forbidden – Sem permissão |
| 404 | Not Found – Recurso não encontrado |
| 500 | Internal Server Error – Erro interno |

---

## 🔑 Exemplo de Fluxo Completo

### 1. Login

**POST** `/api/auth/login`

```json
{
  "username": "joao_tec",
  "password": "password123"
}
```

**Resposta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9..."
}
```

### 2. Criar Ticket

**POST** `/api/tickets`

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

**Body:**
```json
{
  "titulo": "Problema de rede",
  "descricao": "Acesso à internet intermitente",
  "prioridade": "ALTA",
  "tipo": "INCIDENTE",
  "abertoPor": {"id": 4}
}
```

### 3. Listar Tickets

**GET** `/api/tickets`

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

## 📚 Documentação Interativa

A documentação interativa da API está disponível no Swagger UI:

```
http://localhost:8080/swagger-ui/index.html
```

---





