package com.example.myapplication8

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.myapplication8.viewmodel.ProductDetailViewModel

@Composable
fun ProductDetailScreen(vm: ProductDetailViewModel = viewModel()) {
    val product by vm.product.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Product detail",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = Color.Blue
        )

        Spacer(modifier = Modifier.height(12.dp))

        Image(
            painter = rememberAsyncImagePainter(product.imageUrl),
            contentDescription = "Product Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = product.name,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Giá: ${product.price}",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = Color.Red
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF4F4F4), RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Text(
                text = product.description,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Start
            )
        }
    }
}
