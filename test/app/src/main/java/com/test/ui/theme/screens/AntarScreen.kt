package com.test.ui.theme.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.test.model.Table
import com.test.ui.theme.components.BatteryIndicator
import com.test.ui.theme.components.BottomBar
import com.test.ui.theme.viewmodels.BatteryViewModel
import com.test.ui.theme.viewmodels.AntarViewModel

val ForestGreen = Color(0xFF228B22)
@Composable
fun AntarScreen(navController: NavController) {
    val viewModel: AntarViewModel = viewModel()
    val tables by viewModel.tables.collectAsState()
    val batteryViewModel: BatteryViewModel = viewModel()
    val batteryLevel by batteryViewModel.batteryLevel.collectAsState()

    Scaffold(
        topBar = { BatteryIndicator(batteryLevel = batteryLevel) },
        bottomBar = { BottomBar(navController) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            MainContent(viewModel, tables)
            LoadingIndicator(viewModel.isLoading)
            ErrorDialog(viewModel.errorMessage) { viewModel.dismissError() }
            ConfirmationDialog(viewModel)
            AddTableDialog(viewModel)
        }
    }
}

@Composable
private fun MainContent(
    viewModel: AntarViewModel,
    tables: List<Table>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),

        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        TraySelectionSection(viewModel)
        DoorControlSection(viewModel)
        TableGridSection(viewModel, tables)
        ActionButtonsSection(viewModel)
    }
}

@Composable
private fun TraySelectionSection(viewModel: AntarViewModel) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Pilih Tray",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Atas", "Tengah", "Bawah").forEach { tray ->
                    TrayButton(
                        tray = tray,
                        isSelected = viewModel.selectedTray == tray,
                        onSelect = { viewModel.setSelectedTray(tray) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TrayButton(
    tray: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Button(
        onClick = onSelect,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) ForestGreen else Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.width(100.dp)
    ) {
        Text(tray, color = Color.Black) // Ubah font ke putih
    }
}

@Composable
private fun TableGridSection(
    viewModel: AntarViewModel,
    tables: List<Table>
) {
    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val screenWidth = maxWidth
        val columns = when {
            screenWidth < 600.dp -> 6
            screenWidth < 840.dp -> 8
            else -> 10
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Pilih Meja",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.heightIn(max = 500.dp)
                ) {
                    items(tables) { table ->
                        TableButton(
                            table = table,
                            isSelected = viewModel.selectedTable == table.name,
                            onSelect = { viewModel.setSelectedTable(table.name) },
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TableButton(
    table: Table,
    isSelected: Boolean,
    onSelect: () -> Unit,
    viewModel: AntarViewModel
) {
    Box(modifier = Modifier) {
        Button(
            onClick = onSelect,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSelected) ForestGreen else Color.White
            ),
            modifier = Modifier
                .aspectRatio(1f)
                .size(64.dp)
        ) {
            Text(
                color = Color.Black, // Ubah font ke putih
                text = table.name.filter { it.isDigit() },
                style = MaterialTheme.typography.titleMedium
            )
        }

        // Badge urutan
        if (isSelected) {
            val order = viewModel.getOrderForTable(table.name)
            if (order != null) {
                Text(
                    text = "$order",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .background(Color.Red, CircleShape)
                        .size(20.dp),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun DoorControlSection(viewModel: AntarViewModel) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Black)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Kontrol Pintu",
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Tombol Buka Pintu
                Button(
                    onClick = { viewModel.sendDoorCommand(1) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.width(120.dp)
                ) {
                    Text("Buka Pintu", color = Color.Black)
                }

                // Tombol Tutup Pintu
                Button(
                    onClick = { viewModel.sendDoorCommand(0) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.width(120.dp)
                ) {
                    Text("Tutup Pintu", color = Color.Black)
                }
            }
        }
    }
}

@Composable
private fun ActionButtonsSection(viewModel: AntarViewModel) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        val screenWidth = maxWidth
        val buttonWidth = if (screenWidth < 600.dp) 100.dp else 150.dp

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { viewModel.addSelection() },
                modifier = Modifier
                    .widthIn(min = buttonWidth)
                    .weight(1f)
                    .padding(end = 8.dp),
                enabled = viewModel.selectedTray != null && viewModel.selectedTable != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (viewModel.selectedTray != null && viewModel.selectedTable != null) ForestGreen else Color.Gray
                ),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                Text("Tambah Pesanan", style = MaterialTheme.typography.bodyLarge)
            }

            Button(
                onClick = { viewModel.showConfirmationDialog = true },
                modifier = Modifier
                    .widthIn(min = buttonWidth)
                    .weight(1f)
                    .padding(start = 8.dp),
                enabled = viewModel.selectedItems.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (viewModel.selectedTray != null && viewModel.selectedTable != null) ForestGreen else Color.Gray
                ),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                Text(
                    "Oke (${viewModel.selectedItems.size}/${AntarViewModel.MAX_ORDERS})",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
private fun ConfirmationDialog(viewModel: AntarViewModel) {
    if (viewModel.showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showConfirmationDialog = false },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .padding(24.dp),
            title = {
                Text(
                    "Konfirmasi ${viewModel.selectedItems.size} Pesanan",
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    viewModel.selectedItems.sortedBy { it.order }.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("${item.order}. ${item.trayPosition} → ${item.tableNumber}")
                                Text(
                                    "Koordinat: ${item.coordinates}",
                                    color = Color.Gray,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                            IconButton(
                                onClick = { viewModel.removeSelection(item.trayPosition) }
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Hapus",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmSelection()
                                //navController.navigate("loadingScreen")
                              },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ForestGreen
                    )
                ) {
                    Text("Konfirmasi Pesanan")
                }
            },
            dismissButton = {
                Button(
                    onClick = { viewModel.showConfirmationDialog = false },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    )
                ) {
                    Text("Kembali Edit")
                }
            }
        )
    }
}

@Composable
private fun LoadingIndicator(isLoading: Boolean) {
    if (isLoading) {
        Dialog(onDismissRequest = {}) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.White, shape = RoundedCornerShape(8.dp))
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun ErrorDialog(errorMessage: String?, onDismiss: () -> Unit) {
    errorMessage?.let {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Terjadi Kesalahan") },
            text = { Text(it) },
            confirmButton = {
                Button(onClick = onDismiss) {
                    Text("Mengerti")
                }

            }
        )
    }
}

@Composable
private fun AddTableDialog(viewModel: AntarViewModel) {
    if (viewModel.showAddTableDialog) {
        var tableName by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { viewModel.hideAddTableDialog() },
            title = { Text("Tambah Meja Baru") },
            text = {
                TextField(
                    value = tableName,
                    onValueChange = { tableName = it },
                    label = { Text("Nomor Meja") },
                    placeholder = { Text("Contoh: Meja 9") }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addTable(tableName)
                        tableName = ""
                    },
                    enabled = tableName.isNotBlank()
                ) {
                    Text("Tambah")
                }
            },
            dismissButton = {
                Button(onClick = { viewModel.hideAddTableDialog() }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun PreviewAntarScreen() {
    AntarScreen(navController = rememberNavController())
}