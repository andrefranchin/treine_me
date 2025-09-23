package com.example.treine_me.ui.professor

sealed class ProfessorRoute {
    object Cursos : ProfessorRoute()
    data class CursoEdit(val id: String?) : ProfessorRoute()
    data class CursoDetail(val id: String) : ProfessorRoute()
    data class ModuloDetail(val produtoId: String, val moduloId: String) : ProfessorRoute()
    object Alunos : ProfessorRoute()
    data class AlunoForm(val id: String?) : ProfessorRoute() // null = criar, não null = editar
    data class AlunoDetail(val id: String) : ProfessorRoute()
    object Planos : ProfessorRoute()
    data class PlanoEdit(val id: String?) : ProfessorRoute()
}
