package com.example.rubberscan

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val AuthTeal     = Color(0xFF00BCD4)
private val AuthTealDark = Color(0xFF0097A7)
private val AuthFieldBg  = Color(0xFFF5F5F5)
private val AuthHint     = Color(0xFFAAAAAA)
private val AuthText     = Color(0xFF1C1C1C)

// ── Login Screen ─────────────────────────────────────────────
@Composable
fun LoginScreen(
    onLogin: () -> Unit = {},
    onSignUp: () -> Unit = {},
    onGoogleSignIn: () -> Unit = {}
) {
    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe      by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Illustration header ──────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .background(
                    Brush.verticalGradient(listOf(Color(0xFFE0F7FA), Color(0xFFB2EBF2)))
                ),
            contentAlignment = Alignment.Center
        ) {
            AuthLeafIllustration()
        }

        // ── Form ─────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
                .padding(top = 32.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Login",
                fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = AuthText)

            Spacer(Modifier.height(8.dp))

            val subtitle = buildAnnotatedString {
                append("Don't Have An Account? ")
                withStyle(SpanStyle(color = AuthTeal, fontWeight = FontWeight.SemiBold)) {
                    append("Sign Up")
                }
            }
            Text(subtitle, fontSize = 13.sp, color = AuthHint,
                modifier = Modifier.clickable { onSignUp() })

            Spacer(Modifier.height(28.dp))

            AuthInputField(
                value = email,
                onValueChange = { email = it },
                placeholder = "Enter your email address",
                keyboardType = KeyboardType.Email,
                leadingIcon = { Icon(Icons.Default.Email, null, tint = AuthHint, modifier = Modifier.size(18.dp)) }
            )

            Spacer(Modifier.height(14.dp))

            AuthInputField(
                value = password,
                onValueChange = { password = it },
                placeholder = "Password",
                isPassword = true,
                passwordVisible = passwordVisible,
                onTogglePassword = { passwordVisible = !passwordVisible },
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = AuthHint, modifier = Modifier.size(18.dp)) }
            )

            Spacer(Modifier.height(14.dp))

            // Remember me + Forgot password
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { rememberMe = !rememberMe }
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor   = AuthTeal,
                            uncheckedColor = AuthHint
                        ),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Remember Me", fontSize = 12.sp, color = AuthHint)
                }
                Text("Forgot Password?",
                    fontSize = 12.sp, color = AuthTeal,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { })
            }

            Spacer(Modifier.height(28.dp))

            // Login button
            AuthPrimaryButton(label = "Login", onClick = onLogin)

            Spacer(Modifier.height(24.dp))

            AuthDivider()

            Spacer(Modifier.height(20.dp))

            GoogleButton(onClick = onGoogleSignIn)
        }

    }
}

// ── Sign Up Screen ────────────────────────────────────────────
@Composable
fun SignUpScreen(
    onSignUp: () -> Unit = {},
    onLogin: () -> Unit = {},
    onGoogleSignIn: () -> Unit = {}
) {
    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible  by remember { mutableStateOf(false) }
    var rememberMe      by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Illustration header ──────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .background(
                    Brush.verticalGradient(listOf(Color(0xFFE0F7FA), Color(0xFFB2EBF2)))
                ),
            contentAlignment = Alignment.Center
        ) {
            AuthLeafIllustration()
        }

        // ── Form ─────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
                .padding(top = 32.dp, bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Sign Up",
                fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = AuthText)

            Spacer(Modifier.height(8.dp))

            val subtitle = buildAnnotatedString {
                append("Already Have An Account? ")
                withStyle(SpanStyle(color = AuthTeal, fontWeight = FontWeight.SemiBold)) {
                    append("Log In")
                }
            }
            Text(subtitle, fontSize = 13.sp, color = AuthHint,
                modifier = Modifier.clickable { onLogin() })

            Spacer(Modifier.height(28.dp))

            AuthInputField(
                value = email,
                onValueChange = { email = it },
                placeholder = "Enter your email address",
                keyboardType = KeyboardType.Email,
                leadingIcon = { Icon(Icons.Default.Email, null, tint = AuthHint, modifier = Modifier.size(18.dp)) }
            )

            Spacer(Modifier.height(14.dp))

            AuthInputField(
                value = password,
                onValueChange = { password = it },
                placeholder = "Password",
                isPassword = true,
                passwordVisible = passwordVisible,
                onTogglePassword = { passwordVisible = !passwordVisible },
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = AuthHint, modifier = Modifier.size(18.dp)) }
            )

            Spacer(Modifier.height(14.dp))

            AuthInputField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = "Confirm Password",
                isPassword = true,
                passwordVisible = confirmVisible,
                onTogglePassword = { confirmVisible = !confirmVisible },
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = AuthHint, modifier = Modifier.size(18.dp)) }
            )

            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { rememberMe = !rememberMe }
                ) {
                    Checkbox(
                        checked = rememberMe,
                        onCheckedChange = { rememberMe = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor   = AuthTeal,
                            uncheckedColor = AuthHint
                        ),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Remember Me", fontSize = 12.sp, color = AuthHint)
                }
                Text("Forgot Password?",
                    fontSize = 12.sp, color = AuthTeal,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { })
            }

            Spacer(Modifier.height(28.dp))

            AuthPrimaryButton(label = "Sign Up", onClick = onSignUp)

            Spacer(Modifier.height(24.dp))

            AuthDivider()

            Spacer(Modifier.height(20.dp))

            GoogleButton(onClick = onGoogleSignIn)
        }
    }
}

// ── Shared components ─────────────────────────────────────────

@Composable
fun AuthInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: @Composable () -> Unit = {},
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePassword: () -> Unit = {},
    keyboardType: KeyboardType = KeyboardType.Text
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, fontSize = 14.sp, color = AuthHint) },
        leadingIcon = { leadingIcon() },
        trailingIcon = if (isPassword) ({
            Icon(
                imageVector = if (passwordVisible) Icons.Default.Visibility
                              else Icons.Default.VisibilityOff,
                contentDescription = null,
                tint = AuthHint,
                modifier = Modifier.size(20.dp).clickable { onTogglePassword() }
            )
        }) else null,
        visualTransformation = if (isPassword && !passwordVisible)
            PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = AuthFieldBg,
            focusedContainerColor   = AuthFieldBg,
            unfocusedIndicatorColor = Color.Transparent,
            focusedIndicatorColor   = AuthTeal,
            cursorColor             = AuthTeal
        ),
        textStyle = TextStyle(fontSize = 14.sp, color = AuthText),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth().height(54.dp)
    )
}

@Composable
fun AuthPrimaryButton(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(50))
            .background(Brush.horizontalGradient(listOf(AuthTeal, AuthTealDark)))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(label, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun AuthDivider() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE0E0E0))
        Text("  OR  ", fontSize = 12.sp, color = AuthHint)
        HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFFE0E0E0))
    }
}

@Composable
fun GoogleButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(50))
            .background(Color.White)
            .border(1.5.dp, Color(0xFFE0E0E0), RoundedCornerShape(50))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painterResource(R.drawable.google),
                contentDescription = null,
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text("Continue with Google",
                fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = AuthText)
        }
    }
}

@Composable
fun AuthLeafIllustration() {
    Canvas(modifier = Modifier.size(150.dp)) {
        val s = size.width / 150f

        rotate(-15f, pivot = Offset(75f * s, 85f * s)) {
            drawOval(Color(0xFF4CAF50),
                topLeft = Offset(38f * s, 48f * s),
                size    = Size(74f * s, 104f * s))
        }
        rotate(-48f, pivot = Offset(42f * s, 90f * s)) {
            drawOval(Color(0xFF66BB6A),
                topLeft = Offset(18f * s, 58f * s),
                size    = Size(48f * s, 76f * s))
        }
        rotate(32f, pivot = Offset(108f * s, 86f * s)) {
            drawOval(Color(0xFF81C784),
                topLeft = Offset(84f * s, 52f * s),
                size    = Size(48f * s, 74f * s))
        }
        drawLine(
            color       = Color(0xFF2E7D32),
            start       = Offset(75f * s, 148f * s),
            end         = Offset(75f * s, 95f * s),
            strokeWidth = 3f * s,
            cap         = StrokeCap.Round
        )
    }
}
