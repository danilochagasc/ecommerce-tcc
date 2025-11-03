# 💳 Payment Service

Serviço responsável pelo processamento de pagamentos e integração com gateways de pagamento do sistema de e-commerce.

## 📋 Sobre

O **Payment Service** gerencia todo o processamento de transações financeiras. Atualmente implementa endpoints de teste para simulação de pagamentos, preparado para futuras integrações com gateways reais (Stripe, Mercado Pago, PagSeguro, etc.).

## 🏗️ Arquitetura

O serviço segue os princípios de **Domain-Driven Design (DDD)** e **Arquitetura Hexagonal**:

- **Core/Domain**: Entidades e regras de negócio (Payment)
- **Core/Application**: Casos de uso, Services, Commands e Queries
- **Adapter**: Implementações HTTP, R2DBC (PostgreSQL) e adaptadores para gateways (futuro)

## 🛠️ Tecnologias

- **Spring Boot 3.5.5** com **Kotlin 2.0.21**
- **Spring WebFlux** - Stack reativa não-bloqueante
- **PostgreSQL 17** via **R2DBC** - Banco de dados reativo
- **AWS SDK S3** - Armazenamento de comprovantes (futuro)
- **Flyway** - Versionamento de banco de dados
- **Valiktor** - Validação de domínio
- **Micrometer + Prometheus** - Métricas e monitoramento

## 📊 Banco de Dados

- **PostgreSQL 17** na porta **5436**
- **Schema**: `paymentdb`
- **Migrations**: Executadas automaticamente pelo Flyway em `src/main/resources/db/migration/`

### Estrutura Planejada

- `payment` - Transações de pagamento (status, gateway, valor, método)

## 🔌 Endpoints

Base URL: `http://localhost:8085`

- `POST /payment/force-success` - Simula pagamento bem-sucedido (desenvolvimento/testes)
- `POST /payment/force-failure` - Simula falha no pagamento (desenvolvimento/testes)

**Nota:** Estes endpoints são para desenvolvimento. Em produção, serão substituídos por integrações reais com gateways.

## 🔄 Estados de Pagamento

- **PENDING** - Pagamento pendente
- **PROCESSING** - Sendo processado pelo gateway
- **SUCCESS** - Pagamento aprovado
- **FAILED** - Pagamento rejeitado
- **CANCELLED** - Pagamento cancelado
- **REFUNDED** - Pagamento reembolsado

## 🔄 Integrações (Futuras)

### Order Service

O **Order Service** integra-se com o Payment Service para processar pagamentos durante a criação de pedidos.

### Gateways de Pagamento (Planejados)

- **Mercado Pago / PagSeguro** (Brasil)
- **Stripe / PayPal** (Internacional)
- **Webhooks** para notificações de status
- **API de Pagamento** para envio de requisições
- **Reconciliação** automática de transações

### AWS S3

Armazenamento de comprovantes de pagamento, faturas e evidências.

## ⚙️ Configuração

### Variáveis de Ambiente

```bash
SPRING_R2DBC_URL=r2dbc:postgresql://payment-postgres:5432/paymentdb
SPRING_R2DBC_USERNAME=postgres
SPRING_R2DBC_PASSWORD=postgres
AWS_S3_ENDPOINT_OVERRIDE_URL=http://localstack:4566
```

## 🚀 Execução

### Com Docker Compose

```bash
cd payment
docker-compose up -d
```

O serviço estará disponível em `http://localhost:8085`

### Manualmente

```bash
./gradlew bootRun
```

**Requisitos:** Java 17+, PostgreSQL 17, Docker (opcional)

## 🧪 Testes

```bash
./gradlew test
```

Utiliza Kotest, MockK e Testcontainers para testes.

## 📊 Monitoramento

- **Health Check**: `http://localhost:8085/actuator/health`
- **Métricas Prometheus**: `http://localhost:8085/actuator/prometheus`

## 💡 Funcionalidades Planejadas

- Integração com gateways reais (Mercado Pago, Stripe)
- Processamento assíncrono de pagamentos
- Recebimento de webhooks dos gateways
- Sistema de reconciliação automática
- Suporte a múltiplos métodos (cartão, PIX, boleto)
- Sistema de reembolsos
- Armazenamento de comprovantes no S3
- Conformidade PCI DSS

## 📝 Notas Importantes

- **Estado atual**: Serviço em desenvolvimento com endpoints de teste
- **Preparado para produção**: Estrutura pronta para integração com gateways reais
- **Segurança**: Preparado para tokenização de cartões e criptografia de dados sensíveis
- **Logs**: Números de cartão serão mascarados em logs
- **Webhooks**: Planejado para recebimento assíncrono de notificações

## 🚧 Roadmap

- [ ] Integração com gateway real (Mercado Pago ou Stripe)
- [ ] Implementação de webhooks
- [ ] Sistema de reconciliação automática
- [ ] Suporte a múltiplos métodos de pagamento
- [ ] Armazenamento de comprovantes no S3
- [ ] Sistema de reembolsos
- [ ] Dashboard de transações
- [ ] Relatórios financeiros
