package com.example.rubberscan

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rubberscan.db.entity.Plantation
import com.example.rubberscan.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantationScreen(
    existing: Plantation?,
    userId: String,
    onSave: (Plantation) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    var name        by remember { mutableStateOf(existing?.name ?: "") }
    var regions     by remember { mutableStateOf<List<PsgcRegion>>(emptyList()) }
    var selRegion   by remember { mutableStateOf<PsgcRegion?>(null) }
    var selProvince by remember { mutableStateOf<PsgcProvince?>(null) }
    var selCity     by remember { mutableStateOf<PsgcCity?>(null) }
    var selBarangay by remember { mutableStateOf(existing?.barangay ?: "") }
    var address     by remember { mutableStateOf(existing?.address ?: "") }

    // Load PSGC data, then restore existing selections (edit mode)
    LaunchedEffect(Unit) {
        regions = PsgcData.load(context)
        if (existing != null) {
            selRegion   = regions.find { it.region == existing.region }
            selProvince = selRegion?.provinces?.find { it.province == existing.province }
            selCity     = selProvince?.cities?.find { it.city == existing.city }
        }
    }

    val isValid = name.isNotBlank() &&
            selRegion != null && selProvince != null &&
            selCity != null && selBarangay.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageBg)
    ) {
        // headre
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(GreenDark)
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f))
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ChevronLeft, contentDescription = "Back",
                    tint = Color.White, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Text(
                if (existing == null) "Register Plantation" else "Edit Plantation",
                color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold
            )
        }

        //form
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text("Plantation Details", fontWeight = FontWeight.Bold,
                        fontSize = 15.sp, color = TextPrimary)

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Plantation Name") },
                        placeholder = { Text("e.g. North Field") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (regions.isEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = GreenDark
                            )
                            Spacer(Modifier.width(10.dp))
                            Text("Loading location data...", fontSize = 13.sp, color = TextMuted)
                        }
                    }

                    //
                    DropdownField(
                        label = "Region",
                        selected = selRegion?.region ?: "",
                        options = regions.map { it.region },
                        enabled = regions.isNotEmpty()
                    ) { picked ->
                        selRegion   = regions.find { it.region == picked }
                        selProvince = null
                        selCity     = null
                        selBarangay = ""
                    }

                    DropdownField(
                        label = "Province",
                        selected = selProvince?.province ?: "",
                        options = selRegion?.provinces?.map { it.province } ?: emptyList(),
                        enabled = selRegion != null
                    ) { picked ->
                        selProvince = selRegion?.provinces?.find { it.province == picked }
                        selCity     = null
                        selBarangay = ""
                    }

                    DropdownField(
                        label = "City / Municipality",
                        selected = selCity?.city ?: "",
                        options = selProvince?.cities?.map { it.city } ?: emptyList(),
                        enabled = selProvince != null
                    ) { picked ->
                        selCity     = selProvince?.cities?.find { it.city == picked }
                        selBarangay = ""
                    }

                    DropdownField(
                        label = "Barangay",
                        selected = selBarangay,
                        options = selCity?.barangays ?: emptyList(),
                        enabled = selCity != null
                    ) { picked ->
                        selBarangay = picked
                    }

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Address Details (optional)") },
                        placeholder = { Text("e.g. Purok 3, Sitio Malipayon") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            Button(
                onClick = {
                    onSave(
                        Plantation(
                            userId    = userId,
                            name      = name.trim(),
                            region    = selRegion?.region ?: "",
                            province  = selProvince?.province ?: "",
                            city      = selCity?.city ?: "",
                            barangay  = selBarangay,
                            address   = address.trim()
                        )
                    )
                },
                enabled = isValid,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GreenDark,
                    disabledContainerColor = Color(0xFFBDBDBD)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
            ) {
                Text(
                    if (existing == null) "Save Plantation" else "Update Plantation",
                    color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold
                )
            }

            if (!isValid) {
                Text(
                    "Fill in the plantation name and complete location to continue.",
                    fontSize = 12.sp, color = TextMuted
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    label: String,
    selected: String,
    options: List<String>,
    enabled: Boolean,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded && enabled,
        onExpandedChange = { if (enabled) expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            label = { Text(label) },
            placeholder = { Text("Select $label") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded && enabled)
            },
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded && enabled,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
