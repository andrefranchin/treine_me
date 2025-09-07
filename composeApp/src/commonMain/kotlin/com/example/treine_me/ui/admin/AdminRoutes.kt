package com.example.treine_me.ui.admin

sealed interface AdminRoute {
    data object Professores : AdminRoute
    data class ProfessorEdit(val id: String? = null) : AdminRoute
}


