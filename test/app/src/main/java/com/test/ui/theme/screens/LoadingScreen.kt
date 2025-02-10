//    package com.test.ui.theme.screens
//
//    @Composable
//    fun LoadingScreen(navController: NavController) {
//        val viewModel: LoadingViewModel = viewModel()
//        val orders by viewModel.orders.collectAsState()
//        val deliveredTable by viewModel.deliveredTable.collectAsState()
//
//        LaunchedEffect(Unit) {
//            viewModel.startPolling()
//        }
//
//        if (deliveredTable != null) {
//            AlertDialog(
//                onDismissRequest = { viewModel.clearDelivery() },
//                title = { Text("Pesanan Telah Sampai") },
//                text = { Text("Pesanan untuk meja $deliveredTable telah sampai") },
//                confirmButton = {
//                    Button(onClick = {
//                        viewModel.deleteOrder(deliveredTable!!)
//                        viewModel.clearDelivery()
//                    }) {
//                        Text("Oke")
//                    }
//                }
//            )
//        }
//
//        Scaffold(
//            topBar = { /* Tambahkan top bar jika perlu */ }
//        ) { padding ->
//            LazyVerticalGrid(
//                columns = GridCells.Fixed(3),
//                modifier = Modifier.padding(padding)
//            ) {
//                items(orders.chunked(3)) { row ->
//                    Row {
//                        row.forEach { order ->
//                            OrderItem(order = order)
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    @Composable
//    fun OrderItem(order: Order) {
//        Card(
//            modifier = Modifier
//                .padding(8.dp)
//                .fillMaxWidth(),
//            elevation = CardDefaults.cardElevation(4.dp)
//        ) {
//            Column(
//                modifier = Modifier.padding(16.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                Text("Meja: ${order.tableNumber}", style = MaterialTheme.typography.titleMedium)
//                Text("Tray: ${order.trayPosition}")
//                Text("Koordinat: ${order.coordinates}")
//            }
//        }
//    }