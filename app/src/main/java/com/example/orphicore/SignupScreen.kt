package com.example.orphicore

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
private val SOcBg            = Color(0xFF09090F)
private val SOcSurface       = Color(0xFF110F1A)
private val SOcSurface2      = Color(0xFF1A1628)
private val SOcPurpleLight   = Color(0xFFE8D5FF)
private val SOcPurpleMid     = Color(0xFFA78BFA)
private val SOcPurpleDeep    = Color(0xFF7C3AED)
private val SOcPurpleDark    = Color(0xFF4C1D95)
private val SOcPurpleFaint   = Color(0xFF2D1F4E)
private val SOcTextPrimary   = Color(0xFFE8D5FF)
private val SOcTextSecondary = Color(0xFF9580C8)
private val SOcDivider       = Color(0xFF2A1F45)
private val SOcAccentPink    = Color(0xFFE879F9)

@Composable
fun SignupScreen(navController: NavController) {

    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible  by remember { mutableStateOf(false) }
    var isLoading       by remember { mutableStateOf(false) }

    val auth = FirebaseAuth.getInstance()

    val infiniteTransition = rememberInfiniteTransition(label = "signupGlow")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            tween(2600, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ), label = "pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SOcBg)
    ) {

        // ── Decorative background canvas ──────────────────────────────────────
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height * 0.28f

            for (i in 4 downTo 1) {
                drawCircle(
                    color  = SOcPurpleDeep.copy(alpha = 0.05f * i * pulse),
                    radius = (70 + i * 32).dp.toPx(),
                    center = Offset(cx, cy)
                )
            }
            listOf(55f, 95f, 135f, 180f).forEachIndexed { i, r ->
                drawCircle(
                    color  = SOcPurpleMid.copy(alpha = (0.14f - i * 0.02f) * pulse),
                    radius = r.dp.toPx(),
                    center = Offset(cx, cy),
                    style  = Stroke(width = 0.8.dp.toPx())
                )
            }

            // Corner brackets
            val pad = 24.dp.toPx()
            val len = 22.dp.toPx()
            val sw  = 1.dp.toPx()
            val col = SOcPurpleMid.copy(alpha = 0.28f)
            drawLine(col, Offset(pad, pad), Offset(pad + len, pad), sw)
            drawLine(col, Offset(pad, pad), Offset(pad, pad + len), sw)
            drawLine(col, Offset(size.width - pad, pad), Offset(size.width - pad - len, pad), sw)
            drawLine(col, Offset(size.width - pad, pad), Offset(size.width - pad, pad + len), sw)
            drawLine(col, Offset(pad, size.height - pad), Offset(pad + len, size.height - pad), sw)
            drawLine(col, Offset(pad, size.height - pad), Offset(pad, size.height - pad - len), sw)
            drawLine(col, Offset(size.width - pad, size.height - pad), Offset(size.width - pad - len, size.height - pad), sw)
            drawLine(col, Offset(size.width - pad, size.height - pad), Offset(size.width - pad, size.height - pad - len), sw)

            // Pink accent sparkle top-right
            val sx = size.width - 48.dp.toPx()
            val sy = 60.dp.toPx()
            val sl = 7.dp.toPx()
            drawLine(SOcAccentPink.copy(alpha = 0.5f * pulse), Offset(sx, sy - sl), Offset(sx, sy + sl), 1.dp.toPx())
            drawLine(SOcAccentPink.copy(alpha = 0.5f * pulse), Offset(sx - sl, sy), Offset(sx + sl, sy), 1.dp.toPx())
            drawLine(SOcAccentPink.copy(alpha = 0.3f * pulse), Offset(sx - sl * 0.7f, sy - sl * 0.7f), Offset(sx + sl * 0.7f, sy + sl * 0.7f), 0.8.dp.toPx())
            drawLine(SOcAccentPink.copy(alpha = 0.3f * pulse), Offset(sx + sl * 0.7f, sy - sl * 0.7f), Offset(sx - sl * 0.7f, sy + sl * 0.7f), 0.8.dp.toPx())
        }

        // ── Scrollable content ────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(72.dp))

            // ── Logo mark ─────────────────────────────────────────────────────
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(72.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(color = SOcPurpleDark.copy(alpha = 0.6f), radius = 34.dp.toPx())
                    drawCircle(
                        color  = SOcAccentPink.copy(alpha = pulse * 0.8f),
                        radius = 34.dp.toPx(),
                        style  = Stroke(width = 1.2.dp.toPx())
                    )
                    drawCircle(
                        color  = SOcPurpleMid.copy(alpha = pulse * 0.5f),
                        radius = 26.dp.toPx(),
                        style  = Stroke(width = 0.7.dp.toPx())
                    )
                }
                Text("✦", color = SOcAccentPink, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "orphicore",
                color = SOcTextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 6.sp
            )
            Text(
                text = "dream · shadow · depth",
                color = SOcTextSecondary,
                fontSize = 10.sp,
                letterSpacing = 3.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // ── Card ──────────────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(SOcSurface)
                    .border(1.dp, SOcDivider, RoundedCornerShape(24.dp))
                    .padding(24.dp)
            ) {

                Text(
                    text = "C R E A T E   A C C O U N T",
                    color = SOcPurpleDeep,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 4.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Join the orphicore world",
                    color = SOcTextSecondary,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Email
                SOcTextField(
                    value         = email,
                    onValueChange = { email = it },
                    label         = "Email",
                    leadingIcon   = Icons.Default.Email
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Password
                SOcTextField(
                    value            = password,
                    onValueChange    = { password = it },
                    label            = "Password",
                    leadingIcon      = Icons.Default.Lock,
                    isPassword       = true,
                    passwordVisible  = passwordVisible,
                    onTogglePassword = { passwordVisible = !passwordVisible }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Confirm password
                SOcTextField(
                    value            = confirmPassword,
                    onValueChange    = { confirmPassword = it },
                    label            = "Confirm Password",
                    leadingIcon      = Icons.Default.Lock,
                    isPassword       = true,
                    passwordVisible  = confirmVisible,
                    onTogglePassword = { confirmVisible = !confirmVisible },
                    isError          = confirmPassword.isNotEmpty() && confirmPassword != password
                )

                if (confirmPassword.isNotEmpty() && confirmPassword != password) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Passwords do not match",
                        color = Color(0xFFFF6B6B),
                        fontSize = 11.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Signup button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(SOcPurpleDeep, Color(0xFF6D28D9))
                            )
                        )
                        .border(1.dp, SOcAccentPink.copy(alpha = 0.35f), RoundedCornerShape(14.dp))
                        .clickable(enabled = !isLoading) {
                            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                                Toast.makeText(navController.context, "Fill all fields", Toast.LENGTH_SHORT).show()
                                return@clickable
                            }
                            if (password.length < 6) {
                                Toast.makeText(navController.context, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                                return@clickable
                            }
                            if (password != confirmPassword) {
                                Toast.makeText(navController.context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                                return@clickable
                            }
                            isLoading = true
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener {
                                    isLoading = false
                                    if (it.isSuccessful) {
                                        navController.navigate("home")
                                    } else {
                                        Toast.makeText(navController.context, it.exception?.message ?: "Signup failed", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                        .padding(vertical = 14.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = SOcPurpleLight,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text(
                            text = "Create Account",
                            color = SOcPurpleLight,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = SOcDivider, thickness = 0.5.dp)
                    Text("  or  ", color = SOcTextSecondary, fontSize = 11.sp)
                    HorizontalDivider(modifier = Modifier.weight(1f), color = SOcDivider, thickness = 0.5.dp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Already have an account? ", color = SOcTextSecondary, fontSize = 13.sp)
                    Text(
                        "Sign In",
                        color = SOcPurpleMid,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { navController.navigate("login") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "✦   orphicore   ✦",
                color = SOcTextSecondary.copy(alpha = 0.3f),
                fontSize = 9.sp,
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ── Signup-local text field ───────────────────────────────────────────────────
@Composable
private fun SOcTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onTogglePassword: (() -> Unit)? = null,
    isError: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SOcSurface2)
            .border(
                1.dp,
                if (isError) Color(0xFFFF6B6B).copy(alpha = 0.6f) else SOcDivider,
                RoundedCornerShape(12.dp)
            )
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(label, color = SOcTextSecondary.copy(alpha = 0.5f), fontSize = 13.sp) },
            leadingIcon = {
                Icon(
                    leadingIcon, contentDescription = null,
                    tint = if (isError) Color(0xFFFF6B6B) else SOcPurpleMid,
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = if (isPassword) ({
                IconButton(onClick = { onTogglePassword?.invoke() }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                        contentDescription = null,
                        tint = SOcTextSecondary,
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
                focusedTextColor        = SOcTextPrimary,
                unfocusedTextColor      = SOcTextPrimary,
                cursorColor             = SOcPurpleMid
            ),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}