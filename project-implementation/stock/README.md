# 📦 Stock Service

Serviço responsável pelo gerenciamento completo do catálogo de produtos, categorias e controle de estoque do sistema de e-commerce.

## 📋 Sobre

O **Stock Service** gerencia todo o catálogo de produtos, incluindo categorias, controle de estoque e upload de imagens. Integra-se com AWS S3 (via LocalStack em desenvolvimento) para armazenamento de imagens de produtos.

## 🏗️ Arquitetura

O serviço segue os princípios de **Domain-Driven Design (DDD)** e **Arquitetura Hexagonal**:

- **Core/Domain**: Entidades e regras de negócio (Product, Category)
- **Core/Application**: Casos de uso, Services, Commands e Queries
- **Adapter**: Implementações HTTP, R2DBC (PostgreSQL) e integração com AWS S3

## 🛠️ Tecnologias

- **Spring Boot 3.5.5** com **Kotlin 2.0.21**
- **Spring WebFlux** - Stack reativa não-bloqueante
- **PostgreSQL 17** via **R2DBC** - Banco de dados reativo
- **AWS SDK S3** - Armazenamento de imagens
- **LocalStack** - Emulação local de serviços AWS (desenvolvimento)
- **Flyway** - Versionamento de banco de dados
- **Valiktor** - Validação de domínio
- **Micrometer + Prometheus** - Métricas e monitoramento

## 📊 Banco de Dados

- **PostgreSQL 17** na porta **5434**
- **Schema**: `stockdb`
- **Migrations**: Executadas automaticamente pelo Flyway em `src/main/resources/db/migration/`

### Principais Entidades

- `category` - Categorias de produtos
- `product` - Produtos do catálogo com estoque

## 🔌 Endpoints Principais

Base URL: `http://localhost:8083`

### Categorias

- `GET /category` - Listar todas as categorias
- `GET /category/{id}` - Buscar categoria por ID
- `POST /category` - Criar categoria
- `PUT /category/{id}` - Atualizar categoria
- `DELETE /category/{id}` - Remover categoria

### Produtos

- `GET /product` - Listar produtos (com filtros opcionais: categoryId, name)
- `GET /product/{id}` - Buscar produto por ID
- `POST /product` - Criar produto
- `POST /product/{id}/image` - Upload de imagem (multipart/form-data)
- `PUT /product/{id}` - Atualizar produto
- `PUT /product/{id}/decrease/{amount}` - Decrementar estoque
- `DELETE /product/{id}` - Remover produto

## ☁️ Integração com AWS S3

O serviço utiliza **AWS S3** para armazenamento de imagens de produtos:

- **Desenvolvimento**: LocalStack (`http://localhost:4566`)
- **Produção**: AWS S3 real
- **Bucket**: `product-images-bucket` (criado automaticamente)

## ⚙️ Configuração

### Variáveis de Ambiente

```bash
SPRING_R2DBC_URL=r2dbc:postgresql://stock-postgres:5432/stockdb
SPRING_R2DBC_USERNAME=postgres
SPRING_R2DBC_PASSWORD=postgres
AWS_S3_ENDPOINT_OVERRIDE_URL=http://localstack:4566  # LocalStack em dev
```

## 🔄 Integrações

- **Checkout Service**: Consulta produtos e verifica estoque
- **Order Service**: Decrementa estoque após confirmação de pedidos

**Comunicação:** HTTP síncrona via WebClient reativo

## 🚀 Execução

### Com Docker Compose

```bash
cd stock
docker-compose up -d
```

O serviço estará disponível em `http://localhost:8083`

**Nota:** O LocalStack é configurado automaticamente via script em `scripts/localstack/create-s3-localstack.sh`

### Manualmente

```bash
./gradlew bootRun
```

**Requisitos:** Java 17+, PostgreSQL 17, LocalStack ou AWS S3, Docker (opcional)

## 🧪 Testes

```bash
./gradlew test
```

Utiliza Kotest, MockK, Testcontainers (PostgreSQL e LocalStack) e WireMock.

## 📊 Monitoramento

- **Health Check**: `http://localhost:8083/actuator/health`
- **Métricas Prometheus**: `http://localhost:8083/actuator/prometheus`

## 💡 Funcionalidades Especiais

- **Controle de estoque**: Verificação automática e operações atômicas
- **Upload de imagens**: Validação de tipo e geração de URLs únicas
- **Busca e filtros**: Busca por nome e filtro por categoria
- **Precisão financeira**: Preços armazenados com precisão decimal

## 📝 Notas Importantes

- Produtos sem estoque (quantity = 0) ainda podem ser visualizados
- Categorias com produtos não podem ser removidas
- Decremento de estoque é operação atômica para evitar condições de corrida
- Imagens são armazenadas no S3 com nomes únicos baseados no ID do produto
