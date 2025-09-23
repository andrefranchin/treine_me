package com.example.treine_me.config

/**
 * Configurações específicas para o app do aluno.
 * Centraliza todas as configurações que podem variar por ambiente.
 */
object AlunoConfig {
    /**
     * ID do professor que este app do aluno está configurado para acessar.
     * Este valor deve ser configurado antes do build ou no início da aplicação.
     */
    var professorId: String = "ba273d71-9f1b-4c1e-b732-dff3913750e1" // ID padrão para desenvolvimento
    
    /**
     * Configura o ID do professor para este app.
     * Deve ser chamado no início da aplicação ou durante a configuração do build.
     */
    fun configureProfessorId(id: String) {
        professorId = id
    }
    
    /**
     * Valida se o ID do professor está configurado corretamente.
     */
    fun isValidProfessorId(): Boolean {
        return professorId.isNotBlank() && professorId.length >= 10 // Validação básica
    }
}
