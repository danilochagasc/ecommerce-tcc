# 🛒 E-commerce TCC

Este é um projeto de e-commerce desenvolvido como Trabalho de Conclusão de Curso (TCC), composto por cinco microserviços independentes que trabalham em conjunto.

## 📋 Arquitetura

O projeto é composto por um **API Gateway** e cinco microsserviços independentes desenvolvidos com **Spring Boot 3.5.5** e **Kotlin 2.0.21**, seguindo os princípios de **Domain-Driven Design (DDD)** e **Arquitetura Hexagonal**:

### API Gateway

- **Gateway Service** (Porta 8080) - Ponto de entrada único com autenticação e autorização centralizadas

### Microserviços

- **Account Service** (Porta interna 8080) - Gerenciamento de usuários e geração de JWT
- **Checkout Service** (Porta interna 8080) - Processamento de carrinho e cupons
- **Stock Service** (Porta interna 8080) - Gerenciamento de produtos e categorias
- **Order Service** (Porta interna 8080) - Gerenciamento de pedidos
- **Payment Service** (Porta interna 8080) - Processamento de pagamentos

> **Nota**: Todos os microserviços são acessíveis **apenas através do Gateway** na porta 8080. Eles não são expostos diretamente para o exterior.

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

| Serviço     | URL                       | Descrição                                |
| ----------- | ------------------------- | ---------------------------------------- |
| **Gateway** | **http://localhost:8080** | **Ponto de entrada único (API Gateway)** |
| Grafana     | http://localhost:3000     | Dashboard de monitoramento               |
| Prometheus  | http://localhost:9090     | Métricas dos serviços                    |

### Rotas Disponíveis no Gateway

#### Rotas Públicas (Sem Autenticação)

- `POST /auth/login` - Login de usuário
- `POST /user/register` - Registro de usuário
- `GET /product/**` - Navegação de produtos
- `GET /category/**` - Navegação de categorias

#### Rotas de Usuário (Requer Autenticação)

- `/user/**` - Gerenciamento de perfil
- `/address/**` - Gerenciamento de endereços
- `/cart/**` - Operações de carrinho
- `/order/**` - Gerenciamento de pedidos
- `/coupon/**` - Visualização e aplicação de cupons

#### Rotas de Admin (Requer Role ADMIN)

- `GET /user` - Listar todos os usuários
- `POST/PUT/DELETE /product/**` - Gerenciamento de produtos
- `POST/PUT/DELETE /category/**` - Gerenciamento de categorias
- `POST/PUT/DELETE /coupon/**` - Gerenciamento de cupons

> **Importante**: Todos os microserviços devem ser acessados através do Gateway. Eles não estão expostos diretamente.

## 🗄️ Bancos de Dados

| Serviço  | Banco      | Porta | Container                  | Migrations |
| -------- | ---------- | ----- | -------------------------- | ---------- |
| Account  | PostgreSQL | 5433  | ecommerce-account-postgres | Flyway     |
| Checkout | Redis      | 6380  | ecommerce-checkout-redis   | -          |
| Stock    | PostgreSQL | 5434  | ecommerce-stock-postgres   | Flyway     |
| Order    | PostgreSQL | 5435  | ecommerce-order-postgres   | Flyway     |
| Payment  | PostgreSQL | 5436  | ecommerce-payment-postgres | Flyway     |

## 🔗 Comunicação Entre Serviços

### Fluxo de Requisição

```
Cliente → Gateway (Porta 8080) → Valida JWT → Verifica Autorização → Roteia para Microserviço
```

### Comunicação Interna

Os serviços se comunicam através da rede compartilhada `ecommerce-network` via HTTP (WebClient reativo):

- **Gateway** → **Todos os Serviços**: Roteamento e autenticação
- **Checkout Service** → **Stock Service**: Validação de produtos e estoque
- **Order Service** → **Account Service**: Validação de usuários
- **Order Service** → **Payment Service**: Processamento de pagamentos
- **Order Service** → **Stock Service**: Decremento de estoque após confirmação de pagamento

### Segurança

- **JWT Validation**: Gateway valida todos os tokens JWT
- **Role-Based Access Control**: Gateway aplica regras de autorização baseadas em roles
- **Service Isolation**: Microserviços não são acessíveis diretamente do exterior

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
├── gateway/                 # API Gateway
│   ├── README.md           # Documentação do gateway
│   ├── docker-compose.yaml
│   ├── Dockerfile
│   └── src/
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

- `gateway/README.md` - **API Gateway**
- `account/README.md` - Account Service
- `checkout/README.md` - Checkout Service
- `stock/README.md` - Stock Service
- `order/README.md` - Order Service
- `payment/README.md` - Payment Service

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
