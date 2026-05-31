# Stack Tecnológico - Sistema ITSM

## Linguagem e versão
- Java 21 LTS

## Framework principal
- Spring Boot 3.5.14 (estável)

## Dependências principais
- Spring Web (REST API)
- Spring Data JPA (persistência)
- Spring Security (autenticação/autorização)
- PostgreSQL Driver
- Lombok (redução de código boilerplate)
- SpringDoc OpenAPI (documentação da API)

## Base de dados
- PostgreSQL 15 (produção)
- H2 Database (para testes rápidos)

## Controlo de versões
- Git + GitHub

## Ferramentas de build
- Maven

## Testes
- JUnit 5
- Mockito

## Interface operacional
- A API REST será consumível via Swagger UI (interface documentada)
- (Opcional) Poderá ser desenvolvida uma consola simples com Spring Shell

## Justificação
A escolha do Spring Boot deve-se à sua produtividade, integração nativa com JPA e segurança, além da vasta documentação. O PostgreSQL garante integridade referencial e desempenho. O uso de Lombok e Maven acelera o desenvolvimento.