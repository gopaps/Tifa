package com.test.ui.theme.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.test.ui.theme.components.BottomBar
import com.test.ui.theme.components.BatteryIndicator
import com.test.ui.theme.viewmodels.BatteryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.*
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView // Pastikan hanya satu impor ini

// Model Data untuk Carousel
data class CarouselImage(
    val id: String = UUID.randomUUID().toString(),
    val fileName: String,
    val caption: String = "",
    val isVideo: Boolean = false // Tambahkan field untuk membedakan gambar dan video
)

class CarouselViewModel : ViewModel() {
    private val _carouselImages = mutableStateOf<List<CarouselImage>>(emptyList())
    val carouselImages: State<List<CarouselImage>> = _carouselImages
    private lateinit var storageDir: File

    fun initStorage(context: android.content.Context) {
        storageDir = File(context.filesDir, "carousel_images")
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        loadImages()
    }

    private fun loadImages() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val images = storageDir.listFiles()?.map { file ->
                    CarouselImage(
                        fileName = file.name,
                        caption = "Image ${file.nameWithoutExtension}",
                        isVideo = file.extension.lowercase() == "mp4"
                    )
                } ?: emptyList()
                _carouselImages.value = images.sortedBy { it.fileName }
            }
        }
    }

    fun saveImage(bitmap: Bitmap, caption: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val fileName = "img_${System.currentTimeMillis()}.png"
                val file = File(storageDir, fileName)
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }
                loadImages()
            }
        }
    }

    fun saveVideo(uri: Uri, context: android.content.Context, caption: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val fileName = "vid_${System.currentTimeMillis()}.mp4"
                val file = File(storageDir, fileName)
                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(file).use { output ->
                        input.copyTo(output)
                    }
                }
                _carouselImages.value = _carouselImages.value + CarouselImage(
                    fileName = fileName,
                    caption = caption,
                    isVideo = true
                )
            }
        }
    }

    fun deleteImage(fileName: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                File(storageDir, fileName).delete()
                loadImages()
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val batteryViewModel: BatteryViewModel = viewModel()
    val carouselViewModel: CarouselViewModel = viewModel()
    val batteryLevel by batteryViewModel.batteryLevel.collectAsState()
    val carouselImages = carouselViewModel.carouselImages.value

    LaunchedEffect(Unit) {
        carouselViewModel.initStorage(context)
    }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val bitmap = BitmapFactory.decodeStream(inputStream)
                carouselViewModel.saveImage(bitmap, "New Image")
            }
        }
    }

    val videoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            carouselViewModel.saveVideo(it, context, "New Video")
        }
    }

    Scaffold(
        topBar = { BatteryIndicator(batteryLevel = batteryLevel) },
        bottomBar = { BottomBar(navController) },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Carousel Section
            val totalPages = carouselImages.size + 1 // +1 untuk halaman CRUD
            val pagerState = rememberPagerState(pageCount = { totalPages })

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 4f)
                    .heightIn(max = 100.dp)
                    .padding(20.dp)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .shadow(4.dp)
                ) { page ->
                    if (page < carouselImages.size) {
                        CarouselItem(image = carouselImages[page], viewModel = carouselViewModel)
                    } else {
                        CRUDPanel(
                            onAddImage = { imagePicker.launch("image/*") },
                            onAddVideo = { videoPicker.launch("video/*") },
                            onDelete = {
                                if (carouselImages.isNotEmpty()) {
                                    carouselViewModel.deleteImage(carouselImages.last().fileName)
                                }
                            }
                        )
                    }
                }

                // Page Indicators
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(totalPages) { iteration ->
                        val color = if (pagerState.currentPage == iteration)
                            Color.White else Color.White.copy(alpha = 0.5f)
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(color)
                        )
                    }
                }
            }

            // Teks di bawah carousel
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 66.dp)
            ) {
                Text(
                    text = "Hai,\nAda yang\nBisa T.I.F.A bantu?",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 42.sp, // Ukuran font lebih kecil
                        lineHeight = 48.sp, // Tambahkan jarak antar baris
                        textAlign = TextAlign.Start // Rata kiri
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 110.dp), // Geser teks lebih ke kanan (ubah sesuai kebutuhan)
                    color = Color.Black
                )
            }
            Spacer(modifier = Modifier.weight(1f))

            // Buttons Row
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(46.dp)
            ) {
                NavigationButton(
                    icon = Icons.Default.DeliveryDining,
                    text = "Antar",
                    route = "antar",
                    navController = navController
                )
                NavigationButton(
                    icon = Icons.Default.Security,
                    text = "Jaga",
                    route = "jaga",
                    navController = navController
                )
                NavigationButton(
                    icon = Icons.AutoMirrored.Filled.DirectionsWalk,
                    text = "Keliling",
                    route = "keliling",
                    navController = navController
                )
                NavigationButton(
                    icon = Icons.Default.Mic,
                    text = "AI",
                    route = "ai",
                    navController = navController
                )
            }
        }
    }
}

@Composable
fun CarouselItem(image: CarouselImage, viewModel: CarouselViewModel) {
    val context = LocalContext.current

    if (image.isVideo) {
        val exoPlayer = remember {
            ExoPlayer.Builder(context).build().apply {
                repeatMode = ExoPlayer.REPEAT_MODE_ONE
            }
        }
        DisposableEffect(Unit) {
            onDispose {
                exoPlayer.release()
            }
        }

        val videoFile = File(context.filesDir, "carousel_images/${image.fileName}")
        LaunchedEffect(videoFile) {
            val mediaItem = MediaItem.fromUri(Uri.fromFile(videoFile))
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.play()
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                factory = { context ->
                    PlayerView(context).apply {
                        player = exoPlayer
                        useController = true
                        resizeMode = 3 // Nilai integer untuk RESIZE_MODE_ZOOM
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    } else {
        val bitmap = remember { mutableStateOf<Bitmap?>(null) }
        LaunchedEffect(image.fileName) {
            withContext(Dispatchers.IO) {
                val file = File(context.filesDir, "carousel_images/${image.fileName}")
                if (file.exists()) {
                    bitmap.value = BitmapFactory.decodeFile(file.absolutePath)
                }
            }
        }

        bitmap.value?.let { bmp ->
            Image(
                bitmap = bmp.asImageBitmap(),
                contentDescription = image.caption,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } ?: run {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.DarkGray),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}

@Composable
fun CRUDPanel(onAddImage: () -> Unit, onAddVideo: () -> Unit, onDelete: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF455A64))
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            IconButton(
                onClick = onAddImage,
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.White, CircleShape)
            ) {
                Icon(
                    Icons.Default.Image,
                    contentDescription = "Tambah Gambar",
                    tint = Color.Black
                )
            }
            IconButton(
                onClick = onAddVideo,
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.White, CircleShape)
            ) {
                Icon(
                    Icons.Default.VideoLibrary,
                    contentDescription = "Tambah Video",
                    tint = Color.Black
                )
            }
        }
        IconButton(
            onClick = onDelete,
            modifier = Modifier
                .size(56.dp)
                .background(Color.White, CircleShape)
        ) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Hapus Item",
                tint = Color.Red
            )
        }
    }
}

@Composable
fun NavigationButton(
    icon: ImageVector,
    text: String,
    route: String,
    navController: NavController
) {
    Button(
        onClick = { navController.navigate(route) },
        modifier = Modifier
            .width(220.dp)
            .height(80.dp)
            .padding(4.dp)
            .shadow(elevation = 10.dp, // Tinggi shadow
                shape = CircleShape, // Bentuk shadow (lingkaran)
                clip = true // Memastikan elemen tetap berbentuk lingkaran
            ),
        shape = RoundedCornerShape(40.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black,
            contentColor = Color.White
        )
    ) {
        Icon(icon, contentDescription = text, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun HomeScreenTabletPreview() {
    val navController = rememberNavController()
    HomeScreen(navController = navController)
}