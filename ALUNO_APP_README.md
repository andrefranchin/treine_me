# App do Aluno - Configuração e Uso

## 🎯 Funcionalidades Implementadas

### ✅ **Autenticação do Aluno**
- Sistema de login específico para alunos
- Validação de acesso por professor
- Gerenciamento automático de token de sessão
- Logout com limpeza de sessão

### ✅ **Configuração de Professor**
- Variável de ambiente centralizada para ID do professor
- Todas as requisições usam automaticamente o professor configurado
- Fácil mudança de professor por ambiente/build

### ✅ **Gerenciamento de Sessão**
- Token JWT armazenado automaticamente após login
- Todas as requisições HTTP incluem o token automaticamente
- Verificação automática de login ao iniciar o app

## 🚀 Como Usar

### **1. Configurar ID do Professor**

O ID do professor está centralizado em `AlunoConfig.kt`:

```kotlin
// Valor padrão para desenvolvimento
AlunoConfig.professorId = "ba273d71-9f1b-4c1e-b732-dff3913750e1"

// Para mudar o professor (ex: no início do app)
AlunoConfig.configureProfessorId("novo-id-do-professor")
```

### **2. Fluxo de Autenticação**

1. **Tela de Boas-vindas**: Primeiro acesso do usuário
2. **Tela de Login**: Credenciais do aluno (email/senha)
3. **Validação**: Sistema verifica se o aluno tem acesso ao professor configurado
4. **Sessão**: Token armazenado automaticamente para futuras requisições

### **3. Navegação do App**

```
Welcome Screen → Login Screen → Explore Classes → Training
     ↑              ↓                ↓
     ←── Logout ←─────────────────────┘
```

## 🔧 Arquitetura Técnica

### **Serviços Principais**

- **`AlunoAuthService`**: Gerencia login/logout e sessão
- **`AlunoConfig`**: Configuração centralizada do professor
- **`PublicService`**: Acesso a conteúdos públicos do professor
- **`TokenStore`**: Armazenamento do token JWT

### **Fluxo de Requisições**

1. **Login**: `POST /auth/aluno/login` com `professorId`
2. **Token**: Armazenado em `TokenStore.token`
3. **Requests**: `ApiClient` adiciona automaticamente o token em todas as requisições
4. **Conteúdo**: Todas as chamadas usam `AlunoConfig.professorId`

### **Componentes UI**

- **`AlunoRoot`**: Gerenciador principal de rotas
- **`WelcomeScreen`**: Tela de boas-vindas
- **`LoginScreen`**: Formulário de autenticação
- **`ExploreClassesScreen`**: Lista de produtos/cursos
- **`TrainingScreen`**: Detalhes do curso com módulos

## 🎨 Funcionalidades da UI

### **Tela de Login**
- Campos de email e senha
- Validação de entrada
- Indicador de loading
- Mensagens de erro claras
- Toggle para mostrar/ocultar senha

### **Tela de Explorar Aulas**
- Lista de produtos do professor
- Menu com opção de logout
- Navegação para detalhes do curso
- Estados de loading e erro

### **Gerenciamento de Sessão**
- Verificação automática de login ao iniciar
- Redirecionamento inteligente baseado no estado de autenticação
- Logout acessível via menu

## 🔐 Segurança

- **JWT Token**: Autenticação baseada em token seguro
- **Validação de Acesso**: Aluno deve ter inscrição ativa com o professor
- **Sessão Persistente**: Token mantido entre sessões do app
- **Logout Seguro**: Limpeza completa do token

## 📱 Estados do App

1. **Não Autenticado**: Mostra Welcome Screen
2. **Autenticando**: Tela de login com loading
3. **Autenticado**: Acesso completo aos conteúdos
4. **Erro de Auth**: Mensagens claras de erro

## 🛠️ Para Desenvolvedores

### **Mudança de Professor**
```kotlin
// No início do app ou baseado em configuração
AlunoConfig.configureProfessorId("novo-professor-id")
```

### **Verificar Estado de Login**
```kotlin
val authService = AlunoAuthService()
if (authService.isLoggedIn()) {
    // Usuário logado
}
```

### **Logout Manual**
```kotlin
val authService = AlunoAuthService()
authService.logout()
```

## 📋 Próximos Passos Sugeridos

1. **Persistência**: Salvar token em storage local para sobreviver a reinicializações
2. **Refresh Token**: Implementar renovação automática de tokens
3. **Multi-Professor**: Permitir que um aluno acesse múltiplos professores
4. **Configuração Dinâmica**: Carregar ID do professor de um servidor de configuração
5. **Biometria**: Adicionar autenticação biométrica como opção
