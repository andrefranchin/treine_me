package com.example.treine_me

import androidx.compose.ui.window.ComposeUIViewController
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

fun MainViewController() = ComposeUIViewController { App() }

@Composable
private fun RoleOnlyScreenDeprecated() { /* kept for reference; not used */ }