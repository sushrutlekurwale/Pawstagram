package com.example.pawstagram.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.width
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale

@Composable
fun SignInScreen(
    onSignIn: (email: String, password: String) -> Unit,
    onSignUp: (email: String, password: String, username: String) -> Unit,
    onResetPassword: (email: String) -> Unit,
    onToggleDarkMode: () -> Unit,
    isDarkMode: Boolean = false,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSignUpMode by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }
    var showResetPassword by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
    ) {
        // Dark mode toggle button at top right
        IconButton(
            onClick = onToggleDarkMode,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = if (isDarkMode) Icons.Filled.DarkMode else Icons.Outlined.DarkMode,
                contentDescription = "Toggle Dark Mode",
                tint = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
            )
        }
        
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        Image(
            painter = painterResource(id = com.example.pawstagram.R.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .height(100.dp)
                .width(100.dp)
                .padding(bottom = 16.dp),
            contentScale = ContentScale.Fit
        )
        
        // Toggle between Sign In and Sign Up
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Sign In Button
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (!isSignUpMode) androidx.compose.material3.MaterialTheme.colorScheme.primary else Color.Transparent
                    )
                    .clickable { 
                        isSignUpMode = false
                        email = ""
                        password = ""
                        username = ""
                    }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Sign In",
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                    fontWeight = if (!isSignUpMode) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal,
                    color = if (!isSignUpMode) androidx.compose.material3.MaterialTheme.colorScheme.onPrimary else androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Sign Up Button
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isSignUpMode) androidx.compose.material3.MaterialTheme.colorScheme.primary else Color.Transparent
                    )
                    .clickable { 
                        isSignUpMode = true
                        email = ""
                        password = ""
                        username = ""
                    }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Sign Up",
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                    fontWeight = if (isSignUpMode) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal,
                    color = if (isSignUpMode) androidx.compose.material3.MaterialTheme.colorScheme.onPrimary else androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = if (isSignUpMode) "Create Your Account" else "Welcome Back",
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = androidx.compose.material3.MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = if (isSignUpMode) "Join Pawstagram today!" else "Sign in to continue",
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
            color = androidx.compose.material3.MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                modifier = Modifier.padding(bottom = 16.dp),
                textAlign = TextAlign.Center,
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                color = androidx.compose.material3.MaterialTheme.colorScheme.error
            )
        }

        if (isSignUpMode) {
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { 
                    Text(
                        "Username",
                        style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                    ) 
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                singleLine = true,
                textStyle = androidx.compose.material3.MaterialTheme.typography.bodyLarge.copy(
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
                ),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                    focusedLabelColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { 
                Text(
                    "Email",
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                ) 
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            textStyle = androidx.compose.material3.MaterialTheme.typography.bodyLarge.copy(
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
            ),
            colors = TextFieldDefaults.colors(
                focusedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                focusedLabelColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { 
                Text(
                    "Password",
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                ) 
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            textStyle = androidx.compose.material3.MaterialTheme.typography.bodyLarge.copy(
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
            ),
            colors = TextFieldDefaults.colors(
                focusedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                focusedLabelColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
            )
        )

        if (!isSignUpMode && !showResetPassword) {
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = { showResetPassword = true }) {
                Text(
                    "Forgot Password?",
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.primary
                )
            }
        }

        if (showResetPassword) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    if (email.isNotBlank()) {
                        onResetPassword(email)
                        showResetPassword = false
                    }
                },
                enabled = !isLoading && email.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.padding(8.dp))
                } else {
                    Text(
                        "Send Reset Email",
                        style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                    )
                }
            }
            TextButton(onClick = { showResetPassword = false }) {
                Text(
                    "Back to Sign In",
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.primary
                )
            }
        } else {
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (isSignUpMode) {
                        onSignUp(email, password, username)
                    } else {
                        onSignIn(email, password)
                    }
                },
                enabled = !isLoading && email.isNotBlank() && password.isNotBlank() && (!isSignUpMode || username.isNotBlank()),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.padding(8.dp))
                } else {
                    Text(
                        if (isSignUpMode) "Sign Up" else "Sign In",
                        style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                }
            }

        }
        }
    }
}


