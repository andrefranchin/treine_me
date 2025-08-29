package com.example.treine_me.database

import com.example.treine_me.models.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseConfig {
    fun init() {
        val driverClassName = "org.h2.Driver"
        val jdbcURL = "jdbc:h2:file:./build/db"
        val database = Database.connect(jdbcURL, driverClassName)
        
        transaction(database) {
            SchemaUtils.create(
                Admins,
                Professores,
                Alunos,
                Planos,
                Produtos,
                Inscricoes,
                ProdutoPlanos,
                Modulos,
                Aulas,
                Conteudos
            )
        }
    }
    
    fun initPostgreSQL(
        host: String = "localhost",
        port: Int = 5432,
        database: String = "treine_me",
        user: String = "postgres",
        password: String = ""
    ) {
        val jdbcURL = "jdbc:postgresql://$host:$port/$database"
        val db = Database.connect(
            url = jdbcURL,
            driver = "org.postgresql.Driver",
            user = user,
            password = password
        )
        
        transaction(db) {
            SchemaUtils.create(
                Admins,
                Professores,
                Alunos,
                Planos,
                Produtos,
                Inscricoes,
                ProdutoPlanos,
                Modulos,
                Aulas,
                Conteudos
            )
        }
    }
}
