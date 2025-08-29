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
- **Email:** `admin@treine-me.com`
- **Senha:** `admin123`

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
  "email": "admin@treine-me.com",
  "senha": "admin123"
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

## 📁 **Endpoints de Upload**

### **Upload de Imagem de Perfil**
```bash
POST /upload/profile-image
Authorization: Bearer {token}
Content-Type: multipart/form-data

# Arquivo: campo "file"
```

### **Upload de Capa de Curso**
```bash
POST /upload/course-cover
Authorization: Bearer {token}
Content-Type: multipart/form-data
```

### **Upload de Vídeo**
```bash
POST /upload/video
Authorization: Bearer {token}
Content-Type: multipart/form-data
```

### **Upload de Documento/E-book**
```bash
POST /upload/document
Authorization: Bearer {token}
Content-Type: multipart/form-data
```

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

## 🎯 **Próximos Passos Sugeridos**

1. **Módulos e Aulas** - Implementar CRUD para estruturar os cursos
2. **Área do Aluno** - Endpoints para alunos acessarem conteúdo
3. **Progresso do Aluno** - Sistema de tracking de progresso
4. **Notificações** - Email/push quando aluno recebe novo plano
5. **Relatórios** - Analytics para professores
6. **Pagamentos** - Integração com gateway de pagamento
7. **Certificados** - Geração automática ao completar cursos

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
