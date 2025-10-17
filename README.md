# Sistema de Contagem Inteligente - Módulo de Câmeras RTSP

## 📱 Visão Geral
Aplicativo Android nativo desenvolvido em Kotlin para exibição simultânea de múltiplos streams de vídeo via RTSP/HTTP, utilizando LibVLC para processamento de mídia em tempo real.

## 🎯 Objetivo
Desenvolver um sistema robusto para captura e exibição de transmissões de vídeo de múltiplas câmeras IP, preparado para integração com sistemas de visão computacional e contagem inteligente.

## ✨ Funcionalidades Implementadas

### 🎥 Exibição de Múltiplas Câmeras
- **Transmissão Simultânea**: Exibe 2 streams de vídeo em tempo real lado a lado
- **Suporte a Protocolos**: RTSP e HTTP para máxima compatibilidade
- **Interface Responsiva**: Layout adaptável para diferentes tamanhos de tela

### 🔧 Gerenciamento de Conexão
- **Status em Tempo Real**: Monitoramento contínuo do estado de cada câmera
- **Reconexão Automática**: Recuperação automática em caso de perda de conexão
- **Controles Manuais**: Botão de reconexão para intervenção do usuário

### ⚡ Performance e Estabilidade
- **Buffer Otimizado**: Configuração de cache para vídeo fluido
- **Timeout Configurável**: Limites personalizáveis para tentativas de conexão
- **Liberação de Recursos**: Gerenciamento adequado de memória e processos

## 🛠️ Tecnologias Utilizadas

- **Linguagem**: Kotlin
- **SDK**: Android Native (API 24+)
- **Biblioteca de Vídeo**: LibVLC 3.6.0
- **Arquitetura**: Clean Architecture com separação de responsabilidades
- **UI**: Android XML com Material Design

## 📁 Estrutura do Projeto
```bash
app/src/main/java/com/example/sistemacontageminteligente/
├── MainActivity.kt # Activity principal e UI
├── CameraManager.kt # Gerenciador central de câmeras
├── CameraConfig.kt # Configurações e URLs
└── RTSPUrlValidator.kt # Validador de URLs RTSP
```
## 🚀 Como Executar

### Pré-requisitos
- Android Studio Arctic Fox ou superior
- Android SDK 34
- Dispositivo/Emulador com API 24+ (Android 7.0+)

### Configuração do Ambiente
1. **Clone o repositório**
```bash
  https://github.com/DiegoCarvalho-dev/Sistema-Camera-RTSP.git
  cd sistema-contagem-inteligente
```

2. **Abra no Android Studio**
```bash
  File → Open → Selecione a pasta do projeto
  Aguarde a sincronização do Gradle
```
3. **Configure o dispositivo**
```bash
  Conecte dispositivo físico via USB OU
  Crie um emulador: AVD Manager → Create Virtual Device
```

4. **Execute a aplicação**
```bash
Clique em Run ▶️ (Shift + F10)
Selecione o dispositivo/alvo
Aguarde a instalação e execução
```
## Configuração de Câmeras
**Para usar câmeras reais, edite o arquivo CameraConfig.kt:**
```bash
object CameraConfig {
    // Substitua pelas suas URLs RTSP
    const val CAMERA_1_URL = "rtsp://usuario:senha@ip-camera-1:554/caminho"
    const val CAMERA_2_URL = "rtsp://usuario:senha@ip-camera-2:554/caminho"
}
```
## 🎮 Como Usar
**Inicialização:** O app conecta automaticamente às câmeras configuradas

**Monitoramento:** Observe os indicadores de status de cada câmera

**Controle:** Use o botão "Reconectar" para reiniciar as conexões

**Performance:** Os vídeos são exibidos com qualidade adaptativa

## 🔧 Configurações Avançadas
```bash
Parâmetros de Performance
kotlin
const val NETWORK_TIMEOUT = 10000    // Timeout de conexão (ms)
const val RECONNECT_DELAY = 2000     // Delay entre reconexões (ms)
Opções do LibVLC

--network-caching=300: Cache para vídeo fluido

--rtsp-tcp: Força uso de TCP para estabilidade

--avcodec-hw=any: Habilita aceleração de hardware
```
## 🐛 Solução de Problemas
**Câmeras Não Conectam:**
```bash
Verifique as URLs no CameraConfig.kt

Confirme acesso à rede do dispositivo

Teste as URLs no VLC Media Player
```
**Vídeo Travando:**
```bash
Ajuste NETWORK_TIMEOUT no CameraConfig.kt

Verifique a velocidade da rede

Reduza a qualidade do stream na câmera
```
**Erro de Compilação:**
```bash
Execute File → Sync Project with Gradle Files

Limpe o projeto: Build → Clean Project

Reinvalide caches: File → Invalidate Caches / Restart
```
## 📊 Status do Projeto
### ✅ COMPLETO - PRONTO PARA PRODUÇÃO
```bash
Exibição de múltiplos streams

Interface profissional

Controles de conexão

Tratamento de erros

Documentação completa
```
## 🔮 Próximas Etapas
```bash
Integração com YOLOv8 para detecção de objetos

Implementação de sistema de contagem inteligente

Adição de banco de dados para persistência

Desenvolvimento de dashboard web
```

## Autor:
### 👤 Diego Ricardo Carvalho
```bash
📧 diegoricardo2527@gmail.com
```
### 💼 LinkedIn
```bash
 www.linkedin.com/in/diegoricardo-dev
```
