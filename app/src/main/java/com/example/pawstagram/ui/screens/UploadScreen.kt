package com.example.pawstagram.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage

@Composable
fun UploadScreen(
    onSubmit: (imageUri: String, caption: String, hashtags: List<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedImage: MutableState<Uri?> = remember { mutableStateOf(null) }
    val caption = remember { mutableStateOf("") }
    val hashtags = remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImage.value = uri }
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
        Image(
            painter = painterResource(id = com.example.pawstagram.R.drawable.logo),
            contentDescription = null,
            modifier = Modifier
                .height(60.dp)
                .width(60.dp)
                .padding(bottom = 16.dp),
            contentScale = ContentScale.Fit
        )
        Button(
            onClick = {
                picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (selectedImage.value == null) "Pick Photo" else "Change Photo",
                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        selectedImage.value?.let { uri ->
            AsyncImage(
                model = uri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(200.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        OutlinedTextField(
            value = caption.value,
            onValueChange = { caption.value = it },
            label = { 
                Text(
                    "Caption",
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                ) 
            },
            modifier = Modifier.fillMaxWidth(),
            textStyle = androidx.compose.material3.MaterialTheme.typography.bodyLarge.copy(
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
            ),
            colors = TextFieldDefaults.colors(
                focusedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                focusedLabelColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = hashtags.value,
            onValueChange = { hashtags.value = it },
            label = { 
                Text(
                    "Hashtags (space/comma separated)",
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                ) 
            },
            modifier = Modifier.fillMaxWidth(),
            textStyle = androidx.compose.material3.MaterialTheme.typography.bodyLarge.copy(
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurface
            ),
            colors = TextFieldDefaults.colors(
                focusedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                focusedLabelColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            enabled = selectedImage.value != null,
            onClick = {
                val tags = hashtags.value.split(',', ' ').mapNotNull {
                    val t = it.trim()
                    if (t.isBlank()) null else t
                }
                selectedImage.value?.let { onSubmit(it.toString(), caption.value, tags) }
                // reset after submit
                caption.value = ""
                hashtags.value = ""
                selectedImage.value = null
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Upload",
                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        }
    }
}



