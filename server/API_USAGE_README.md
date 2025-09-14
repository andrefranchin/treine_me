# 🚀 Treine Me API - Guia de Uso

## 📋 Resumo do Sistema Implementado

### ✅ **Funcionalidades Implementadas**

1. **Sistema de Admin** - Você pode cadastrar professores
2. **Autenticação JWT** - Login para Admin, Professor e Aluno
3. **CRUD de Professores** - Gerenciamento completo pelo admin
4. **CRUD de Planos** - Professores criam seus planos de assinatura
5. **CRUD de Produtos** - Professores criam cursos, e-books, mentorias
6. **Associação Produto-Plano** - Define quais produtos cada plano inclui
7. **Atribuição Manual de Planos** - Professor atribui planos aos alunos
8. **Upload de Arquivos** - Integração com Cloudflare R2
9. **Storage Abstrato** - Fácil troca de provedor de storage

## 🔑 **Credenciais Padrão**

**Admin (você):**
- **Email:** `dedefranchin@gmail.com`
- **Senha:** `bebaleite`

## 🌐 **Configuração do Cloudflare R2**

Para usar o storage, configure as variáveis de ambiente:

```bash
export R2_ACCESS_KEY_ID="sua_access_key"
export R2_SECRET_ACCESS_KEY="sua_secret_key"
```

**Bucket configurado:** `https://24be5a76d99e172619714a8eb94b63d9.r2.cloudflarestorage.com/treine-me`

## 🚀 **Como Usar**

### 1. **Iniciar o Servidor**
```bash
cd server
./gradlew run
```

### 2. **Acessar Documentação**
- **Swagger UI:** http://localhost:8080/swagger
- **OpenAPI:** http://localhost:8080/openapi

### 3. **Fluxo de Trabalho**

#### **Passo 1: Login como Admin**
```bash
POST /auth/login
{
  "email": "dedefranchin@gmail.com",
  "senha": "bebaleite"
}
```

#### **Passo 2: Cadastrar Professor**
```bash
POST /admin/professores
Authorization: Bearer {seu_token_admin}
{
  "nome": "João Silva",
  "email": "joao@exemplo.com",
  "senha": "senha123",
  "bio": "Professor de programação"
}
```

#### **Passo 3: Professor faz Login**
```bash
POST /auth/login
{
  "email": "joao@exemplo.com",
  "senha": "senha123"
}
```

#### **Passo 4: Professor cria Plano**
```bash
POST /professores/me/planos
Authorization: Bearer {token_professor}
{
  "nome": "Plano Básico",
  "descricao": "Acesso a cursos básicos",
  "valor": "29.90",
  "recorrencia": "MENSAL"
}
```

#### **Passo 5: Professor cria Produto (Curso)**
```bash
POST /professores/me/produtos
Authorization: Bearer {token_professor}
{
  "titulo": "JavaScript Fundamentals",
  "descricao": "Aprenda JavaScript do zero",
  "tipo": "CURSO",
  "capaUrl": "https://exemplo.com/capa.jpg"
}
```

#### **Passo 6: Associar Produto ao Plano**
```bash
POST /professores/me/planos/{planoId}/produtos/{produtoId}
Authorization: Bearer {token_professor}
```

#### **Passo 7: Atribuir Plano a Aluno**
```bash
POST /professores/me/planos/{planoId}/alunos/{alunoId}
Authorization: Bearer {token_professor}
```

## 📁 **Sistema de Upload de Arquivos**

A API possui um sistema completo de upload integrado com Cloudflare R2 para gerenciar todos os tipos de mídia da plataforma.

### **Tipos de Upload Disponíveis:**

#### **Upload Básico:**

##### **1. Upload de Imagem de Perfil**
```bash
POST /upload/profile-image
Authorization: Bearer {token}
Content-Type: multipart/form-data

# Arquivo: campo "file"
# Tipos aceitos: JPEG, PNG, WebP
# Tamanho máximo: 100MB
```

##### **2. Upload de Capa de Curso**
```bash
POST /upload/course-cover
Authorization: Bearer {token}
Content-Type: multipart/form-data

# Arquivo: campo "file"
# Tipos aceitos: JPEG, PNG, WebP
# Recomendado: 1200x630px
```

##### **3. Upload de Vídeo para Aulas**
```bash
POST /upload/video
Authorization: Bearer {token}
Content-Type: multipart/form-data

# Arquivo: campo "file"
# Tipos aceitos: MP4, WebM, QuickTime
# Recomendado: MP4 com codec H.264
```

##### **4. Upload de Documento/E-book**
```bash
POST /upload/document
Authorization: Bearer {token}
Content-Type: multipart/form-data

# Arquivo: campo "file"
# Tipos aceitos: PDF, DOC, DOCX
# PDFs vão para pasta "ebooks", outros para "course-files"
```

#### **Upload Contextual (Novo):**

##### **5. Galeria de Curso**
```bash
POST /upload/course-gallery
Authorization: Bearer {token}
Content-Type: multipart/form-data

# Arquivo: campo "file"
# Múltiplas fotos permitidas
# Armazenado em: course-gallery/
```

##### **6. Capa de Módulo**
```bash
POST /upload/module-cover
Authorization: Bearer {token}
Content-Type: multipart/form-data

# Arquivo: campo "file"
# Armazenado em: module-covers/
```

##### **7. Galeria de Módulo**
```bash
POST /upload/module-gallery
Authorization: Bearer {token}
Content-Type: multipart/form-data

# Arquivo: campo "file"
# Armazenado em: module-gallery/
```

##### **8. Capa de Aula**
```bash
POST /upload/lesson-cover
Authorization: Bearer {token}
Content-Type: multipart/form-data

# Arquivo: campo "file"
# Armazenado em: lesson-covers/
```

##### **9. Galeria de Aula**
```bash
POST /upload/lesson-gallery
Authorization: Bearer {token}
Content-Type: multipart/form-data

# Arquivo: campo "file"
# Armazenado em: lesson-gallery/
```

##### **10. Fotos Gerais**
```bash
POST /upload/general-photos
Authorization: Bearer {token}
Content-Type: multipart/form-data

# Arquivo: campo "file"
# Uso flexível
# Armazenado em: general-photos/
```

### **Estrutura de Pastas no Storage:**
```
treine-me/
├── profile-images/     # Fotos de perfil de usuários
├── course-covers/      # Capas de cursos e produtos
├── course-gallery/     # Galeria de fotos do curso
├── course-videos/      # Vídeos de aulas e conteúdo
├── course-files/       # Materiais complementares (DOC, DOCX)
├── module-covers/      # Capas de módulos
├── module-gallery/     # Galeria de fotos do módulo
├── lesson-covers/      # Capas de aulas
├── lesson-gallery/     # Galeria de fotos da aula
├── general-photos/     # Fotos de uso geral
└── ebooks/            # E-books em PDF
```

### **Fluxo de Trabalho com Uploads:**

1. **Faça o upload** do arquivo usando o endpoint apropriado
2. **Receba a URL** retornada pela API
3. **Use a URL** nos campos correspondentes ao criar/editar recursos
4. **Arquivos são organizados** automaticamente em pastas específicas

### **Exemplo Prático:**

```bash
# 1. Upload da capa do curso
curl -X POST "http://localhost:8080/upload/course-cover" \
  -H "Authorization: Bearer SEU_TOKEN" \
  -F "file=@capa_curso.jpg"

# Resposta:
# {
#   "success": true,
#   "data": {
#     "fileName": "course_cover_1703123456_def456.jpg",
#     "url": "https://24be5a76d99e172619714a8eb94b63d9.r2.cloudflarestorage.com/treine-me/course-covers/course_cover_1703123456_def456.jpg",
#     "contentType": "image/jpeg",
#     "size": 2048000
#   }
# }

# 2. Criar produto usando a URL da capa
curl -X POST "http://localhost:8080/professores/me/produtos" \
  -H "Authorization: Bearer SEU_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "JavaScript Fundamentals",
    "descricao": "Aprenda JavaScript do zero",
    "tipo": "CURSO",
    "capaUrl": "https://24be5a76d99e172619714a8eb94b63d9.r2.cloudflarestorage.com/treine-me/course-covers/course_cover_1703123456_def456.jpg"
  }'
```

### **Características do Sistema:**
- ✅ **Renomeação automática** para evitar conflitos
- ✅ **Validação de tipos** de arquivo
- ✅ **Limite de tamanho** (100MB por arquivo)
- ✅ **Organização automática** em pastas
- ✅ **URLs públicas** para acesso direto
- ✅ **Integração completa** com Cloudflare R2

## 🔧 **Endpoints Principais**

### **Admin**
- `POST /admin/professores` - Criar professor
- `GET /admin/professores` - Listar professores
- `GET /admin/professores/{id}` - Ver professor
- `PUT /admin/professores/{id}` - Editar professor
- `DELETE /admin/professores/{id}` - Desativar professor
- `POST /admin/professores/{id}/reset-password` - Resetar senha

### **Professor - Planos**
- `POST /professores/me/planos` - Criar plano
- `GET /professores/me/planos` - Listar meus planos
- `GET /professores/me/planos/{id}` - Ver plano
- `PUT /professores/me/planos/{id}` - Editar plano
- `DELETE /professores/me/planos/{id}` - Excluir plano

### **Professor - Produtos**
- `POST /professores/me/produtos` - Criar produto
- `GET /professores/me/produtos` - Listar meus produtos
- `GET /professores/me/produtos/{id}` - Ver produto
- `PUT /professores/me/produtos/{id}` - Editar produto
- `DELETE /professores/me/produtos/{id}` - Excluir produto

### **Professor - Associações**
- `GET /professores/me/planos/{id}/produtos` - Ver produtos do plano
- `POST /professores/me/planos/{planoId}/produtos/{produtoId}` - Adicionar produto ao plano
- `DELETE /professores/me/planos/{planoId}/produtos/{produtoId}` - Remover produto do plano

### **Professor - Atribuir Planos**
- `POST /professores/me/planos/{planoId}/alunos/{alunoId}` - Dar plano ao aluno

## 📊 **Tipos de Dados**

### **Enums Disponíveis**
- **UserRole:** `ADMIN`, `PROFESSOR`, `ALUNO`
- **Recorrencia:** `MENSAL`, `ANUAL`
- **TipoProduto:** `CURSO`, `MENTORIA`, `EBOOK`
- **StatusInscricao:** `ATIVA`, `CANCELADA`, `PENDENTE`, `EXPIRADA`
- **TipoConteudo:** `VIDEO`, `TEXTO`, `ATIVIDADE`

### **Estrutura de Resposta**
Todas as respostas seguem o padrão:
```json
{
  "success": true|false,
  "data": { ... },     // Se success = true
  "error": {           // Se success = false
    "message": "Mensagem para usuário",
    "details": "Detalhes técnicos",
    "field": "campo_com_erro"
  }
}
```

## 🔄 **Trocar Provedor de Storage**

Para trocar o Cloudflare R2 por outro provedor:

1. **Crie uma nova implementação de `StorageService`:**
```kotlin
class AWSS3Service : StorageService {
    // Implementar métodos
}
```

2. **Substitua no `FileUploadRoutes.kt`:**
```kotlin
val fileUploadService = FileUploadService(AWSS3Service()) // Em vez de CloudflareR2Service()
```

## 📚 **Estrutura de Conteúdo (Módulos e Aulas)**

A plataforma suporta uma estrutura hierárquica de conteúdo para organizar cursos:

### **Hierarquia:**
```
Produto (Curso)
├── Módulo 1
│   ├── Aula 1.1 (Vídeo)
│   ├── Aula 1.2 (Texto)
│   └── Aula 1.3 (Atividade)
├── Módulo 2
│   ├── Aula 2.1 (Vídeo)
│   └── Aula 2.2 (Documento)
└── ...
```

### **Tipos de Conteúdo de Aula:**
- **VIDEO** - Aulas em vídeo (usar `/upload/video`)
- **TEXTO** - Conteúdo em Markdown
- **ATIVIDADE** - Exercícios e atividades práticas

### **Fluxo de Criação de Conteúdo:**

1. **Criar Produto** (curso, e-book, mentoria)
2. **Criar Módulos** dentro do produto
3. **Criar Aulas** dentro dos módulos
4. **Adicionar Conteúdo** (vídeo, texto, arquivo) às aulas
5. **Associar Produto** aos planos

### **Exemplo de Uso:**

```bash
# 1. Criar produto (curso)
curl -X POST "http://localhost:8080/professores/me/produtos" \
  -H "Authorization: Bearer SEU_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "JavaScript Completo",
    "descricao": "Curso completo de JavaScript",
    "tipo": "CURSO"
  }'

# 2. Criar módulo no produto
curl -X POST "http://localhost:8080/professores/me/produtos/{produtoId}/modulos" \
  -H "Authorization: Bearer SEU_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Fundamentos",
    "descricao": "Conceitos básicos do JavaScript",
    "ordem": 1
  }'

# 3. Criar aula no módulo
curl -X POST "http://localhost:8080/professores/me/modulos/{moduloId}/aulas" \
  -H "Authorization: Bearer SEU_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Variáveis e Tipos",
    "descricao": "Aprenda sobre declaração de variáveis",
    "ordem": 1,
    "tipoConteudo": "VIDEO"
  }'

# 4. Upload de vídeo para a aula
curl -X POST "http://localhost:8080/upload/video" \
  -H "Authorization: Bearer SEU_TOKEN" \
  -F "file=@aula_variaveis.mp4"

# 5. Adicionar conteúdo à aula
curl -X POST "http://localhost:8080/professores/me/aulas/{aulaId}/conteudo" \
  -H "Authorization: Bearer SEU_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "urlVideo": "https://24be5a76d99e172619714a8eb94b63d9.r2.cloudflarestorage.com/treine-me/course-videos/video_1703123456_ghi789.mp4"
  }'
```

## 🎯 **Próximos Passos Sugeridos**

1. **Área do Aluno** - Endpoints para alunos acessarem conteúdo
2. **Progresso do Aluno** - Sistema de tracking de progresso
3. **Notificações** - Email/push quando aluno recebe novo plano
4. **Relatórios** - Analytics para professores
5. **Pagamentos** - Integração com gateway de pagamento
6. **Certificados** - Geração automática ao completar cursos

## 🐛 **Resolução de Problemas**

### **Erro de CORS**
- CORS está configurado para aceitar qualquer origem em desenvolvimento

### **Erro de JWT**
- Verifique se o token está sendo enviado no header: `Authorization: Bearer {token}`

### **Erro de Upload**
- Verifique as variáveis de ambiente do R2
- Confirme que o arquivo não excede 100MB
- Verifique se o tipo de arquivo é permitido

### **Banco de Dados**
- O H2 cria automaticamente o arquivo em `./build/db`
- Para resetar: delete a pasta `build/db*`

## 📞 **Suporte**

O sistema está completamente funcional e documentado. Todas as funcionalidades solicitadas foram implementadas:

✅ **Admin cadastra professores**  
✅ **Professor gerencia seus planos/cursos**  
✅ **Professor atribui planos manualmente aos alunos**  
✅ **Storage com Cloudflare R2 (facilmente substituível)**  
✅ **Upload de arquivos (fotos, vídeos, documentos)**  
✅ **Documentação Swagger completa**  

Agora você pode começar a usar o sistema! 🎉
