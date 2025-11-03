# 📋 Order Service

Serviço responsável pelo gerenciamento completo do ciclo de vida de pedidos no sistema de e-commerce.

## 📋 Sobre

O **Order Service** gerencia todo o processo de criação, processamento e rastreamento de pedidos. Integra-se com outros serviços (Account, Payment, Stock) e possui integração com AWS (Lambda + SES) para envio de notificações por email.

## 🏗️ Arquitetura

O serviço segue os princípios de **Domain-Driven Design (DDD)** e **Arquitetura Hexagonal**:

- **Core/Domain**: Entidades e regras de negócio (Order, Item)
- **Core/Application**: Casos de uso, Services, Commands e Queries
- **Adapter**: Implementações HTTP, R2DBC (PostgreSQL) e clientes HTTP para outros serviços

## 🛠️ Tecnologias

- **Spring Boot 3.5.5** com **Kotlin 2.0.21**
- **Spring WebFlux** - Stack reativa não-bloqueante
- **PostgreSQL 17** via **R2DBC** - Banco de dados reativo
- **WebClient** - Cliente HTTP reativo para integrações
- **AWS SDK S3** - Armazenamento de documentos (futuro)
- **Flyway** - Versionamento de banco de dados
- **Valiktor** - Validação de domínio
- **Micrometer + Prometheus** - Métricas e monitoramento

## 📊 Banco de Dados

- **PostgreSQL 17** na porta **5435**
- **Schema**: `orderdb`
- **Migrations**: Executadas automaticamente pelo Flyway em `src/main/resources/db/migration/`

### Principais Entidades

- `order` - Pedidos do sistema
- `item` - Itens de cada pedido

## 🔌 Endpoints Principais

Base URL: `http://localhost:8084`

- `GET /order` - Listar todos os pedidos
- `GET /order/account/{accountId}` - Listar pedidos de um usuário
- `POST /order` - Criar novo pedido (a partir do carrinho)
- `DELETE /order/{id}` - Remover pedido

## 🔄 Estados do Pedido

- **PENDING_PAYMENT** - Aguardando confirmação de pagamento
- **PAYMENT_CONFIRMED** - Pagamento confirmado, processando
- **PROCESSING** - Pedido sendo preparado
- **SHIPPED** - Pedido enviado
- **DELIVERED** - Pedido entregue
- **CANCELLED** - Pedido cancelado

## 🔄 Integrações

### Account Service

Validação de usuários antes de criar pedidos.

### Payment Service

Processamento de pagamentos durante a criação do pedido.

### Stock Service

Decremento de estoque após confirmação de pagamento através do endpoint `PUT /product/{id}/decrease/{amount}`.

### AWS Lambda + SES

Envio de notificações por email em mudanças de status do pedido:
- Confirmação de pedido
- Atualização de status
- Informações de envio
- Confirmação de entrega

## ⚙️ Configuração

### Variáveis de Ambiente

```bash
SPRING_R2DBC_URL=r2dbc:postgresql://order-postgres:5432/orderdb
SPRING_R2DBC_USERNAME=postgres
SPRING_R2DBC_PASSWORD=postgres
PAYMENT_BASE_URL=http://payment-app:8080
ACCOUNT_BASE_URL=http://account-app:8080
AWS_S3_ENDPOINT_OVERRIDE_URL=http://localstack:4566
```

## 🚀 Execução

### Com Docker Compose

```bash
cd order
docker-compose up -d
```

O serviço estará disponível em `http://localhost:8084`

**Dependências:** Account Service (8081), Payment Service (8085), Stock Service (8083)

### Manualmente

```bash
./gradlew bootRun
```

**Requisitos:** Java 17+, PostgreSQL 17, serviços dependentes em execução, Docker (opcional)

## 🧪 Testes

```bash
./gradlew test
```

Utiliza Kotest, MockK, Testcontainers e WireMock para mock de serviços HTTP externos.

## 📊 Monitoramento

- **Health Check**: `http://localhost:8084/actuator/health`
- **Métricas Prometheus**: `http://localhost:8084/actuator/prometheus`

## 💡 Funcionalidades Especiais

- **Validação em cascata**: Usuário, endereço, produtos e estoque antes de criar pedido
- **Histórico completo**: Mantém histórico de todos os pedidos
- **Operações atômicas**: Garante consistência na criação de pedidos
- **Rollback automático**: Em caso de falhas durante o processamento

## 🔄 Fluxo de Pedido

1. Cliente finaliza carrinho (Checkout Service)
2. Order Service cria pedido:
   - Valida usuário (Account Service)
   - Valida produtos/estoque (Stock Service)
   - Processa pagamento (Payment Service)
   - Cria pedido no banco
3. Pagamento confirmado → Estoque decrementado (Stock Service)
4. Notificação enviada (AWS Lambda + SES)
5. Status atualizado

## 📝 Notas Importantes

- Pedidos não podem ser modificados após criação (apenas cancelados)
- Estoque é decrementado apenas após confirmação de pagamento
- Integrações utilizam timeouts configuráveis
- Notificações por email são assíncronas (não bloqueiam o fluxo)
- Histórico de pedidos é mantido mesmo após cancelamento
