#!/bin/bash

# Script para gerenciar o e-commerce completo
# Uso: ./scripts/ecommerce.sh [start|stop|restart|status|logs|clean]

set -e

COMPOSE_FILE="docker-compose.yaml"
PROJECT_NAME="ecommerce"

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Função para imprimir mensagens coloridas
print_message() {
    echo -e "${2}${1}${NC}"
}

# Função para verificar se o Docker está rodando
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        print_message "❌ Docker não está rodando. Por favor, inicie o Docker primeiro." $RED
        exit 1
    fi
}

# Função para iniciar todos os serviços
start_services() {
    print_message "🚀 Iniciando todos os serviços do e-commerce..." $BLUE
    check_docker
    
    docker-compose -f $COMPOSE_FILE -p $PROJECT_NAME up -d
    
    print_message "✅ Serviços iniciados com sucesso!" $GREEN
    print_message "📊 Acesse os serviços em:" $YELLOW
    print_message "   • Account Service: http://localhost:8081" $NC
    print_message "   • Checkout Service: http://localhost:8082" $NC
    print_message "   • Stock Service: http://localhost:8083" $NC
    print_message "   • Order Service: http://localhost:8084" $NC
    print_message "   • Payment Service: http://localhost:8085" $NC
    print_message "   • Grafana: http://localhost:3000" $NC
    print_message "   • Prometheus: http://localhost:9090" $NC
}

# Função para parar todos os serviços
stop_services() {
    print_message "🛑 Parando todos os serviços..." $YELLOW
    docker-compose -f $COMPOSE_FILE -p $PROJECT_NAME down
    print_message "✅ Serviços parados com sucesso!" $GREEN
}

# Função para reiniciar todos os serviços
restart_services() {
    print_message "🔄 Reiniciando todos os serviços..." $YELLOW
    stop_services
    sleep 2
    start_services
}

# Função para mostrar status dos serviços
show_status() {
    print_message "📊 Status dos serviços:" $BLUE
    docker-compose -f $COMPOSE_FILE -p $PROJECT_NAME ps
}

# Função para mostrar logs
show_logs() {
    print_message "📝 Logs dos serviços:" $BLUE
    docker-compose -f $COMPOSE_FILE -p $PROJECT_NAME logs -f
}

# Função para limpar tudo (containers, volumes, networks)
clean_all() {
    print_message "🧹 Limpando todos os recursos..." $YELLOW
    docker-compose -f $COMPOSE_FILE -p $PROJECT_NAME down -v --remove-orphans
    docker system prune -f
    print_message "✅ Limpeza concluída!" $GREEN
}

# Função para mostrar ajuda
show_help() {
    print_message "🛒 E-commerce Management Script" $BLUE
    echo ""
    print_message "Uso: $0 [comando]" $NC
    echo ""
    print_message "Comandos disponíveis:" $YELLOW
    echo "  start     - Inicia todos os serviços"
    echo "  stop      - Para todos os serviços"
    echo "  restart   - Reinicia todos os serviços"
    echo "  status    - Mostra status dos serviços"
    echo "  logs      - Mostra logs dos serviços"
    echo "  clean     - Remove containers, volumes e networks"
    echo "  help      - Mostra esta ajuda"
    echo ""
}

# Main
case "${1:-help}" in
    start)
        start_services
        ;;
    stop)
        stop_services
        ;;
    restart)
        restart_services
        ;;
    status)
        show_status
        ;;
    logs)
        show_logs
        ;;
    clean)
        clean_all
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        print_message "❌ Comando inválido: $1" $RED
        show_help
        exit 1
        ;;
esac
