package com.example.orphicore.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlin.math.sin
import kotlin.random.Random

// ── Colors ───────────────────────────────────────────────────────────────────
private val BgColor       = Color(0xFF09090F)
private val PurpleLight   = Color(0xFFE8D5FF)
private val PurpleMid     = Color(0xFFA78BFA)
private val PurpleDeep    = Color(0xFF7C3AED)
private val PurpleDark    = Color(0xFF4C1D95)
private val PurpleFaint   = Color(0xFF6D28D9)

// ── Particle data ─────────────────────────────────────────────────────────────
private data class Particle(
    val x: Float,          // 0..1 fractional horizontal position
    val size: Float,
    val color: Color,
    val durationMs: Int,
    val delayMs: Int
)

private val particleColors = listOf(
    PurpleMid, Color(0xFFC4B5FD), PurpleDeep, Color(0xFFE879F9), Color(0xFF818CF8)
)

private val particles = List(32) {
    Particle(
        x         = Random.nextFloat(),
        size      = Random.nextFloat() * 4f + 1.5f,
        color     = particleColors.random(),
        durationMs = (3000 + Random.nextInt(4000)),
        delayMs   = Random.nextInt(5000)
    )
}

// ── Ring specs ────────────────────────────────────────────────────────────────
private data class RingSpec(val radiusDp: Float, val color: Color, val delayMs: Int)

private val rings = listOf(
    RingSpec(40f,  PurpleMid.copy(alpha = 0.33f),  400),
    RingSpec(90f,  PurpleDeep.copy(alpha = 0.27f), 800),
    RingSpec(160f, PurpleFaint.copy(alpha = 0.20f),1200),
    RingSpec(235f, PurpleDark.copy(alpha = 0.13f), 1600),
)

// ── SplashScreen ──────────────────────────────────────────────────────────────
@Composable
fun SplashScreen(navController: NavController) {

    // Navigate after 5 s (matches animation length)
    LaunchedEffect(Unit) {
        delay(5500)
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            navController.navigate("home")  { popUpTo("splash") { inclusive = true } }
        } else {
            navController.navigate("login") { popUpTo("splash") { inclusive = true } }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor),
        contentAlignment = Alignment.Center
    ) {

        // ── Glow orb ──────────────────────────────────────────────────────────
        GlowOrb()

        // ── Rings ─────────────────────────────────────────────────────────────
        rings.forEach { RingAnimation(it) }

        // ── Particles ─────────────────────────────────────────────────────────
        particles.forEach { ParticleAnimation(it) }

        // ── Corner brackets ───────────────────────────────────────────────────
        CornerBrackets()

        // ── Text block ────────────────────────────────────────────────────────
        TextBlock()
    }
}

// ── Glow orb ──────────────────────────────────────────────────────────────────
@Composable
private fun GlowOrb() {
    val infiniteTransition = rememberInfiniteTransition(label = "orb")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "orbScale"
    )
    val orbAnim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        orbAnim.animateTo(1f, animationSpec = tween(1000, delayMillis = 200, easing = FastOutSlowInEasing))
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val radius = 130.dp.toPx() * scale * orbAnim.value
        for (i in 5 downTo 1) {
            drawCircle(
                color = PurpleDeep.copy(alpha = 0.07f * i * orbAnim.value),
                radius = radius * (1f + i * 0.18f),
                center = Offset(cx, cy)
            )
        }
        drawCircle(
            color = PurpleDeep.copy(alpha = 0.25f * orbAnim.value),
            radius = radius,
            center = Offset(cx, cy)
        )
    }
}

// ── Single ring ───────────────────────────────────────────────────────────────
@Composable
private fun RingAnimation(spec: RingSpec) {
    val anim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(spec.delayMs.toLong())
        anim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 3000, easing = FastOutSlowInEasing)
        )
    }
    // scale 0→1 and alpha 0→0.7→0
    val scale = anim.value
    val alpha = if (scale < 0.6f) scale / 0.6f * 0.7f else (1f - scale) / 0.4f * 0.7f

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            color  = spec.color.copy(alpha = alpha.coerceIn(0f, 1f)),
            radius = spec.radiusDp.dp.toPx() * scale,
            center = Offset(size.width / 2f, size.height / 2f),
            style  = Stroke(width = 1.dp.toPx())
        )
    }
}

// ── Single particle ───────────────────────────────────────────────────────────
@Composable
private fun ParticleAnimation(p: Particle) {
    val anim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(p.delayMs.toLong())
        while (true) {
            anim.snapTo(0f)
            anim.animateTo(1f, animationSpec = tween(p.durationMs, easing = LinearEasing))
        }
    }
    Canvas(modifier = Modifier.fillMaxSize()) {
        val progress = anim.value
        val x = p.x * size.width
        val y = size.height - progress * (size.height + 40.dp.toPx())
        val alpha = when {
            progress < 0.1f -> progress / 0.1f
            progress > 0.85f -> (1f - progress) / 0.15f
            else -> 1f
        }
        drawCircle(
            color  = p.color.copy(alpha = alpha.coerceIn(0f, 1f)),
            radius = p.size.dp.toPx(),
            center = Offset(x, y)
        )
    }
}

// ── Corner brackets ───────────────────────────────────────────────────────────
@Composable
private fun CornerBrackets() {
    val alpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(2200)
        alpha.animateTo(1f, tween(600))
    }
    Canvas(modifier = Modifier.fillMaxSize().alpha(alpha.value)) {
        val pad  = 28.dp.toPx()
        val len  = 26.dp.toPx()
        val sw   = 1.dp.toPx()
        val col  = PurpleMid.copy(alpha = 0.4f)
        // top-left
        drawLine(col, Offset(pad, pad), Offset(pad + len, pad), sw)
        drawLine(col, Offset(pad, pad), Offset(pad, pad + len), sw)
        // top-right
        drawLine(col, Offset(size.width - pad, pad), Offset(size.width - pad - len, pad), sw)
        drawLine(col, Offset(size.width - pad, pad), Offset(size.width - pad, pad + len), sw)
        // bottom-left
        drawLine(col, Offset(pad, size.height - pad), Offset(pad + len, size.height - pad), sw)
        drawLine(col, Offset(pad, size.height - pad), Offset(pad, size.height - pad - len), sw)
        // bottom-right
        drawLine(col, Offset(size.width - pad, size.height - pad), Offset(size.width - pad - len, size.height - pad), sw)
        drawLine(col, Offset(size.width - pad, size.height - pad), Offset(size.width - pad, size.height - pad - len), sw)
    }
}

// ── Text block ────────────────────────────────────────────────────────────────
@Composable
private fun TextBlock() {
    // Eyebrow fade
    val eyebrowAlpha = remember { Animatable(0f) }
    // Main word: alpha + letter spacing simulated via scale
    val mainAlpha = remember { Animatable(0f) }
    val mainBlur  = remember { Animatable(14f) }
    // Divider line width
    val lineWidth = remember { Animatable(0f) }
    // Subtitle
    val subAlpha  = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Eyebrow
        delay(800)
        eyebrowAlpha.animateTo(0.6f, tween(600))

        // Main word
        delay(0)
        mainAlpha.animateTo(1f, tween(800, easing = FastOutSlowInEasing))
        mainBlur.animateTo(0f, tween(800, easing = FastOutSlowInEasing))

        // Glitch flash at ~4.4s
        delay(2800)
        mainAlpha.animateTo(0.25f, tween(80))
        mainAlpha.animateTo(1f,    tween(80))

        // Line
        lineWidth.animateTo(1f, tween(700, easing = FastOutSlowInEasing))

        // Subtitle
        subAlpha.animateTo(0.6f, tween(500))
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 32.dp)
    ) {
        // Eyebrow
        Text(
            text      = "✦   A E S T H E T I C   ✦",
            color     = PurpleDeep.copy(alpha = eyebrowAlpha.value),
            fontSize  = 9.sp,
            letterSpacing = 5.sp,
            fontWeight    = FontWeight.Normal,
            textAlign     = TextAlign.Center,
            modifier      = Modifier.padding(bottom = 20.dp)
        )

        // Main title
        Text(
            text      = "orphicore",
            color     = PurpleLight.copy(alpha = mainAlpha.value),
            fontSize  = 36.sp,
            fontWeight    = FontWeight.Bold,
            letterSpacing = 6.sp,
            textAlign     = TextAlign.Center,
            modifier      = Modifier
                .blur(mainBlur.value.coerceAtLeast(0f).dp)
        )

        // Divider line
        Spacer(modifier = Modifier.height(18.dp))
        Box(
            modifier = Modifier
                .width((200 * lineWidth.value).dp)
                .height(1.dp)
                .background(PurpleMid.copy(alpha = 0.6f))
        )

        // Subtitle
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text      = "dream  ·  shadow  ·  depth",
            color     = PurpleMid.copy(alpha = subAlpha.value),
            fontSize  = 9.sp,
            letterSpacing = 4.sp,
            fontWeight    = FontWeight.Normal,
            textAlign     = TextAlign.Center
        )
    }
}