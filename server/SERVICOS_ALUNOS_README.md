# 🎓 Serviços de Alunos e Progresso - Treine Me API

## 📋 Resumo das Funcionalidades Implementadas

Este documento descreve os novos serviços implementados para gerenciamento de alunos e tracking de progresso na plataforma Treine Me.

### ✅ **Funcionalidades Adicionadas**

1. **CRUD Completo de Alunos** - Professores podem gerenciar seus alunos
2. **Sistema de Progresso de Aulas** - Tracking detalhado do progresso dos alunos
3. **Autenticação Específica para Alunos** - Sistema de login com controle por professor
4. **Relatórios de Progresso** - Visualização detalhada para professores
5. **Tabelas de Banco de Dados** - Nova estrutura para persistir dados de progresso

---

## 🗄️ **Estrutura do Banco de Dados**

### Nova Tabela: `progressos_aula`

```sql
CREATE TABLE progressos_aula (
    id UUID PRIMARY KEY,
    aluno_id UUID REFERENCES alunos(id) ON DELETE CASCADE,
    aula_id UUID REFERENCES aulas(id) ON DELETE CASCADE,
    professor_id UUID REFERENCES professores(id) ON DELETE CASCADE,
    
    -- Dados de progresso
    minutos_totais_assistidos INTEGER DEFAULT 0,
    ultimo_minuto_assistido INTEGER DEFAULT 0,
    percentual_concluido INTEGER DEFAULT 0, -- 0-100
    concluida BOOLEAN DEFAULT FALSE,
    
    -- Timestamps
    data_inicio_visualizacao TIMESTAMP NULL,
    data_ultima_visualizacao TIMESTAMP NULL,
    data_conclusao TIMESTAMP NULL,
    
    -- Metadados
    numero_visualizacoes INTEGER DEFAULT 0,
    tempo_total_sessao INTEGER DEFAULT 0, -- Em segundos
    dispositivo_ultima_visualizacao VARCHAR(50) NULL,
    
    -- Campos base
    dt_ins TIMESTAMP NOT NULL,
    dt_upd TIMESTAMP NOT NULL,
    id_user_ins UUID NOT NULL,
    id_user_upd UUID NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    
    -- Índices
    UNIQUE(aluno_id, aula_id),
    INDEX(professor_id)
);
```

---

## 🔐 **Autenticação de Alunos**

### Endpoints de Autenticação

#### **1. Login com Professor Específico**
```bash
POST /auth/aluno/login
{
  "email": "aluno@exemplo.com",
  "senha": "senha123",
  "professorId": "550e8400-e29b-41d4-a716-446655440000"
}
```

### **Características do Sistema de Autenticação:**
- ✅ **Controle por Professor**: Cada aluno só acessa conteúdo de professores específicos
- ✅ **Validação de Inscrição**: Verifica se o aluno tem inscrição ativa
- ✅ **Login Direto**: Aluno deve sempre especificar qual professor quer acessar
- ✅ **Tokens JWT**: Sistema seguro de autenticação

---

## 👥 **CRUD de Alunos (Para Professores)**

### Endpoints Disponíveis

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `POST` | `/professores/me/alunos` | Criar novo aluno |
| `GET` | `/professores/me/alunos` | Listar alunos do professor |
| `GET` | `/professores/me/alunos/{id}` | Obter aluno específico |
| `PUT` | `/professores/me/alunos/{id}` | Atualizar dados do aluno |
| `DELETE` | `/professores/me/alunos/{id}` | Desativar aluno |
| `POST` | `/professores/me/alunos/{id}/reset-password` | Resetar senha do aluno |

### **Exemplo de Uso:**

```bash
# Criar aluno
curl -X POST "http://localhost:8080/professores/me/alunos" \
  -H "Authorization: Bearer {token_professor}" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "email": "joao@exemplo.com",
    "senha": "senha123",
    "fotoPerfilUrl": "https://exemplo.com/foto.jpg"
  }'

# Listar alunos
curl -X GET "http://localhost:8080/professores/me/alunos?page=1&size=20" \
  -H "Authorization: Bearer {token_professor}"
```

---

## 📊 **Sistema de Progresso de Aulas**

### Para Alunos (Registrar Progresso)

#### **Registrar Progresso**
```bash
POST /aluno/progresso
Authorization: Bearer {token_aluno}
{
  "aulaId": "550e8400-e29b-41d4-a716-446655440000",
  "minutosTotaisAssistidos": 15,
  "ultimoMinutoAssistido": 15,
  "percentualConcluido": 75,
  "concluida": false,
  "dispositivoUltimaVisualizacao": "iPhone"
}
```

#### **Obter Meu Progresso**
```bash
GET /aluno/progresso?page=1&size=20
Authorization: Bearer {token_aluno}
```

### **Dados Coletados:**
- ✅ **Minutos assistidos**: Total e último minuto
- ✅ **Percentual de conclusão**: 0-100%
- ✅ **Status de conclusão**: Boolean
- ✅ **Timestamps**: Início, última visualização, conclusão
- ✅ **Metadados**: Número de visualizações, dispositivo, tempo total

---

## 📈 **Relatórios de Progresso (Para Professores)**

### Endpoints de Relatórios

#### **1. Resumo Geral do Aluno**
```bash
GET /professores/me/alunos/{alunoId}/progresso/resumo
Authorization: Bearer {token_professor}
```

**Retorna:**
- Total de aulas disponíveis
- Aulas assistidas e concluídas
- Minutos totais assistidos
- Percentual geral de conclusão
- Data da última atividade

#### **2. Progresso por Módulo**
```bash
GET /professores/me/alunos/{alunoId}/progresso/por-modulo
Authorization: Bearer {token_professor}
```

**Retorna:**
- Progresso organizado por módulo
- Estatísticas por módulo
- Lista detalhada de aulas

#### **3. Progresso Detalhado**
```bash
GET /professores/me/alunos/{alunoId}/progresso/detalhado?page=1&size=20
Authorization: Bearer {token_professor}
```

#### **4. Estatísticas Gerais**
```bash
GET /professores/me/estatisticas-gerais
Authorization: Bearer {token_professor}
```

**Retorna:**
- Total de alunos
- Total de minutos assistidos
- Média de progresso por aluno
- Alunos ativos
- Percentual de alunos ativos

---

## 🏗️ **Arquitetura dos Serviços**

### **Serviços Implementados:**

1. **`AlunoCrudService`** - CRUD completo de alunos
2. **`ProgressoAulaService`** - Gerenciamento de progresso
3. **`AlunoAuthService`** - Autenticação específica
4. **`AlunoService`** - Funcionalidades gerais (atualizado)

### **Modelos de Dados:**

1. **`ProgressoAula`** - Modelo principal de progresso
2. **`ProgressoAulaCreateRequest`** - Request para criar progresso
3. **`ProgressoAulaResponse`** - Response com dados completos
4. **`ResumoProgressoAluno`** - Resumo estatístico
5. **`ProgressoPorModulo`** - Progresso agrupado por módulo

### **Rotas Organizadas:**

1. **`AlunoAuthRoutes`** - Autenticação de alunos
2. **`ProfessorAlunoRoutes`** - Professor gerencia alunos
3. **`AlunoRoutes`** - Funcionalidades do aluno (atualizado)

---

## 🔒 **Controle de Acesso**

### **Regras de Segurança:**

1. **Professores**:
   - ✅ Só podem ver/editar alunos que têm inscrições com eles
   - ✅ Só podem ver progresso de seus próprios alunos
   - ✅ Podem resetar senhas de seus alunos

2. **Alunos**:
   - ✅ Só podem registrar progresso em aulas que têm acesso
   - ✅ Só podem ver seu próprio progresso
   - ✅ Precisam de inscrição ativa para acessar conteúdo

3. **Validações**:
   - ✅ Verificação de inscrição ativa
   - ✅ Validação de professor-aluno
   - ✅ Controle de acesso por plano

---

## 📝 **Documentação Swagger**

Toda a nova funcionalidade foi adicionada à documentação Swagger:

- **Tags adicionadas:**
  - `CRUD de Alunos`
  - `Progresso de Aulas`
  - `Autenticação de Alunos`
  - `Relatórios de Progresso`

- **Schemas adicionados:**
  - Todos os modelos de progresso
  - Responses paginados
  - Requests de autenticação específicos

**Acesse:** `http://localhost:8080/swagger`

---

## 🚀 **Como Usar**

### **1. Inicie o Servidor**
```bash
cd server
./gradlew run
```

### **2. Teste as Funcionalidades**

#### **Criar Aluno (Professor)**
```bash
POST /professores/me/alunos
Authorization: Bearer {token_professor}
{
  "nome": "Maria Silva",
  "email": "maria@exemplo.com",
  "senha": "senha123",
  "planoId": "550e8400-e29b-41d4-a716-446655440000"
}
```
> **✨ Novidade**: O professor agora seleciona um plano no momento da criação do aluno. O aluno é automaticamente inscrito no plano selecionado com status `ATIVA`.

#### **Login do Aluno**
```bash
POST /auth/aluno/login
{
  "email": "maria@exemplo.com",
  "senha": "senha123",
  "professorId": "{id_do_professor}"
}
```

#### **Registrar Progresso (Aluno)**
```bash
POST /aluno/progresso
Authorization: Bearer {token_aluno}
{
  "aulaId": "{id_da_aula}",
  "minutosTotaisAssistidos": 10,
  "ultimoMinutoAssistido": 10,
  "percentualConcluido": 50,
  "concluida": false
}
```

#### **Ver Progresso do Aluno (Professor)**
```bash
GET /professores/me/alunos/{alunoId}/progresso/resumo
Authorization: Bearer {token_professor}
```

---

## 📊 **Exemplo de Fluxo Completo**

### **Cenário:** Professor acompanha progresso de aluno

1. **Professor cria aluno**
2. **Professor atribui plano ao aluno**
3. **Aluno faz login especificando o professor**
4. **Aluno assiste aula e registra progresso**
5. **Professor visualiza relatórios de progresso**

```bash
# 1. Criar aluno
curl -X POST "/professores/me/alunos" -d '{"nome":"João","email":"joao@exemplo.com","senha":"123456"}'

# 2. Atribuir plano (endpoint existente)
curl -X POST "/professores/me/planos/{planoId}/alunos/{alunoId}"

# 3. Login do aluno
curl -X POST "/auth/aluno/login" -d '{"email":"joao@exemplo.com","senha":"123456","professorId":"{professorId}"}'

# 4. Registrar progresso
curl -X POST "/aluno/progresso" -d '{"aulaId":"{aulaId}","minutosTotaisAssistidos":15,"percentualConcluido":75}'

# 5. Ver progresso (professor)
curl -X GET "/professores/me/alunos/{alunoId}/progresso/resumo"
```

---

## 🎯 **Próximos Passos Sugeridos**

1. **Dashboard de Progresso** - Interface visual para professores
2. **Notificações** - Alertas quando aluno completa aulas
3. **Gamificação** - Pontos, badges, rankings
4. **Relatórios Avançados** - Exportação, gráficos, tendências
5. **Integração Mobile** - Apps nativos para melhor tracking

---

## ✅ **Resumo do que foi Implementado**

- ✅ **Tabela de progresso** com todos os campos necessários
- ✅ **CRUD completo de alunos** para professores
- ✅ **Sistema de autenticação** específico para alunos
- ✅ **Tracking de progresso** com dados detalhados
- ✅ **Relatórios completos** para professores
- ✅ **Controle de acesso** robusto
- ✅ **Documentação Swagger** atualizada
- ✅ **Validações e segurança** em todos os endpoints

O sistema está pronto para uso e permite que professores acompanhem detalhadamente o progresso de seus alunos! 🎉
