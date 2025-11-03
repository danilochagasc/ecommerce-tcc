# 🛒 Checkout Service

Serviço responsável pelo gerenciamento de carrinho de compras e cupons de desconto do sistema de e-commerce.

## 📋 Sobre

O **Checkout Service** gerencia o processo de checkout, incluindo operações de carrinho de compras e aplicação de cupons de desconto. Utiliza Redis para armazenamento em memória, garantindo alta performance nas operações de leitura e escrita.

## 🏗️ Arquitetura

O serviço segue os princípios de **Domain-Driven Design (DDD)** e **Arquitetura Hexagonal**:

- **Core/Domain**: Entidades e regras de negócio (Cart, Coupon)
- **Core/Application**: Casos de uso, Services, Commands e Queries
- **Adapter**: Implementações HTTP, Redis e cliente HTTP para Stock Service

## 🛠️ Tecnologias

- **Spring Boot 3.5.5** com **Kotlin 2.0.21**
- **Spring WebFlux** - Stack reativa não-bloqueante
- **Redis** - Banco de dados em memória (porta 6380)
- **Spring Data Redis** - Integração reativa com Redis
- **WebClient** - Cliente HTTP reativo para comunicação com Stock Service
- **Valiktor** - Validação de domínio
- **Micrometer + Prometheus** - Métricas e monitoramento

## 📊 Armazenamento

- **Redis** na porta **6380**
- **Persistência**: Em memória (dados temporários com TTL)
- **Serialização**: JSON
- **Estruturas**: Hashes para carrinho e cupons

## 🔌 Endpoints Principais

Base URL: `http://localhost:8082`

### Carrinho

- `GET /cart/{id}` - Buscar carrinho
- `POST /cart/{id}` - Adicionar item ao carrinho
- `PUT /cart/{cartId}/item/{productId}/increase/{quantity}` - Aumentar quantidade
- `PUT /cart/{cartId}/item/{productId}/decrease/{quantity}` - Diminuir quantidade
- `DELETE /cart/{cartId}/item/{productId}` - Remover item
- `PUT /cart/{id}/coupon/{code}` - Aplicar cupom
- `DELETE /cart/{id}/coupon` - Remover cupom
- `DELETE /cart/{id}` - Limpar carrinho

### Cupons

- `GET /coupon` - Listar todos os cupons
- `GET /coupon/{code}` - Buscar cupom por código
- `POST /coupon` - Criar cupom
- `PUT /coupon/{code}` - Atualizar cupom
- `DELETE /coupon/{code}` - Remover cupom

## 🔄 Integrações

### Stock Service

O serviço integra-se com o **Stock Service** para validar existência de produtos, verificar disponibilidade de estoque e obter preços atualizados antes de adicionar itens ao carrinho.

**Comunicação:** HTTP síncrona via WebClient reativo

## ⚙️ Configuração

### Variáveis de Ambiente

```bash
SPRING_DATA_REDIS_HOST=checkout-redis
SPRING_DATA_REDIS_PORT=6379
STOCK_BASE_URL=http://stock-app:8080
```

## 🚀 Execução

### Com Docker Compose

```bash
cd checkout
docker-compose up -d
```

O serviço estará disponível em `http://localhost:8082`

### Manualmente

```bash
./gradlew bootRun
```

**Requisitos:** Java 17+, Redis, Docker (opcional)

## 🧪 Testes

```bash
./gradlew test
```

Utiliza Kotest, MockK, Testcontainers e WireMock para testes.

## 📊 Monitoramento

- **Health Check**: `http://localhost:8082/actuator/health`
- **Métricas Prometheus**: `http://localhost:8082/actuator/prometheus`

## 💡 Funcionalidades Especiais

- **Cálculo automático**: Subtotal, desconto e total calculados automaticamente
- **Validação de estoque**: Verifica disponibilidade antes de adicionar itens
- **TTL configurável**: Carrinhos podem expirar automaticamente
- **Descontos**: Suporte a descontos fixos e percentuais com limite máximo

## 📝 Notas Importantes

- Dados em Redis são temporários e podem ser perdidos em reinicialização
- Integração com Stock Service é síncrona, mas não bloqueia (modelo reativo)
- Cupons podem ter período de validade e valor mínimo de compra
- Descontos percentuais podem ter valor máximo limitado
