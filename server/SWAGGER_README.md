# Documentação da API - Treine Me

Este projeto utiliza **OpenAPI 3.0** (Swagger) para documentação automática da API.

## 🚀 Como acessar a documentação

### 1. Swagger UI (Interface Web Interativa)
Após iniciar o servidor, acesse:
```
http://localhost:8080/swagger
```

A interface Swagger UI permite:
- ✅ Visualizar todos os endpoints disponíveis
- ✅ Testar requisições diretamente no navegador
- ✅ Ver exemplos de request/response
- ✅ Baixar a especificação OpenAPI

### 2. Especificação OpenAPI (YAML)
Para acessar o arquivo de especificação diretamente:
```
http://localhost:8080/openapi
```

## 📋 Funcionalidades da Documentação

### Endpoints Documentados
- **Authentication**: Login e registro de professores e alunos
- **Admin**: Gestão de professores pelo administrador
- **Professores**: CRUD de planos, produtos e atribuição de alunos
- **Upload**: Sistema completo de upload de arquivos (fotos, vídeos, documentos)
- **Health Check**: Verificação de status da API

### Informações Incluídas
- **Schemas**: Modelos de dados de request/response
- **Exemplos**: Casos de uso práticos
- **Códigos de Erro**: Respostas para diferentes cenários
- **Autenticação**: Como usar tokens JWT
- **Validações**: Regras de negócio e validações

## 🔧 Estrutura dos Arquivos

```
server/src/main/
├── kotlin/com/example/treine_me/
│   └── plugins/OpenAPI.kt          # Configuração do Swagger
└── resources/
    └── openapi/documentation.yaml  # Especificação OpenAPI
```

## 📝 Padrão de Resposta da API

Todas as respostas seguem o padrão:

```json
{
  "success": true|false,
  "data": { ... },     // Presente apenas se success = true
  "error": {           // Presente apenas se success = false
    "message": "Mensagem clara para o usuário",
    "details": "Detalhes técnicos (opcional)",
    "field": "campo_com_erro" // Para erros de validação
  }
}
```

## 🛠️ Como testar os endpoints

### 1. Via Swagger UI
1. Acesse `http://localhost:8080/swagger`
2. Escolha o endpoint desejado
3. Clique em "Try it out"
4. Preencha os parâmetros
5. Clique em "Execute"

### 2. Via cURL
Exemplo de registro de professor:

```bash
curl -X POST "http://localhost:8080/auth/register/professor" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Maria Santos",
    "email": "maria@exemplo.com",
    "senha": "senhasegura123",
    "bio": "Professora de programação"
  }'
```

### 3. Via Postman
1. Importe a especificação OpenAPI: `http://localhost:8080/openapi`
2. O Postman criará automaticamente uma collection com todos os endpoints

## 🔐 Autenticação

Para endpoints protegidos:
1. Faça login via `/auth/login`
2. Copie o token retornado
3. No Swagger UI, clique em "Authorize" e cole o token
4. Ou adicione o header: `Authorization: Bearer SEU_TOKEN`

## 📁 Sistema de Upload de Arquivos

A documentação inclui endpoints completos para upload de arquivos:

### **Endpoints de Upload:**
- **`/upload/profile-image`** - Fotos de perfil (JPEG, PNG, WebP)
- **`/upload/course-cover`** - Capas de cursos (JPEG, PNG, WebP)  
- **`/upload/video`** - Vídeos de aulas (MP4, WebM, QuickTime)
- **`/upload/document`** - Documentos e e-books (PDF, DOC, DOCX)

### **Características:**
- ✅ Integração com Cloudflare R2
- ✅ Validação de tipos de arquivo
- ✅ Limite de 100MB por arquivo
- ✅ Renomeação automática para evitar conflitos
- ✅ Organização em pastas específicas
- ✅ URLs públicas para acesso direto

## 📚 Próximos Passos

A documentação será expandida conforme novos endpoints forem implementados:
- Gestão de módulos e aulas
- Inscrições de alunos
- Progresso do aluno
- Relatórios e analytics

## 🐛 Problemas Comuns

### Swagger UI não carrega
- Verifique se o servidor está rodando na porta 8080
- Confirme que o arquivo `documentation.yaml` existe em `resources/openapi/`

### Endpoints não aparecem
- Verifique se o plugin OpenAPI está registrado no `Application.kt`
- Confirme se as rotas estão definidas no arquivo YAML

### Erro de CORS
- O CORS está configurado para aceitar qualquer origem em desenvolvimento
- Em produção, configure origins específicos no `HTTP.kt`
