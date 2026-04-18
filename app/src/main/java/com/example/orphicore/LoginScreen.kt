package com.example.orphicore

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

// ── Orphicore palette ─────────────────────────────────────────────────────────
private val OcBg            = Color(0xFF09090F)
private val OcSurface       = Color(0xFF110F1A)
private val OcSurface2      = Color(0xFF1A1628)
private val OcPurpleLight   = Color(0xFFE8D5FF)
private val OcPurpleMid     = Color(0xFFA78BFA)
private val OcPurpleDeep    = Color(0xFF7C3AED)
private val OcPurpleDark    = Color(0xFF4C1D95)
private val OcPurpleFaint   = Color(0xFF2D1F4E)
private val OcTextPrimary   = Color(0xFFE8D5FF)
private val OcTextSecondary = Color(0xFF9580C8)
private val OcDivider       = Color(0xFF2A1F45)
private val OcAccentPink    = Color(0xFFE879F9)

@Composable
fun LoginScreen(navController: NavController) {

    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading       by remember { mutableStateOf(false) }

    val auth = FirebaseAuth.getInstance()

    // Pulsing glow animation
    val infiniteTransition = rememberInfiniteTransition(label = "loginGlow")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.35f, targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            tween(2400, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ), label = "pulse"
    )
    val rotateDeg by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(
            tween(12000, easing = LinearEasing), RepeatMode.Restart
        ), label = "rotate"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(OcBg)
    ) {

        // ── Decorative background canvas ──────────────────────────────────────
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height * 0.32f

            // Glow orb
            for (i in 4 downTo 1) {
                drawCircle(
                    color  = OcPurpleDeep.copy(alpha = 0.06f * i * pulse),
                    radius = (80 + i * 35).dp.toPx(),
                    center = Offset(cx, cy)
                )
            }
            // Concentric rings
            listOf(60f, 100f, 145f, 195f).forEachIndexed { i, r ->
                drawCircle(
                    color  = OcPurpleMid.copy(alpha = (0.15f - i * 0.025f) * pulse),
                    radius = r.dp.toPx(),
                    center = Offset(cx, cy),
                    style  = Stroke(width = 0.8.dp.toPx())
                )
            }

            // Corner brackets
            val pad = 24.dp.toPx()
            val len = 22.dp.toPx()
            val sw  = 1.dp.toPx()
            val col = toArgb(OcPurpleMid.copy(alpha = 0.3f))
            val colLong = toArgb(OcPurpleMid.copy(alpha = 0.3f))
            drawLine(OcPurpleMid.copy(alpha = 0.3f), Offset(pad, pad), Offset(pad + len, pad), sw)
            drawLine(OcPurpleMid.copy(alpha = 0.3f), Offset(pad, pad), Offset(pad, pad + len), sw)
            drawLine(OcPurpleMid.copy(alpha = 0.3f), Offset(size.width - pad, pad), Offset(size.width - pad - len, pad), sw)
            drawLine(OcPurpleMid.copy(alpha = 0.3f), Offset(size.width - pad, pad), Offset(size.width - pad, pad + len), sw)
            drawLine(OcPurpleMid.copy(alpha = 0.3f), Offset(pad, size.height - pad), Offset(pad + len, size.height - pad), sw)
            drawLine(OcPurpleMid.copy(alpha = 0.3f), Offset(pad, size.height - pad), Offset(pad, size.height - pad - len), sw)
            drawLine(OcPurpleMid.copy(alpha = 0.3f), Offset(size.width - pad, size.height - pad), Offset(size.width - pad - len, size.height - pad), sw)
            drawLine(OcPurpleMid.copy(alpha = 0.3f), Offset(size.width - pad, size.height - pad), Offset(size.width - pad, size.height - pad - len), sw)
        }

        // ── Content ───────────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(80.dp))

            // ── Logo mark ─────────────────────────────────────────────────────
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(72.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color  = OcPurpleDark.copy(alpha = 0.6f),
                        radius = 34.dp.toPx()
                    )
                    drawCircle(
                        color  = OcPurpleMid.copy(alpha = pulse * 0.8f),
                        radius = 34.dp.toPx(),
                        style  = Stroke(width = 1.2.dp.toPx())
                    )
                    drawCircle(
                        color  = OcAccentPink.copy(alpha = pulse * 0.5f),
                        radius = 26.dp.toPx(),
                        style  = Stroke(width = 0.7.dp.toPx())
                    )
                }
                Text(
                    text = "✦",
                    color = OcPurpleMid,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // App name
            Text(
                text = "orphicore",
                color = OcTextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 6.sp
            )
            Text(
                text = "dream · shadow · depth",
                color = OcTextSecondary,
                fontSize = 10.sp,
                letterSpacing = 3.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // ── Card ──────────────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(OcSurface)
                    .border(1.dp, OcDivider, RoundedCornerShape(24.dp))
                    .padding(24.dp)
            ) {

                // Section label
                Text(
                    text = "W E L C O M E   B A C K",
                    color = OcPurpleDeep,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 4.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Sign in to continue",
                    color = OcTextSecondary,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Email field
                OcTextField(
                    value       = email,
                    onValueChange = { email = it },
                    label       = "Email",
                    leadingIcon = Icons.Default.Email
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Password field
                OcTextField(
                    value           = password,
                    onValueChange   = { password = it },
                    label           = "Password",
                    leadingIcon     = Icons.Default.Lock,
                    isPassword      = true,
                    passwordVisible = passwordVisible,
                    onTogglePassword = { passwordVisible = !passwordVisible }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Forgot password
                Text(
                    text = "Forgot password?",
                    color = OcPurpleMid,
                    fontSize = 12.sp,
                    modifier = Modifier.align(Alignment.End)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Login button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(OcPurpleDeep, OcPurpleDark)
                            )
                        )
                        .border(1.dp, OcPurpleMid.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        .clickable(enabled = !isLoading) {
                            if (email.isEmpty() || password.isEmpty()) {
                                Toast.makeText(navController.context, "Fill all fields", Toast.LENGTH_SHORT).show()
                                return@clickable
                            }
                            isLoading = true
                            auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener {
                                    isLoading = false
                                    if (it.isSuccessful) {
                                        navController.navigate("home")
                                    } else {
                                        Toast.makeText(navController.context, it.exception?.message ?: "Login failed", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                        .padding(vertical = 14.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = OcPurpleLight,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = "Sign In",
                            color = OcPurpleLight,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Divider
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = OcDivider, thickness = 0.5.dp)
                    Text(
                        "  or  ",
                        color = OcTextSecondary,
                        fontSize = 11.sp
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = OcDivider, thickness = 0.5.dp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sign up redirect
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Don't have an account? ",
                        color = OcTextSecondary,
                        fontSize = 13.sp
                    )
                    Text(
                        "Sign Up",
                        color = OcPurpleMid,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { navController.navigate("signup") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Footer
            Text(
                text = "✦   orphicore   ✦",
                color = OcTextSecondary.copy(alpha = 0.3f),
                fontSize = 9.sp,
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ── Shared text field ─────────────────────────────────────────────────────────
@Composable
fun OcTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePassword: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(OcSurface2)
            .border(1.dp, OcDivider, RoundedCornerShape(12.dp))
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(label, color = OcTextSecondary.copy(alpha = 0.5f), fontSize = 13.sp) },
            leadingIcon = {
                Icon(leadingIcon, contentDescription = null, tint = OcPurpleMid, modifier = Modifier.size(18.dp))
            },
            trailingIcon = if (isPassword) ({
                IconButton(onClick = { onTogglePassword?.invoke() }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                        contentDescription = null,
                        tint = OcTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }) else null,
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            colors = TextFieldDefaults.colors(
                focusedContainerColor   = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor   = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor        = OcTextPrimary,
                unfocusedTextColor      = OcTextPrimary,
                cursorColor             = OcPurpleMid
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}

private fun toArgb(color: Color): Int = color.toArgb()