# 🛒 E-commerce TCC

Este é um projeto de e-commerce desenvolvido como Trabalho de Conclusão de Curso (TCC), composto por três microserviços independentes que trabalham em conjunto.

## 📋 Arquitetura

O projeto é composto por três serviços principais:

- **Account Service** (Porta 8081) - Gerenciamento de usuários e autenticação
- **Checkout Service** (Porta 8082) - Processamento de carrinho e cupons
- **Stock Service** (Porta 8083) - Gerenciamento de produtos e categorias

## 🚀 Como Executar

### Pré-requisitos

- Docker e Docker Compose instalados
- Git (para clonar o repositório)

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
docker-compose up -d
```

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

| Serviço    | URL                   | Descrição                  |
| ---------- | --------------------- | -------------------------- |
| Account    | http://localhost:8081 | Gerenciamento de usuários  |
| Checkout   | http://localhost:8082 | Carrinho e cupons          |
| Stock      | http://localhost:8083 | Produtos e categorias      |
| Grafana    | http://localhost:3000 | Dashboard de monitoramento |
| Prometheus | http://localhost:9090 | Métricas dos serviços      |

## 🗄️ Bancos de Dados

| Serviço  | Banco      | Porta | Container                  | Migrations |
| -------- | ---------- | ----- | -------------------------- | ---------- |
| Account  | PostgreSQL | 5433  | ecommerce-account-postgres | Flyway     |
| Stock    | PostgreSQL | 5434  | ecommerce-stock-postgres   | Flyway     |
| Checkout | Redis      | 6380  | ecommerce-checkout-redis   | -          |

## 🔗 Comunicação Entre Serviços

Atualmente os serviços não se comunicam diretamente, mas estão preparados para comunicação futura através da rede compartilhada `ecommerce-network`.

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

# Verificar status das migrations
docker exec ecommerce-account-flyway flyway info
docker exec ecommerce-stock-flyway flyway info
```

## 📁 Estrutura do Projeto

```
ecommerce-tcc/
├── account/                 # Serviço de Account
│   ├── docker-compose.yaml
│   ├── Dockerfile
│   └── src/
├── checkout/                # Serviço de Checkout
│   ├── docker-compose.yaml
│   ├── Dockerfile
│   └── src/
├── stock/                   # Serviço de Stock
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
└── README.md
```

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

- Todos os serviços usam Spring Boot com Kotlin
- **Account e Stock** usam PostgreSQL com Flyway para migrations
- **Checkout** usa Redis (sem migrations)
- Cada serviço tem suas próprias métricas no Prometheus
- O Grafana está configurado para mostrar métricas de todos os serviços
- Os bancos de dados são independentes por serviço
- A rede `ecommerce-network` permite comunicação futura entre serviços
- **Migrations são executadas automaticamente** antes das aplicações iniciarem

## 🤝 Contribuição

Este é um projeto de TCC. Para sugestões ou melhorias, entre em contato com o desenvolvedor.

## 📄 Licença

Este projeto é desenvolvido para fins acadêmicos como Trabalho de Conclusão de Curso.
