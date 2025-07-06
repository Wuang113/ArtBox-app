package com.example.appvetranh

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import java.io.File

@Composable
fun GalleryScreen(navController: NavController) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val userEmail = currentUser?.email ?: "Không rõ"

    var showDrawer by remember { mutableStateOf(false) }
    val imageList = remember { mutableStateListOf<File>() }

    val currentBackStackEntry = remember { navController.currentBackStackEntry }
    val imagePath = currentBackStackEntry?.arguments?.getString("path")

    LaunchedEffect(Unit) {
        val saved = context.filesDir.listFiles()?.filter {
            it.extension == "png"
        } ?: emptyList()
        imageList.addAll(saved)

        imagePath?.let {
            val file = File(it)
            if (file.exists() && !imageList.contains(file)) {
                imageList.add(file)
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_1),
                    contentDescription = "Logo",
                    modifier = Modifier.size(30.dp)
                )

                Text(
                    text = "GALLERY",
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp)
                )

                IconButton(
                    onClick = { showDrawer = !showDrawer },
                    modifier = Modifier.size(40.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_4),
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(30.dp)
                            .clip(RoundedCornerShape(50))
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Danh sách ảnh
                items(imageList) { file ->
                    val bitmap = remember(file.absolutePath) {
                        BitmapFactory.decodeFile(file.absolutePath)
                    }
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        bitmap?.let {
                            Image(bitmap = it.asImageBitmap(), contentDescription = null)
                        }
                    }
                }

                // Nút +
                item {
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                            .clickable {
                                navController.navigate("draw")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Thêm ảnh",
                            modifier = Modifier.size(40.dp),
                            tint = Color.Black
                        )
                    }
                }
            }
        }

        if (showDrawer) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable { showDrawer = false }
            )
        }

        AnimatedVisibility(
            visible = showDrawer,
            enter = slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(300)
            ),
            exit = slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(300)
            ),
            modifier = Modifier
                .fillMaxHeight()
                .width(240.dp)
                .align(Alignment.CenterEnd)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_1),
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(50))
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Thông tin người dùng", fontWeight = FontWeight.Bold)
                    Text("Email: $userEmail")
                    Text(
                        "Đăng xuất",
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .clickable {
                                auth.signOut()
                                navController.navigate("home") {
                                    popUpTo("gallery") { inclusive = true }
                                }
                            },
                        color = Color.Red
                    )
                }
            }
        }
    }
}
