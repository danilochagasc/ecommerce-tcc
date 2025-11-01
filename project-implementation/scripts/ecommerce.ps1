# Script PowerShell para gerenciar o e-commerce completo
# Uso: .\scripts\ecommerce.ps1 [start|stop|restart|status|logs|clean]

param(
    [Parameter(Position=0)]
    [string]$Command = "help"
)

$ComposeFile = "docker-compose.yaml"
$ProjectName = "ecommerce"

# Função para imprimir mensagens coloridas
function Write-ColorMessage {
    param(
        [string]$Message,
        [string]$Color = "White"
    )
    Write-Host $Message -ForegroundColor $Color
}

# Função para verificar se o Docker está rodando
function Test-Docker {
    try {
        docker info | Out-Null
        return $true
    }
    catch {
        return $false
    }
}

# Função para iniciar todos os serviços
function Start-Services {
    Write-ColorMessage "🚀 Iniciando todos os serviços do e-commerce..." "Blue"
    
    if (-not (Test-Docker)) {
        Write-ColorMessage "❌ Docker não está rodando. Por favor, inicie o Docker primeiro." "Red"
        exit 1
    }
    
    docker-compose -f $ComposeFile -p $ProjectName up -d
    
    Write-ColorMessage "✅ Serviços iniciados com sucesso!" "Green"
    Write-ColorMessage "📊 Acesse os serviços em:" "Yellow"
    Write-Host "   • Account Service: http://localhost:8081"
    Write-Host "   • Checkout Service: http://localhost:8082"
    Write-Host "   • Stock Service: http://localhost:8083"
    Write-Host "   • Order Service: http://localhost:8084"
    Write-Host "   • Payment Service: http://localhost:8085"
    Write-Host "   • Grafana: http://localhost:3000"
    Write-Host "   • Prometheus: http://localhost:9090"
}

# Função para parar todos os serviços
function Stop-Services {
    Write-ColorMessage "🛑 Parando todos os serviços..." "Yellow"
    docker-compose -f $ComposeFile -p $ProjectName down
    Write-ColorMessage "✅ Serviços parados com sucesso!" "Green"
}

# Função para reiniciar todos os serviços
function Restart-Services {
    Write-ColorMessage "🔄 Reiniciando todos os serviços..." "Yellow"
    Stop-Services
    Start-Sleep -Seconds 2
    Start-Services
}

# Função para mostrar status dos serviços
function Show-Status {
    Write-ColorMessage "📊 Status dos serviços:" "Blue"
    docker-compose -f $ComposeFile -p $ProjectName ps
}

# Função para mostrar logs
function Show-Logs {
    Write-ColorMessage "📝 Logs dos serviços:" "Blue"
    docker-compose -f $ComposeFile -p $ProjectName logs -f
}

# Função para limpar tudo (containers, volumes, networks)
function Clean-All {
    Write-ColorMessage "🧹 Limpando todos os recursos..." "Yellow"
    docker-compose -f $ComposeFile -p $ProjectName down -v --remove-orphans
    docker system prune -f
    Write-ColorMessage "✅ Limpeza concluída!" "Green"
}

# Função para mostrar ajuda
function Show-Help {
    Write-ColorMessage "🛒 E-commerce Management Script" "Blue"
    Write-Host ""
    Write-ColorMessage "Uso: .\scripts\ecommerce.ps1 [comando]" "White"
    Write-Host ""
    Write-ColorMessage "Comandos disponíveis:" "Yellow"
    Write-Host "  start     - Inicia todos os serviços"
    Write-Host "  stop      - Para todos os serviços"
    Write-Host "  restart   - Reinicia todos os serviços"
    Write-Host "  status    - Mostra status dos serviços"
    Write-Host "  logs      - Mostra logs dos serviços"
    Write-Host "  clean     - Remove containers, volumes e networks"
    Write-Host "  help      - Mostra esta ajuda"
    Write-Host ""
}

# Main
switch ($Command.ToLower()) {
    "start" {
        Start-Services
    }
    "stop" {
        Stop-Services
    }
    "restart" {
        Restart-Services
    }
    "status" {
        Show-Status
    }
    "logs" {
        Show-Logs
    }
    "clean" {
        Clean-All
    }
    "help" {
        Show-Help
    }
    default {
        Write-ColorMessage "❌ Comando inválido: $Command" "Red"
        Show-Help
        exit 1
    }
}
