# 🛒 E-commerce TCC

Este é um projeto de e-commerce desenvolvido como Trabalho de Conclusão de Curso (TCC), composto por cinco microserviços independentes que trabalham em conjunto.

## 📋 Arquitetura

O projeto é composto por cinco microsserviços independentes desenvolvidos com **Spring Boot 3.5.5** e **Kotlin 2.0.21**, seguindo os princípios de **Domain-Driven Design (DDD)** e **Arquitetura Hexagonal**:

- **Account Service** (Porta 8081) - Gerenciamento de usuários e autenticação JWT
- **Checkout Service** (Porta 8082) - Processamento de carrinho e cupons
- **Stock Service** (Porta 8083) - Gerenciamento de produtos e categorias
- **Order Service** (Porta 8084) - Gerenciamento de pedidos
- **Payment Service** (Porta 8085) - Processamento de pagamentos

### Stack Tecnológica Utilizada

- **Framework**: Spring Boot 3.5.5 com Spring WebFlux (stack reativa)
- **Linguagem**: Kotlin 2.0.21 (JVM 17)
- **Banco de dados**: PostgreSQL 17 (R2DBC) e Redis
- **Versionamento de Banco de Dados**: Flyway
- **Monitoramento**: Prometheus + Grafana
- **Containerização**: Docker + Docker Compose
- **AWS**: LocalStack (desenvolvimento) para simulação de Amazon S3

## 🚀 Como Executar

### Pré-requisitos

- **Docker** e **Docker Compose** instalados
- **Git** (para clonar o repositório)
- **Java 17+** (se executar serviços manualmente)
- Mínimo **8GB de RAM** recomendado (para todos os containers)

### Execução Rápida

#### No Windows (PowerShell):

```powershell
.\scripts\ecommerce.ps1 start
```

#### No Linux/Mac (Bash):

```bash
chmod +x scripts/ecommerce.sh
./scripts/ecommerce.sh start
```

### Execução Manual

```bash
# Na raiz do projeto
docker-compose up -d
```

**Nota:** Certifique-se de estar na pasta `project-implementation` para executar o docker-compose principal que orquestra todos os serviços.

## 📊 Monitoramento

O projeto inclui monitoramento completo com:

- **Grafana**: http://localhost:3000
- **Prometheus**: http://localhost:9090

## 🔧 Comandos Disponíveis

### Scripts de Gerenciamento

#### PowerShell (Windows):

```powershell
.\scripts\ecommerce.ps1 [comando]
```

#### Bash (Linux/Mac):

```bash
./scripts/ecommerce.sh [comando]
```

**Comandos disponíveis:**

- `start` - Inicia todos os serviços
- `stop` - Para todos os serviços
- `restart` - Reinicia todos os serviços
- `status` - Mostra status dos serviços
- `logs` - Mostra logs dos serviços
- `clean` - Remove containers, volumes e networks
- `help` - Mostra ajuda

### Docker Compose Manual

```bash
# Iniciar todos os serviços
docker-compose up -d

# Parar todos os serviços
docker-compose down

# Ver logs
docker-compose logs -f

# Ver status
docker-compose ps

# Limpar tudo
docker-compose down -v --remove-orphans
```

## 🌐 Endpoints dos Serviços

| Serviço    | URL                   | Descrição                   |
| ---------- | --------------------- | --------------------------- |
| Account    | http://localhost:8081 | Gerenciamento de usuários   |
| Checkout   | http://localhost:8082 | Carrinho e cupons           |
| Stock      | http://localhost:8083 | Produtos e categorias       |
| Order      | http://localhost:8084 | Gerenciamento de pedidos    |
| Payment    | http://localhost:8085 | Processamento de pagamentos |
| Grafana    | http://localhost:3000 | Dashboard de monitoramento  |
| Prometheus | http://localhost:9090 | Métricas dos serviços       |

## 🗄️ Bancos de Dados

| Serviço  | Banco      | Porta | Container                  | Migrations |
| -------- | ---------- | ----- | -------------------------- | ---------- |
| Account  | PostgreSQL | 5433  | ecommerce-account-postgres | Flyway     |
| Checkout | Redis      | 6380  | ecommerce-checkout-redis   | -          |
| Stock    | PostgreSQL | 5434  | ecommerce-stock-postgres   | Flyway     |
| Order    | PostgreSQL | 5435  | ecommerce-order-postgres   | Flyway     |
| Payment  | PostgreSQL | 5436  | ecommerce-payment-postgres | Flyway     |

## 🔗 Comunicação Entre Serviços

Os serviços se comunicam através da rede compartilhada `ecommerce-network` via HTTP (WebClient reativo):

- **Checkout Service** → **Stock Service**: Validação de produtos e estoque
- **Order Service** → **Account Service**: Validação de usuários
- **Order Service** → **Payment Service**: Processamento de pagamentos
- **Order Service** → **Stock Service**: Decremento de estoque após confirmação de pagamento

## 🗃️ Migrations de Banco de Dados

O projeto utiliza **Flyway** para gerenciar migrations dos bancos PostgreSQL:

### **Account Service**

- **Migrations**: `account/src/main/resources/db/migration/`
- **Container**: `ecommerce-account-flyway`
- **Execução**: Automática na inicialização

### **Stock Service**

- **Migrations**: `stock/src/main/resources/db/migration/`
- **Container**: `ecommerce-stock-flyway`
- **Execução**: Automática na inicialização

### **Order Service**

- **Migrations**: `order/src/main/resources/db/migration/`
- **Container**: `ecommerce-order-flyway`
- **Execução**: Automática na inicialização

### **Payment Service**

- **Migrations**: `payment/src/main/resources/db/migration/`
- **Container**: `ecommerce-payment-flyway`
- **Execução**: Automática na inicialização

### **Ordem de Execução**

1. PostgreSQL containers iniciam
2. Flyway executa migrations
3. Aplicações Spring Boot iniciam
4. Serviços ficam disponíveis

### **Verificar Migrations**

```bash
# Ver logs do Flyway
docker-compose logs account-flyway
docker-compose logs stock-flyway
docker-compose logs order-flyway
docker-compose logs payment-flyway

# Verificar status das migrations
docker exec ecommerce-account-flyway flyway info
docker exec ecommerce-stock-flyway flyway info
docker exec ecommerce-order-flyway flyway info
docker exec ecommerce-payment-flyway flyway info
```

## 📁 Estrutura do Projeto

```
project-implementation/
├── account/                 # Account Service
│   ├── README.md           # Documentação do serviço
│   ├── docker-compose.yaml
│   ├── Dockerfile
│   └── src/
├── checkout/                # Checkout Service
│   ├── README.md           # Documentação do serviço
│   ├── docker-compose.yaml
│   ├── Dockerfile
│   └── src/
├── stock/                   # Stock Service
│   ├── README.md           # Documentação do serviço
│   ├── docker-compose.yaml
│   ├── Dockerfile
│   └── src/
├── order/                   # Order Service
│   ├── README.md           # Documentação do serviço
│   ├── docker-compose.yaml
│   ├── Dockerfile
│   └── src/
├── payment/                 # Payment Service
│   ├── README.md           # Documentação do serviço
│   ├── docker-compose.yaml
│   ├── Dockerfile
│   └── src/
├── monitoring/              # Configurações centralizadas
│   ├── prometheus/
│   └── grafana/
├── scripts/                 # Scripts de gerenciamento
│   ├── ecommerce.sh
│   └── ecommerce.ps1
├── docker-compose.yaml      # Compose principal
└── README.md                # Este arquivo
```

### Documentação Individual

Cada serviço possui seu próprio README com informações detalhadas:

- `account/README.md` - Account Service
- `checkout/README.md` - Checkout Service
- `stock/README.md` - Stock Service
- `order/README.md` - Order Service
- `payment/README.md` - Payment Service

## 🛠️ Desenvolvimento

### Executar Serviços Individualmente

Cada serviço pode ser executado independentemente:

```bash
# Account Service
cd account
docker-compose up -d

# Checkout Service
cd checkout
docker-compose up -d

# Stock Service
cd stock
docker-compose up -d
```

### Logs de Desenvolvimento

```bash
# Logs de todos os serviços
docker-compose logs -f

# Logs de um serviço específico
docker-compose logs -f account-app
docker-compose logs -f checkout-app
docker-compose logs -f stock-app
docker-compose logs -f order-app
docker-compose logs -f payment-app
```

## 🧹 Limpeza

Para limpar completamente o ambiente:

```bash
# Usando script
./scripts/ecommerce.sh clean

# Ou manualmente
docker-compose down -v --remove-orphans
docker system prune -f
```

## 📝 Notas Importantes

### Tecnologias e Padrões

- Todos os serviços usam **Spring Boot 3.5.5** com **Kotlin 2.0.21**
- Stack reativa com **Spring WebFlux** e **R2DBC** (non-blocking I/O)
- Arquitetura baseada em **DDD** e **Arquitetura Hexagonal**
- **Account, Stock, Order e Payment** usam PostgreSQL 17 com Flyway para migrations
- **Checkout** usa Redis para armazenamento em memória (sem migrations)

### Infraestrutura

- Cada serviço possui seu próprio banco de dados (isolamento de dados)
- A rede `ecommerce-network` permite comunicação entre serviços via HTTP
- **LocalStack** é usado para simulação de AWS S3 em desenvolvimento (compartilhado por Stock, Order e Payment)
- **Migrations são executadas automaticamente** pelo Flyway antes das aplicações iniciarem

### Monitoramento

- Cada serviço expõe métricas via Spring Boot Actuator
- **Prometheus** coleta métricas de todos os serviços
- **Grafana** fornece dashboards centralizados para visualização
- Health checks disponíveis em `/actuator/health` em cada serviço

## 🤝 Contribuição

Este é um projeto de TCC. Para sugestões ou melhorias, entre em contato com o desenvolvedor.

## 📄 Licença

Este projeto é desenvolvido para fins acadêmicos como Trabalho de Conclusão de Curso.
