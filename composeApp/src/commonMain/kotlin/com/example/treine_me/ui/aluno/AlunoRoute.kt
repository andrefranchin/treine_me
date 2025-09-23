package com.example.treine_me.ui.aluno

sealed class AlunoRoute {
    object Welcome : AlunoRoute()
    object Login : AlunoRoute()
    object ExploreClasses : AlunoRoute()
    data class Training(val produtoId: String) : AlunoRoute()
}
