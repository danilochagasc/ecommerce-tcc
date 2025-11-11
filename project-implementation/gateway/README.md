# 🚪 Gateway Service

Serviço responsável pelo roteamento centralizado, autenticação e autorização de todas as requisições do sistema de e-commerce.

## 📋 Sobre

O **Gateway Service** atua como ponto de entrada único para todos os microserviços do sistema. Centraliza a validação de tokens JWT gerados pelo Account Service e aplica regras de autorização baseadas em papéis (roles: USER e ADMIN). Todas as requisições passam pelo Gateway antes de serem roteadas para os serviços apropriados.

## 🏗️ Arquitetura

O serviço segue os princípios de **Domain-Driven Design (DDD)** e **Arquitetura Hexagonal**:

- **Core/Domain**: Entidades e regras de negócio (TokenValidator, Role, UserId)
- **Core/Application**: Casos de uso e serviços
- **Adapter**: Implementações HTTP, Spring Cloud Gateway, Spring Security e JWT

## 🛠️ Tecnologias

- **Spring Boot 3.5.5** com **Kotlin 2.0.21**
- **Spring Cloud Gateway** - Roteamento e filtros reativos
- **Spring WebFlux** - Stack reativa não-bloqueante
- **Spring Security** + **JJWT** - Validação de tokens JWT e autorização
- **Micrometer + Prometheus** - Métricas e monitoramento

## 🔐 Autenticação e Autorização

### Responsabilidades

- **Validação de Tokens**: Valida tokens JWT gerados pelo Account Service
- **Decodificação de Claims**: Extrai informações do token (userId, email, role)
- **Controle de Acesso**: Aplica regras de autorização baseadas em roles
- **Roteamento Seguro**: Roteia requisições autenticadas para os serviços

### Importante

- O Gateway **NÃO gera tokens** - apenas valida e decodifica
- A geração de tokens é responsabilidade exclusiva do **Account Service**
- Ambos os serviços devem usar o **mesmo JWT_SECRET** para validação funcionar

## 🔌 Endpoints e Rotas

Base URL: `http://localhost:8080`

### Rotas Públicas (Sem Autenticação)

- `POST /auth/login` - Autenticação de usuário (rota para Account Service)
- `POST /user/register` - Cadastro de novos usuários (rota para Account Service)
- `GET /product` - Listar produtos (rota para Stock Service)
- `GET /product/{id}` - Buscar produto por ID (rota para Stock Service)
- `GET /category` - Listar categorias (rota para Stock Service)
- `GET /category/{id}` - Buscar categoria por ID (rota para Stock Service)

### Rotas de Usuário (Requer Autenticação)

- `GET /user/findByLogin` - Buscar usuário por login (Account Service)
- `PUT /user/{id}` - Atualizar dados do usuário (Account Service)
- `PUT /user/{id}/password` - Alterar senha (Account Service)
- `GET /address/{userId}` - Listar endereços (Account Service)
- `POST /address` - Criar endereço (Account Service)
- `PUT /address/{id}` - Atualizar endereço (Account Service)
- `DELETE /address/{id}` - Remover endereço (Account Service)
- `GET /cart/{id}` - Buscar carrinho (Checkout Service)
- `POST /cart/{id}` - Adicionar item ao carrinho (Checkout Service)
- `PUT /cart/{cartId}/item/{productId}/increase/{quantity}` - Aumentar quantidade (Checkout Service)
- `PUT /cart/{cartId}/item/{productId}/decrease/{quantity}` - Diminuir quantidade (Checkout Service)
- `DELETE /cart/{cartId}/item/{productId}` - Remover item (Checkout Service)
- `PUT /cart/{id}/coupon/{code}` - Aplicar cupom (Checkout Service)
- `DELETE /cart/{id}/coupon` - Remover cupom (Checkout Service)
- `GET /coupon/{code}` - Buscar cupom por código (Checkout Service)
- `GET /order` - Listar pedidos (Order Service)
- `GET /order/account/{accountId}` - Listar pedidos do usuário (Order Service)
- `POST /order` - Criar pedido (Order Service)

### Rotas de Admin (Requer Role ADMIN)

- `GET /user` - Listar todos os usuários (Account Service)
- `POST /product` - Criar produto (Stock Service)
- `PUT /product/{id}` - Atualizar produto (Stock Service)
- `DELETE /product/{id}` - Remover produto (Stock Service)
- `POST /product/{id}/image` - Upload de imagem (Stock Service)
- `PUT /product/{id}/decrease/{amount}` - Decrementar estoque (Stock Service)
- `POST /category` - Criar categoria (Stock Service)
- `PUT /category/{id}` - Atualizar categoria (Stock Service)
- `DELETE /category/{id}` - Remover categoria (Stock Service)
- `POST /coupon` - Criar cupom (Checkout Service)
- `PUT /coupon/{code}` - Atualizar cupom (Checkout Service)
- `DELETE /coupon/{code}` - Remover cupom (Checkout Service)

## 🔄 Roteamento para Microserviços

O Gateway roteia requisições para os seguintes serviços:

### Account Service
- Rotas: `/auth/**`, `/user/**`, `/address/**`
- Porta interna: `8080` (não exposta externamente)

### Checkout Service
- Rotas: `/cart/**`, `/coupon/**`
- Porta interna: `8080` (não exposta externamente)

### Stock Service
- Rotas: `/product/**`, `/category/**`
- Porta interna: `8080` (não exposta externamente)

### Order Service
- Rotas: `/order/**`
- Porta interna: `8080` (não exposta externamente)

### Payment Service
- Rotas: `/payment/**`
- Porta interna: `8080` (não exposta externamente)

## ⚙️ Configuração

### Variáveis de Ambiente

```bash
JWT_SECRET=sua_chave_secreta_muito_segura  # DEVE ser igual ao Account Service
ACCOUNT_BASE_URL=http://account-app:8080
CHECKOUT_BASE_URL=http://checkout-app:8080
STOCK_BASE_URL=http://stock-app:8080
ORDER_BASE_URL=http://order-app:8080
PAYMENT_BASE_URL=http://payment-app:8080
SERVER_PORT=8080
```

### Configuração JWT

```yaml
security:
  jwt:
    secret-key: "${JWT_SECRET:change_this_to_a_long_secret_key_very_secure}"
    access-token-expiration: 3600000  # 1 hora em milissegundos
```

**⚠️ CRÍTICO**: O `JWT_SECRET` deve ser **exatamente igual** no Gateway e no Account Service. Caso contrário, a validação de tokens falhará.

## 🚀 Execução

### Com Docker Compose

```bash
cd project-implementation
docker-compose up -d gateway-app
```

O serviço estará disponível em `http://localhost:8080`

**Dependências:** Account Service, Checkout Service, Stock Service, Order Service, Payment Service

### Manualmente

```bash
cd gateway
./gradlew bootRun
```

**Requisitos:** Java 17+, todos os microserviços em execução, Docker (opcional)

## 🧪 Testes

```bash
./gradlew test
```

Utiliza Kotest, MockK e Spring Security Test para testes.

## 📊 Monitoramento

- **Health Check**: `http://localhost:8080/actuator/health`
- **Métricas Prometheus**: `http://localhost:8080/actuator/prometheus`
- **Info**: `http://localhost:8080/actuator/info`

## 💡 Funcionalidades Especiais

- **Validação de Token**: Verifica assinatura e expiração antes de rotear
- **Extração de Bearer Token**: Extrai automaticamente tokens do header `Authorization`
- **Role-Based Access Control**: Aplica regras de autorização baseadas em roles (USER, ADMIN)
- **Roteamento Inteligente**: Roteia requisições baseado em path e método HTTP
- **Rewrite Path**: Reescreve paths para manter compatibilidade com serviços
- **Isolamento de Serviços**: Microserviços não são expostos diretamente ao exterior

## 🔄 Fluxo de Requisição

1. **Cliente** envia requisição para Gateway (`http://localhost:8080`)
2. **Gateway** extrai token do header `Authorization: Bearer <token>`
3. **Gateway** valida token JWT (assinatura e expiração)
4. **Gateway** decodifica claims (userId, email, role)
5. **Gateway** verifica autorização baseada em role e rota
6. **Gateway** roteia requisição para microserviço apropriado
7. **Microserviço** processa requisição e retorna resposta
8. **Gateway** retorna resposta ao cliente

## 📝 Notas Importantes

- **Ponto de Entrada Único**: Todos os clientes devem acessar apenas o Gateway (porta 8080)
- **Microserviços Isolados**: Serviços não são acessíveis diretamente do exterior
- **JWT_SECRET Sincronizado**: Deve ser idêntico no Gateway e Account Service
- **Rotas Públicas**: Login e registro não requerem autenticação
- **Rotas Protegidas**: Demais rotas requerem token JWT válido
- **Rotas Admin**: Requerem role ADMIN no token
- **Validação Antes de Roteamento**: Tokens inválidos são rejeitados antes de chegar aos serviços
- **Sem Geração de Tokens**: Gateway apenas valida tokens gerados pelo Account Service

## 🔐 Segurança

- **Validação de Assinatura**: Verifica se token foi assinado com JWT_SECRET correto
- **Verificação de Expiração**: Rejeita tokens expirados
- **Extração Segura**: Valida formato Bearer antes de processar
- **Isolamento de Rede**: Microserviços em rede Docker interna
- **Sem Exposição Direta**: Serviços não acessíveis externamente
