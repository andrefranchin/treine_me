# 🚀 Como Usar a Documentação da API

## ⚡ Início Rápido

### 1. Inicie o servidor
```bash
cd server
./gradlew run
```

### 2. Acesse a documentação
- **Swagger UI**: http://localhost:8080/swagger
- **OpenAPI YAML**: http://localhost:8080/openapi

## 📖 Opções de Documentação

### 🌐 Swagger UI (Recomendado)
- **URL**: `http://localhost:8080/swagger`
- **Vantagens**: 
  - Interface visual intuitiva
  - Teste endpoints diretamente no navegador
  - Exemplos de código automáticos
  - Não precisa instalar nada

### 📱 Postman
1. Abra o Postman
2. Importe a coleção: `server/postman/Treine_Me_API.postman_collection.json`
3. Configure a variável `baseUrl` para `http://localhost:8080`
4. Pronto para usar!

### 🔧 Insomnia
1. Abra o Insomnia
2. Importe via URL: `http://localhost:8080/openapi`
3. Escolha "OpenAPI 3.0"

### 💻 Outras ferramentas
Qualquer ferramenta que suporte OpenAPI 3.0 pode importar via:
`http://localhost:8080/openapi`

## 🎯 Testando a API

### Exemplo 1: Registrar Professor
```bash
curl -X POST "http://localhost:8080/auth/register/professor" \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João Silva",
    "email": "joao@exemplo.com", 
    "senha": "minhasenha123"
  }'
```

### Exemplo 2: Fazer Login
```bash
curl -X POST "http://localhost:8080/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "joao@exemplo.com",
    "senha": "minhasenha123"
  }'
```

## 📁 **Sistema de Upload de Arquivos**

A documentação inclui endpoints completos para upload de arquivos:

### **Endpoints de Upload Disponíveis:**
- **`/upload/profile-image`** - Fotos de perfil (JPEG, PNG, WebP)
- **`/upload/course-cover`** - Capas de cursos (JPEG, PNG, WebP)
- **`/upload/video`** - Vídeos de aulas (MP4, WebM, QuickTime)
- **`/upload/document`** - Documentos e e-books (PDF, DOC, DOCX)

### **Características:**
- ✅ Integração com Cloudflare R2
- ✅ Validação de tipos de arquivo
- ✅ Limite de 100MB por arquivo
- ✅ Renomeação automática
- ✅ Organização em pastas específicas

## 📚 **Estrutura de Conteúdo**

A documentação inclui schemas para:
- **Módulos** - Organização hierárquica de conteúdo
- **Aulas** - Unidades de aprendizado
- **Conteúdo** - Vídeos, textos e arquivos das aulas

## 📝 Próximos Endpoints

A documentação será atualizada automaticamente conforme você implementar:
- `/professores/me` - Dados do professor logado
- `/planos` - Gestão de planos
- `/produtos` - Gestão de cursos
- `/alunos/me/inscricoes` - Inscrições do aluno

## ❓ Dúvidas?

Consulte o arquivo detalhado: `server/SWAGGER_README.md`
