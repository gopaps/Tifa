package com.test.ui.theme.screens

import android.Manifest
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.test.ui.theme.components.BatteryIndicator
import com.test.ui.theme.components.BottomBar
import com.test.ui.theme.viewmodels.BatteryViewModel
import java.util.*

class AIViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    var recognizedText by mutableStateOf("")
    var isListening by mutableStateOf(false)
    var isSpeaking by mutableStateOf(false)
    var hasRecordPermission by mutableStateOf(false)
    private var speechRecognizer: SpeechRecognizer? = null
    private var tts: TextToSpeech? = null

    init {
        initializeSpeechRecognizer()
        initializeTTS()
    }

    private fun initializeSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) {}
                override fun onBeginningOfSpeech() {}
                override fun onRmsChanged(rmsdB: Float) {}
                override fun onBufferReceived(buffer: ByteArray?) {}
                override fun onEndOfSpeech() {
                    isListening = false
                }
                override fun onError(error: Int) {
                    isListening = false
                }
                override fun onResults(results: Bundle?) {
                    results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.let {
                        recognizedText = it.getOrNull(0) ?: ""
                    }
                }
                override fun onPartialResults(partialResults: Bundle?) {}
                override fun onEvent(eventType: Int, params: Bundle?) {}
            })
        }
    }

    private fun initializeTTS() {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("id", "ID")
            }
        }
    }

    fun startListening() {
        if (!hasRecordPermission) return
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "id-ID")
        }
        speechRecognizer?.startListening(intent)
        isListening = true
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
        isListening = false
    }

    fun speakText() {
        recognizedText.takeIf { it.isNotEmpty() }?.let {
            tts?.speak(it, TextToSpeech.QUEUE_FLUSH, null, null)
            isSpeaking = true
        }
    }

    override fun onCleared() {
        super.onCleared()
        speechRecognizer?.destroy()
        tts?.stop()
        tts?.shutdown()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIScreen(navController: NavController) {
    val batteryViewModel: BatteryViewModel = viewModel()
    val batteryLevel by batteryViewModel.batteryLevel.collectAsState()
    val context = LocalContext.current
    val viewModel: AIViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return AIViewModel(context.applicationContext as Application) as T
        }
    })

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.hasRecordPermission = isGranted
    }

    LaunchedEffect(Unit) {
        val permissionCheck = android.content.pm.PackageManager.PERMISSION_GRANTED
        viewModel.hasRecordPermission = permissionCheck == android.content.pm.PackageManager.PERMISSION_GRANTED
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Asisten AI", color = Color.Black) },
                actions = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Kembali", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = { BottomBar(navController) },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
                .background(Color.White),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Display Recognized Text
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp)
                    .background(Color.LightGray)
                    .border(2.dp, Color.DarkGray, shape = MaterialTheme.shapes.medium)
                    .padding(16.dp)
            ) {
                Text(
                    text = viewModel.recognizedText.ifEmpty { "Tekan mikrofon untuk mulai berbicara..." },
                    textAlign = TextAlign.Start,
                    color = Color.Black
                )
            }

            // Control Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Voice Input Button
                Button(
                    onClick = {
                        if (viewModel.isListening) {
                            viewModel.stopListening()
                        } else {
                            if (viewModel.hasRecordPermission) {
                                viewModel.startListening()
                            } else {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = if (viewModel.isListening) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Mikrofon",
                        tint = Color.White
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(if (viewModel.isListening) "Berhenti" else "Bicara")
                }

                // Text-to-Speech Button
                Button(
                    onClick = { viewModel.speakText() },
                    enabled = viewModel.recognizedText.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )
                ) {
                    Icon(Icons.Default.VolumeUp, "Bersuara", tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Dengarkan")
                }
            }
        }
    }
}

// Preview for AIScreen
@Preview(showBackground = true)
@Composable
fun AIScreenPreview() {
    AIScreen(navController = NavController(LocalContext.current))
}