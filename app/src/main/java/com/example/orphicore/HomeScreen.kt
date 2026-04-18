package com.example.orphicore

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.orphicore.data.model.Product
import com.example.orphicore.viewmodel.CartViewModel
import com.example.orphicore.viewmodel.ProductViewModel

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
fun HomeScreen(
    navController: NavController,
    productViewModel: ProductViewModel,
    cartViewModel: CartViewModel
) {
    val products by productViewModel.products.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val filteredProducts = products.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        containerColor = OcBg,
        bottomBar = { OcBottomNavHome(navController) }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(OcBg)
        ) {

            // ── Top header ────────────────────────────────────────────────────
            OcHomeHeader(searchQuery = searchQuery, onSearchChange = { searchQuery = it })

            // ── Category chips ────────────────────────────────────────────────
            OcCategoryRow()

            Spacer(modifier = Modifier.height(8.dp))

            // ── Section label ─────────────────────────────────────────────────
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(14.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(OcPurpleDeep)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (searchQuery.isEmpty()) "ALL PRODUCTS" else "RESULTS",
                    color = OcPurpleMid,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 4.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${filteredProducts.size} items",
                    color = OcTextSecondary,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Product grid ──────────────────────────────────────────────────
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(
                    start = 12.dp, end = 12.dp, bottom = 80.dp, top = 4.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredProducts) { product ->
                    OcProductCard(
                        product = product,
                        onAddClick = { cartViewModel.addToCart(product) },
                        onClick = { navController.navigate("productDetails/${product.id}") }
                    )
                }
            }
        }
    }
}

// ── Header with search ────────────────────────────────────────────────────────
@Composable
private fun OcHomeHeader(searchQuery: String, onSearchChange: (String) -> Unit) {

    val infiniteTransition = rememberInfiniteTransition(label = "headerGlow")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            tween(2200, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ), label = "headerPulse"
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
        // Subtle decorative ring top-right
        Canvas(
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.TopEnd)
        ) {
            drawCircle(
                color  = OcPurpleDeep.copy(alpha = 0.12f * pulse),
                radius = 60.dp.toPx(),
                center = Offset(size.width, 0f),
                style  = Stroke(width = 1.dp.toPx())
            )
            drawCircle(
                color  = OcPurpleDeep.copy(alpha = 0.08f * pulse),
                radius = 90.dp.toPx(),
                center = Offset(size.width, 0f),
                style  = Stroke(width = 0.8.dp.toPx())
            )
        }

        Column {
            // Eyebrow
            Text(
                text = "✦   O R P H I C O R E",
                color = OcPurpleMid.copy(alpha = 0.55f),
                fontSize = 9.sp,
                letterSpacing = 5.sp,
                fontWeight = FontWeight.Normal
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Discover",
                color = OcTextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(14.dp))

            // Search bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(OcSurface2)
                    .border(1.dp, OcDivider, RoundedCornerShape(14.dp))
            ) {
                TextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = {
                        Text(
                            "Search products…",
                            color = OcTextSecondary.copy(alpha = 0.5f),
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = OcPurpleMid,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = OcTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
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
    }
}

// ── Category chips row ────────────────────────────────────────────────────────
@Composable
private fun OcCategoryRow() {
    val categories = listOf("All", "Fresh", "Dairy", "Snacks", "Drinks", "Bakery")
    var selected by remember { mutableStateOf("All") }

    androidx.compose.foundation.lazy.LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 10.dp)
    ) {
        items(categories.size) { i ->
            val cat = categories[i]
            val isSelected = cat == selected
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) OcPurpleDeep else OcSurface2)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) OcPurpleMid.copy(alpha = 0.6f) else OcDivider,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { selected = cat }
                    .padding(horizontal = 16.dp, vertical = 7.dp)
            ) {
                Text(
                    text = cat,
                    color = if (isSelected) OcPurpleLight else OcTextSecondary,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

// ── Product card ──────────────────────────────────────────────────────────────
@Composable
fun OcProductCard(
    product: Product,
    onAddClick: () -> Unit,
    onClick: () -> Unit
) {
    var added by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(OcSurface)
            .border(1.dp, OcDivider, RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        // Image area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
                .background(OcSurface2)
        ) {
            AsyncImage(
                model = product.image,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Subtle gradient overlay at bottom of image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, OcSurface)
                        )
                    )
            )
        }

        // Details
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = product.name,
                color = OcTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                letterSpacing = 0.2.sp
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "₹${product.price}",
                color = OcPurpleMid,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Add button
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (added) OcPurpleFaint else OcPurpleDeep
                    )
                    .border(
                        1.dp,
                        if (added) OcPurpleMid.copy(alpha = 0.4f) else Color.Transparent,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable {
                        added = true
                        onAddClick()
                    }
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        if (added) Icons.Default.Check else Icons.Default.Add,
                        contentDescription = null,
                        tint = if (added) OcPurpleMid else OcPurpleLight,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (added) "Added" else "Add",
                        color = if (added) OcPurpleMid else OcPurpleLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

// ── Bottom navigation ─────────────────────────────────────────────────────────
@Composable
private fun OcBottomNavHome(navController: NavController) {
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
            selected = true,
            onClick = { },
            icon = { Icon(Icons.Default.Home, null) },
            label = { Text("Home", fontSize = 10.sp, letterSpacing = 0.5.sp) },
            colors = itemColors
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("cart") },
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