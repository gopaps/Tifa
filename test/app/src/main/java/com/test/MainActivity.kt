package com.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.test.ui.theme.TestTheme

import com.test.ui.theme.navigation.Navigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestTheme {
                Navigation()
            }
        }
    }
}


//@Composable
//fun ImageList(modifier: Modifier = Modifier) {
//    val images = remember { mutableStateOf<List<ImageModel>>(emptyList()) }
//
//    LaunchedEffect(Unit) {
//        RetrofitInstance.api.getImages().enqueue(object : Callback<List<ImageModel>> {
//            override fun onResponse(call: Call<List<ImageModel>>, response: Response<List<ImageModel>>) {
//                if (response.isSuccessful) {
//                    images.value = response.body() ?: emptyList()
//                }
//            }
//
//            override fun onFailure(call: Call<List<ImageModel>>, t: Throwable) {
//                t.printStackTrace()
//            }
//        })
//    }
//
//    LazyColumn(modifier = modifier) {
//        items(images.value) { image ->
//            val imageBytes = Base64.getDecoder().decode(image.image_data)
//            AsyncImage(
//                model = imageBytes,
//                contentDescription = image.image_name,
//                modifier = Modifier.fillMaxSize()
//            )
//        }
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun MainScreenPreview() {
//    TestTheme {
//        HomeScreen(rememberNavController())
//    }
//}