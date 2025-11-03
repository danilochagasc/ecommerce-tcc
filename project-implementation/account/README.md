# 🔐 Account Service

Serviço responsável pelo gerenciamento de usuários, autenticação e autorização do sistema de e-commerce.

## 📋 Sobre

O **Account Service** centraliza todas as funcionalidades relacionadas a contas de usuários, incluindo cadastro, autenticação JWT, gerenciamento de endereços e controle de acesso baseado em papéis (roles: USER e ADMIN).

## 🏗️ Arquitetura

O serviço segue os princípios de **Domain-Driven Design (DDD)** e **Arquitetura Hexagonal**:

- **Core/Domain**: Entidades, Value Objects e regras de negócio (User, Address, Auth)
- **Core/Application**: Casos de uso, Services, Commands e Queries
- **Adapter**: Implementações HTTP, R2DBC (PostgreSQL) e JWT

## 🛠️ Tecnologias

- **Spring Boot 3.5.5** com **Kotlin 2.0.21**
- **Spring WebFlux** - Stack reativa não-bloqueante
- **PostgreSQL 17** via **R2DBC** - Banco de dados reativo
- **Spring Security** + **JJWT** - Autenticação e autorização
- **Flyway** - Versionamento de banco de dados
- **Valiktor** - Validação de domínio
- **Micrometer + Prometheus** - Métricas e monitoramento

## 📊 Banco de Dados

- **PostgreSQL 17** na porta **5433**
- **Schema**: `accountdb`
- **Migrations**: Executadas automaticamente pelo Flyway em `src/main/resources/db/migration/`

### Principais Entidades

- `user` - Usuários do sistema com roles (USER/ADMIN)
- `address` - Endereços dos usuários (máximo 3 por usuário)

## 🔌 Endpoints Principais

Base URL: `http://localhost:8081`

- `POST /auth/login` - Autenticação e geração de token JWT
- `POST /user/register` - Cadastro de novos usuários
- `GET /user` - Listar usuários (ADMIN only)
- `GET /user/{id}` - Buscar usuário por ID
- `PUT /user/{id}` - Atualizar dados do usuário
- `PUT /user/{id}/password` - Alterar senha
- `GET /address/{userId}` - Listar endereços do usuário
- `POST /address` - Criar endereço
- `PUT /address/{id}` - Atualizar endereço
- `DELETE /address/{id}` - Remover endereço

## 🔐 Autenticação

- **JWT (JSON Web Tokens)** com algoritmo HS256
- **Token de acesso**: Expira em 1 hora (configurável)
- **Formato**: Bearer Token no header `Authorization`
- **Roles**: USER (usuário comum) e ADMIN (administrador)

## ⚙️ Configuração

### Variáveis de Ambiente

```bash
SPRING_R2DBC_URL=r2dbc:postgresql://account-postgres:5432/accountdb
SPRING_R2DBC_USERNAME=postgres
SPRING_R2DBC_PASSWORD=postgres
JWT_SECRET=sua_chave_secreta_muito_segura
```

## 🚀 Execução

### Com Docker Compose

```bash
cd account
docker-compose up -d
```

O serviço estará disponível em `http://localhost:8081`

### Manualmente

```bash
./gradlew bootRun
```

**Requisitos:** Java 17+, PostgreSQL 17, Docker (opcional)

## 🧪 Testes

```bash
./gradlew test
```

Utiliza Kotest, MockK e Testcontainers para testes unitários e de integração.

## 📊 Monitoramento

- **Health Check**: `http://localhost:8081/actuator/health`
- **Métricas Prometheus**: `http://localhost:8081/actuator/prometheus`

## 📝 Notas Importantes

- Senhas são criptografadas usando **BCrypt**
- CPF e email são únicos no sistema
- Cada usuário pode ter no máximo 3 endereços
- Rotas públicas: `/auth/login`, `/user/register`, `/actuator/**`
- Demais rotas requerem autenticação JWT válida
