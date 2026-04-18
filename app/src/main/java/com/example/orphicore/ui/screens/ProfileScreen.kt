package com.example.orphicore.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import java.nio.file.Files.size

// ── Orphicore color palette (mirrors SplashScreen) ───────────────────────────
private val OcBg           = Color(0xFF09090F)
private val OcSurface      = Color(0xFF110F1A)
private val OcSurface2     = Color(0xFF1A1628)
private val OcPurpleLight  = Color(0xFFE8D5FF)
private val OcPurpleMid    = Color(0xFFA78BFA)
private val OcPurpleDeep   = Color(0xFF7C3AED)
private val OcPurpleDark   = Color(0xFF4C1D95)
private val OcPurpleFaint  = Color(0xFF2D1F4E)
private val OcTextPrimary  = Color(0xFFE8D5FF)
private val OcTextSecondary= Color(0xFF9580C8)
private val OcDivider      = Color(0xFF2A1F45)
private val OcAccentPink   = Color(0xFFE879F9)

@Composable
fun ProfileScreen(navController: NavController) {

    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    // Pulsing glow animation for avatar ring
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            tween(1800, easing = FastOutSlowInEasing),
            RepeatMode.Reverse
        ), label = "glowAlpha"
    )

    Scaffold(
        containerColor = OcBg,
        bottomBar = {
            OcBottomNav(navController)
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(OcBg)
        ) {

            // ── Header banner ─────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(OcPurpleDark.copy(alpha = 0.6f), OcBg)
                        )
                    )
            ) {
                // Decorative rings in header
                OcDecorativeRings(modifier = Modifier.fillMaxSize())

                // Corner brackets
                OcCornerBrackets(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize()
                )

                // Eyebrow label
                Text(
                    text = "✦   P R O F I L E   ✦",
                    color = OcPurpleMid.copy(alpha = 0.6f),
                    fontSize = 9.sp,
                    letterSpacing = 5.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 20.dp)
                )

                // Avatar + name centered in header
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    // Avatar with pulsing ring
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(80.dp)
                    ) {
                        // Outer glow ring
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .border(
                                    width = 1.5.dp,
                                    brush = Brush.sweepGradient(
                                        listOf(
                                            OcPurpleMid.copy(alpha = glowAlpha),
                                            OcAccentPink.copy(alpha = glowAlpha * 0.6f),
                                            OcPurpleDeep.copy(alpha = glowAlpha)
                                        )
                                    ),
                                    shape = CircleShape
                                )
                        )
                        // Avatar circle
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .background(OcPurpleFaint),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (user?.email?.firstOrNull()?.uppercaseChar() ?: 'U').toString(),
                                color = OcPurpleMid,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = user?.email ?: "User",
                        color = OcTextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Orphicore Member",
                        color = OcTextSecondary,
                        fontSize = 11.sp,
                        letterSpacing = 3.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Stats row ─────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OcStatCard("Orders", "12", modifier = Modifier.weight(1f))
                OcStatCard("Saved", "5", modifier = Modifier.weight(1f))
                OcStatCard("Points", "840", modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Section label ─────────────────────────────────────────────────
            OcSectionLabel("ACCOUNT", modifier = Modifier.padding(horizontal = 20.dp))

            Spacer(modifier = Modifier.height(8.dp))

            // ── Menu card ─────────────────────────────────────────────────────
            OcMenuCard(
                modifier = Modifier.padding(horizontal = 16.dp),
                items = listOf(
                    Triple("My Orders",        Icons.Default.Face,           {}),
                    Triple("Saved Addresses",  Icons.Default.LocationOn,     {}),
                    Triple("Payment Methods",  Icons.Default.FavoriteBorder, {}),
                )
            )

            Spacer(modifier = Modifier.height(20.dp))
            OcSectionLabel("PREFERENCES", modifier = Modifier.padding(horizontal = 20.dp))
            Spacer(modifier = Modifier.height(8.dp))

            OcMenuCard(
                modifier = Modifier.padding(horizontal = 16.dp),
                items = listOf(
                    Triple("Settings",         Icons.Default.Settings, {}),
                    Triple("Help & Support",   Icons.Default.Info,     {}),
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Logout ────────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .border(
                        width = 1.dp,
                        color = Color(0xFF7C1D1D).copy(alpha = 0.6f),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .background(Color(0xFF1A0F0F))
                    .clickable {
                        auth.signOut()
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.ExitToApp,
                        contentDescription = null,
                        tint = Color(0xFFFF6B6B),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = "Logout",
                        color = Color(0xFFFF6B6B),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Footer label ──────────────────────────────────────────────────
            Text(
                text = "orphicore  ·  dream · shadow · depth",
                color = OcTextSecondary.copy(alpha = 0.35f),
                fontSize = 9.sp,
                letterSpacing = 3.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }
    }
}

// ── Stat card ─────────────────────────────────────────────────────────────────
@Composable
private fun OcStatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(OcSurface2)
            .border(1.dp, OcDivider, RoundedCornerShape(12.dp))
            .padding(vertical = 14.dp)
    ) {
        Text(value, color = OcPurpleMid, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(2.dp))
        Text(label, color = OcTextSecondary, fontSize = 10.sp, letterSpacing = 1.5.sp)
    }
}

// ── Section label ─────────────────────────────────────────────────────────────
@Composable
private fun OcSectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = OcPurpleDeep,
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 4.sp,
        modifier = modifier
    )
}

// ── Menu card (grouped items) ─────────────────────────────────────────────────
@Composable
private fun OcMenuCard(
    items: List<Triple<String, ImageVector, () -> Unit>>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(OcSurface)
            .border(1.dp, OcDivider, RoundedCornerShape(16.dp))
    ) {
        items.forEachIndexed { index, (title, icon, onClick) ->
            OcMenuItem(title = title, icon = icon, onClick = onClick)
            if (index < items.lastIndex) {
                HorizontalDivider(
                    modifier = Modifier.padding(start = 56.dp),
                    color = OcDivider,
                    thickness = 0.5.dp
                )
            }
        }
    }
}

// ── Single menu item ──────────────────────────────────────────────────────────
@Composable
private fun OcMenuItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(OcPurpleFaint)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = OcPurpleMid,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = title,
            color = OcTextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = 0.3.sp,
            modifier = Modifier.weight(1f)
        )
        Icon(
            Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = OcTextSecondary.copy(alpha = 0.5f),
            modifier = Modifier.size(18.dp)
        )
    }
}

// ── Bottom navigation ─────────────────────────────────────────────────────────
@Composable
private fun OcBottomNav(navController: NavController) {
    NavigationBar(
        containerColor = OcSurface,
        tonalElevation = 0.dp,
        modifier = Modifier.border(
            width = 0.5.dp,
            color = OcDivider,
            shape = RectangleShape
        )
    ) {
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("home") },
            icon = { Icon(Icons.Default.Home, null) },
            label = { Text("Home", fontSize = 10.sp, letterSpacing = 0.5.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor   = OcPurpleMid,
                unselectedIconColor = OcTextSecondary,
                selectedTextColor   = OcPurpleMid,
                unselectedTextColor = OcTextSecondary,
                indicatorColor      = OcPurpleFaint
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("cart") },
            icon = { Icon(Icons.Default.ShoppingCart, null) },
            label = { Text("Cart", fontSize = 10.sp, letterSpacing = 0.5.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor   = OcPurpleMid,
                unselectedIconColor = OcTextSecondary,
                selectedTextColor   = OcPurpleMid,
                unselectedTextColor = OcTextSecondary,
                indicatorColor      = OcPurpleFaint
            )
        )
        NavigationBarItem(
            selected = true,
            onClick = { },
            icon = { Icon(Icons.Default.Person, null) },
            label = { Text("Profile", fontSize = 10.sp, letterSpacing = 0.5.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor   = OcPurpleMid,
                unselectedIconColor = OcTextSecondary,
                selectedTextColor   = OcPurpleMid,
                unselectedTextColor = OcTextSecondary,
                indicatorColor      = OcPurpleFaint
            )
        )
    }
}

// ── Decorative concentric rings (header background) ───────────────────────────
@Composable
private fun OcDecorativeRings(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "rings")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(2400, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ), label = "pulse"
    )
    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        listOf(50f, 100f, 160f, 220f).forEachIndexed { i, r ->
            drawCircle(
                color  = OcPurpleDeep.copy(alpha = (0.18f - i * 0.03f) * pulse),
                radius = r.dp.toPx(),
                center = Offset(cx, cy),
                style  = Stroke(width = 0.8.dp.toPx())
            )
        }
    }
}

// ── Corner brackets ───────────────────────────────────────────────────────────
@Composable
private fun OcCornerBrackets(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val pad = 0f
        val len = 18.dp.toPx()
        val sw  = 1.dp.toPx()
        val col = OcPurpleMid.copy(alpha = 0.35f)
        drawLine(col, Offset(pad, pad), Offset(pad + len, pad), sw)
        drawLine(col, Offset(pad, pad), Offset(pad, pad + len), sw)
        drawLine(col, Offset(size.width - pad, pad), Offset(size.width - pad - len, pad), sw)
        drawLine(col, Offset(size.width - pad, pad), Offset(size.width - pad, pad + len), sw)
        drawLine(col, Offset(pad, size.height - pad), Offset(pad + len, size.height - pad), sw)
        drawLine(col, Offset(pad, size.height - pad), Offset(pad, size.height - pad - len), sw)
        drawLine(col, Offset(size.width - pad, size.height - pad), Offset(size.width - pad - len, size.height - pad), sw)
        drawLine(col, Offset(size.width - pad, size.height - pad), Offset(size.width - pad, size.height - pad - len), sw)
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen(navController = rememberNavController())
}