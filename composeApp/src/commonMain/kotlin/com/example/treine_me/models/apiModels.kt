package com.example.treine_me.models

// Reexport shared API models for client usage
typealias ApiError = com.example.treine_me.api.ApiError
typealias ApiResponse<T> = com.example.treine_me.api.ApiResponse<T>

typealias LoginRequest = com.example.treine_me.api.LoginRequest
typealias LoginResponse = com.example.treine_me.api.LoginResponse
typealias UserInfo = com.example.treine_me.api.UserInfo

typealias ProfessorCreateRequest = com.example.treine_me.api.ProfessorCreateRequest
typealias ProfessorResponse = com.example.treine_me.api.ProfessorResponse

typealias AlunoCreateRequest = com.example.treine_me.api.AlunoCreateRequest
typealias AlunoResponse = com.example.treine_me.api.AlunoResponse
