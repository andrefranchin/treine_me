package com.example.treine_me.exceptions

sealed class AppException(message: String, cause: Throwable? = null) : Exception(message, cause)

class ValidationException(message: String, val field: String? = null) : AppException(message)

class AuthenticationException(message: String) : AppException(message)

class AuthorizationException(message: String) : AppException(message)

class NotFoundException(message: String) : AppException(message)

class ConflictException(message: String) : AppException(message)

class ForbiddenException(message: String) : AppException(message)

class BusinessException(message: String) : AppException(message)
