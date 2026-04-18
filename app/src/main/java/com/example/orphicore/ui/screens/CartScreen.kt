package com.example.orphicore.ui.screens

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.orphicore.viewmodel.CartViewModel

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
private val OcRed           = Color(0xFFFF6B6B)
private val OcRedSurface    = Color(0xFF1A0F0F)

@Composable
fun CartScreen(
    navController: NavController,
    cartViewModel: CartViewModel
) {
    val cartItems by cartViewModel.cartItems.collectAsState()
    val context = LocalContext.current

    Scaffold(
        containerColor = OcBg,
        bottomBar = { OcBottomNavCart(navController) }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(OcBg)
        ) {

            // ── Header ────────────────────────────────────────────────────────
            OcCartHeader(itemCount = cartItems.size)

            if (cartItems.isEmpty()) {

                // ── Empty state ───────────────────────────────────────────────
                OcEmptyCart(modifier = Modifier.weight(1f))

            } else {

                // ── Cart items list ───────────────────────────────────────────
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(
                        start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(cartItems) { item ->
                        OcCartItem(
                            name     = item.product.name,
                            price    = item.product.price,
                            image    = item.product.image,
                            quantity = item.quantity,
                            onAdd    = { cartViewModel.addToCart(item.product) },
                            onRemove = { cartViewModel.removeFromCart(item.product) }
                        )
                    }
                }

                // ── Order summary ─────────────────────────────────────────────
                OcOrderSummary(
                    total       = cartViewModel.getTotalPrice(),
                    itemCount   = cartItems.sumOf { it.quantity },
                    onPlaceOrder = {
                        cartViewModel.placeOrder(
                            onSuccess = {
                                Toast.makeText(context, "Order placed successfully", Toast.LENGTH_SHORT).show()
                            },
                            onFailure = {
                                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                )
            }
        }
    }
}

// ── Header ────────────────────────────────────────────────────────────────────
@Composable
private fun OcCartHeader(itemCount: Int) {

    val infiniteTransition = rememberInfiniteTransition(label = "cartGlow")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.35f, targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ), label = "pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(OcPurpleDark.copy(alpha = 0.45f), OcBg)
                )
            )
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        // Decorative ring top-right
        Canvas(modifier = Modifier.size(100.dp).align(Alignment.TopEnd)) {
            drawCircle(
                color  = OcPurpleDeep.copy(alpha = 0.13f * pulse),
                radius = 55.dp.toPx(),
                center = Offset(size.width, 0f),
                style  = Stroke(width = 1.dp.toPx())
            )
            drawCircle(
                color  = OcPurpleDeep.copy(alpha = 0.08f * pulse),
                radius = 80.dp.toPx(),
                center = Offset(size.width, 0f),
                style  = Stroke(width = 0.8.dp.toPx())
            )
        }

        Column {
            Text(
                text = "✦   O R P H I C O R E",
                color = OcPurpleMid.copy(alpha = 0.55f),
                fontSize = 9.sp,
                letterSpacing = 5.sp,
                fontWeight = FontWeight.Normal
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Your Cart",
                    color = OcTextPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                if (itemCount > 0) {
                    Spacer(modifier = Modifier.width(10.dp))
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(OcPurpleDeep)
                            .padding(horizontal = 9.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "$itemCount",
                            color = OcPurpleLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// ── Cart item row ─────────────────────────────────────────────────────────────
@Composable
private fun OcCartItem(
    name: String,
    price: Double,
    image: String?,
    quantity: Int,
    onAdd: () -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(OcSurface)
            .border(1.dp, OcDivider, RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Product image
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(OcSurface2)
        ) {
            if (!image.isNullOrEmpty()) {
                AsyncImage(
                    model = image,
                    contentDescription = name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = OcPurpleFaint,
                    modifier = Modifier
                        .size(28.dp)
                        .align(Alignment.Center)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Name + price
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                color = OcTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                letterSpacing = 0.2.sp
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "₹${price}",
                color = OcPurpleMid,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "subtotal  ₹${"%.0f".format(price * quantity)}",
                color = OcTextSecondary,
                fontSize = 10.sp,
                letterSpacing = 0.5.sp
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Qty stepper
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(OcSurface2)
                .border(1.dp, OcDivider, RoundedCornerShape(10.dp))
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(34.dp)
                    .clickable { onRemove() }
            ) {
                Icon(
                    Icons.Default.Clear,
                    contentDescription = "Remove",
                    tint = OcRed,
                    modifier = Modifier.size(14.dp)
                )
            }

            Text(
                text = "$quantity",
                color = OcTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp)
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(34.dp)
                    .clickable { onAdd() }
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add",
                    tint = OcPurpleMid,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

// ── Order summary + place order ───────────────────────────────────────────────
@Composable
private fun OcOrderSummary(
    total: Double,
    itemCount: Int,
    onPlaceOrder: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(OcSurface)
            .border(
                width = 1.dp,
                color = OcDivider,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            .padding(20.dp)
    ) {
        // Section label
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(14.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(OcPurpleDeep)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "ORDER SUMMARY",
                color = OcPurpleMid,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 4.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Line items
        OcSummaryRow("Items ($itemCount)", "₹${"%.2f".format(total)}")
        Spacer(modifier = Modifier.height(6.dp))
        OcSummaryRow("Delivery", "Free")
        Spacer(modifier = Modifier.height(10.dp))

        HorizontalDivider(color = OcDivider, thickness = 0.5.dp)

        Spacer(modifier = Modifier.height(10.dp))

        // Total
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Total",
                color = OcTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "₹${"%.2f".format(total)}",
                color = OcPurpleLight,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Place order button
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
                .border(
                    1.dp,
                    OcPurpleMid.copy(alpha = 0.4f),
                    RoundedCornerShape(14.dp)
                )
                .clickable { onPlaceOrder() }
                .padding(vertical = 14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = OcPurpleLight,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Place Order",
                    color = OcPurpleLight,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
            }
        }

        // Footer tagline
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "dream  ·  shadow  ·  depth",
            color = OcTextSecondary.copy(alpha = 0.3f),
            fontSize = 9.sp,
            letterSpacing = 3.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

// ── Summary row helper ────────────────────────────────────────────────────────
@Composable
private fun OcSummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = OcTextSecondary, fontSize = 13.sp)
        Text(
            value,
            color = if (value == "Free") Color(0xFF6EE7B7) else OcTextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// ── Empty cart state ──────────────────────────────────────────────────────────
@Composable
private fun OcEmptyCart(modifier: Modifier = Modifier) {

    val infiniteTransition = rememberInfiniteTransition(label = "emptyPulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ), label = "emptyAlpha"
    )

    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Pulsing cart icon with ring
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(100.dp)) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color  = OcPurpleDeep.copy(alpha = 0.18f * pulse),
                        radius = 48.dp.toPx(),
                        style  = Stroke(width = 1.dp.toPx())
                    )
                    drawCircle(
                        color  = OcPurpleDeep.copy(alpha = 0.10f * pulse),
                        radius = 38.dp.toPx(),
                        style  = Stroke(width = 0.8.dp.toPx())
                    )
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(OcPurpleFaint)
                ) {
                    Icon(
                        Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = OcPurpleMid,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Your cart is empty",
                color = OcTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Add products to get started",
                color = OcTextSecondary,
                fontSize = 12.sp,
                letterSpacing = 1.sp
            )
        }
    }
}

// ── Bottom navigation ─────────────────────────────────────────────────────────
@Composable
private fun OcBottomNavCart(navController: NavController) {
    NavigationBar(
        containerColor = OcSurface,
        tonalElevation = 0.dp,
        modifier = Modifier.border(
            width = 0.5.dp,
            color = OcDivider,
            shape = RectangleShape
        )
    ) {
        val itemColors = NavigationBarItemDefaults.colors(
            selectedIconColor   = OcPurpleMid,
            unselectedIconColor = OcTextSecondary,
            selectedTextColor   = OcPurpleMid,
            unselectedTextColor = OcTextSecondary,
            indicatorColor      = OcPurpleFaint
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("home") },
            icon = { Icon(Icons.Default.Home, null) },
            label = { Text("Home", fontSize = 10.sp, letterSpacing = 0.5.sp) },
            colors = itemColors
        )
        NavigationBarItem(
            selected = true,
            onClick = { },
            icon = { Icon(Icons.Default.ShoppingCart, null) },
            label = { Text("Cart", fontSize = 10.sp, letterSpacing = 0.5.sp) },
            colors = itemColors
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("profile") },
            icon = { Icon(Icons.Default.Person, null) },
            label = { Text("Profile", fontSize = 10.sp, letterSpacing = 0.5.sp) },
            colors = itemColors
        )
    }
}