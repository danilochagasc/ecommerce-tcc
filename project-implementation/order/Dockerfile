# =========================
# 1️⃣ Build stage
# =========================
FROM gradle:8.10.1-jdk17 AS builder

# Define o diretório de trabalho dentro do container
WORKDIR /app

# Copia apenas os arquivos necessários para baixar dependências
COPY build.gradle.kts settings.gradle.kts ./
COPY gradle gradle

# Faz o download das dependências antes de copiar o código
RUN gradle dependencies --no-daemon

# Copia o código-fonte
COPY . .

# Compila o projeto e gera o JAR final
RUN gradle bootJar --no-daemon

# =========================
# 2️⃣ Runtime stage
# =========================
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Copia o JAR gerado da etapa anterior
COPY --from=builder /app/build/libs/*.jar app.jar

# Expõe a porta padrão do Spring Boot
EXPOSE 8080

# Define o comando de inicialização
ENTRYPOINT ["java", "-jar", "app.jar"]
