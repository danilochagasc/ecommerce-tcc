# 🛒 E-commerce TCC - Trabalho de Conclusão de Curso

Trabalho de Conclusão de Curso desenvolvido como parte dos requisitos para obtenção do título de graduação.

## 📋 Sobre o Projeto

Este TCC consiste em um **relato de experiência sobre o uso de microsserviços em um sistema de e-commerce**, desenvolvendo uma arquitetura baseada em microsserviços para demonstrar na prática os desafios, soluções e aprendizados envolvidos na adoção dessa abordagem arquitetural.

### Informações Acadêmicas

- **Autor**: Danilo Chagas Clemente
- **Orientador**: Prof. DSc. Bruno de Abreu Silva
- **Instituição**: Universidade Federal de Lavras (UFLA)
- **Título**: Relato de experiência sobre o uso de microsserviços em um sistema de e-commerce
- **Subtitle**: Uma abordagem prática sobre a arquitetura de microsserviços no desenvolvimento de sistemas modernos

## 📁 Estrutura do Projeto

O projeto está organizado em duas partes principais:

```
ecommerce-tcc/
├── latex/                   # 📄 Documentação acadêmica (LaTeX)
│   ├── main.tex            # Arquivo principal do documento
│   ├── secoes/             # Seções do trabalho (introdução, metodologia, etc.)
│   ├── anexos/             # Anexos do trabalho
│   ├── apendices/          # Apêndices do trabalho
│   ├── glossarios/         # Glossários, siglas e símbolos
│   ├── imgs/               # Imagens e figuras
│   ├── codigos/            # Exemplos de código
│   └── README.md           # Instruções sobre o template LaTeX
│
└── project-implementation/ # 💻 Implementação prática
    ├── account/            # Account Service (microsserviço)
    ├── checkout/           # Checkout Service (microsserviço)
    ├── stock/              # Stock Service (microsserviço)
    ├── order/              # Order Service (microsserviço)
    ├── payment/            # Payment Service (microsserviço)
    ├── monitoring/         # Configurações de monitoramento
    ├── scripts/           # Scripts de gerenciamento
    ├── docker-compose.yaml # Orquestração dos serviços
    └── README.md           # Documentação completa da implementação
```

## 📄 Parte 1: Documentação Acadêmica (LaTeX)

A pasta `latex/` contém toda a documentação acadêmica do TCC escrita em LaTeX, utilizando o template da UFLA (6ª edição do manual).

### Conteúdo

- **Estrutura completa do documento**: Capa, folha de rosto, resumo, abstract, etc.
- **Seções principais**: Introdução, Fundamentação Teórica, Metodologia, Conclusão
- **Elementos pós-textuais**: Anexos, apêndices, glossários, referências bibliográficas

### Como Usar

Para compilar o documento LaTeX, consulte o `latex/README.md` que contém instruções detalhadas sobre:
- Como usar o template no Overleaf
- Principais características do template
- Estrutura do documento

## 💻 Parte 2: Implementação Prática

A pasta `project-implementation/` contém a implementação completa do sistema de e-commerce baseado em microsserviços.

### Arquitetura

O sistema é composto por **5 microsserviços independentes**:

1. **Account Service** (Porta 8081) - Gerenciamento de usuários e autenticação JWT
2. **Checkout Service** (Porta 8082) - Carrinho de compras e cupons de desconto
3. **Stock Service** (Porta 8083) - Catálogo de produtos e controle de estoque
4. **Order Service** (Porta 8084) - Gerenciamento de pedidos
5. **Payment Service** (Porta 8085) - Processamento de pagamentos

### Stack Tecnológica

- **Framework**: Spring Boot 3.5.5 com Spring WebFlux (stack reativa)
- **Linguagem**: Kotlin 2.0.21 (JVM 17)
- **Banco de dados**: PostgreSQL 17 (R2DBC) e Redis
- **Arquitetura**: Domain-Driven Design (DDD) e Arquitetura Hexagonal
- **Monitoramento**: Prometheus + Grafana
- **Containerização**: Docker + Docker Compose

### Documentação

Consulte `project-implementation/README.md` para:
- Instruções completas de execução
- Configuração do ambiente
- Endpoints dos serviços
- Monitoramento e métricas

Cada microsserviço possui seu próprio README com informações detalhadas:
- `project-implementation/account/README.md`
- `project-implementation/checkout/README.md`
- `project-implementation/stock/README.md`
- `project-implementation/order/README.md`
- `project-implementation/payment/README.md`

## 🚀 Início Rápido

### Para trabalhar com a documentação (LaTeX):

```bash
cd latex
# Consulte latex/README.md para instruções de compilação
```

### Para trabalhar com a implementação:

```bash
cd project-implementation
# Consulte project-implementation/README.md para instruções completas

# Execução rápida:
docker-compose up -d
```

## 📚 Objetivos do Trabalho

Este TCC tem como objetivo geral desenvolver uma solução backend baseada em uma arquitetura de microsserviços independentes, aplicável a um sistema de e-commerce, buscando:

- Revisar literatura e boas práticas relacionadas a microsserviços
- Modelar arquitetura baseada em microsserviços
- Implementar protótipo funcional com serviços essenciais
- Validar a solução através de testes
- Refletir sobre desafios, limitações e aprendizados

## 🔗 Links Úteis

- **Template LaTeX UFLA**: Baseado no template oficial da UFLA para trabalhos acadêmicos
- **Documentação Spring Boot**: https://spring.io/projects/spring-boot
- **Documentação Kotlin**: https://kotlinlang.org/docs/home.html

## 📝 Notas

- Este é um projeto acadêmico desenvolvido para fins de conclusão de curso
- A documentação acadêmica está em LaTeX seguindo as normas da UFLA
- A implementação prática demonstra conceitos de arquitetura de microsserviços
- Ambas as partes são complementares e fazem parte do trabalho completo

## 📄 Licença

Este projeto é desenvolvido para fins acadêmicos como Trabalho de Conclusão de Curso.

