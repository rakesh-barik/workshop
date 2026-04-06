package com.purina.feedright.ui.visit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.purina.feedright.data.local.entity.FarmEntity
import com.purina.feedright.data.local.entity.ProductEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VisitRecordScreen(
    viewModel: VisitRecordViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.visitSaved) {
        if (uiState.visitSaved) onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Record Visit") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Farm dropdown
                FarmDropdown(
                    farms = uiState.farms,
                    selectedFarm = uiState.selectedFarm,
                    onFarmSelected = viewModel::onFarmSelected,
                    isError = uiState.errorMessage != null && uiState.selectedFarm == null
                )

                // Product dropdown
                ProductDropdown(
                    products = uiState.products,
                    selectedProduct = uiState.selectedProduct,
                    onProductSelected = viewModel::onProductSelected,
                    isError = uiState.errorMessage != null && uiState.selectedProduct == null
                )

                // Quantity field
                OutlinedTextField(
                    value = uiState.quantity,
                    onValueChange = viewModel::onQuantityChanged,
                    label = { Text("Quantity (kg / bags)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.errorMessage != null && uiState.quantity.isBlank()
                )

                // Notes field (optional)
                OutlinedTextField(
                    value = uiState.notes,
                    onValueChange = viewModel::onNotesChanged,
                    label = { Text("Notes (optional)") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth()
                )

                // Error message
                if (uiState.errorMessage != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = uiState.errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // Success message
                if (uiState.successMessage != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = uiState.successMessage!!,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Save button
                Button(
                    onClick = viewModel::onSaveClicked,
                    enabled = !uiState.isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Save Visit", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}

@Composable
private fun FarmDropdown(
    farms: List<FarmEntity>,
    selectedFarm: FarmEntity?,
    onFarmSelected: (FarmEntity?) -> Unit,
    isError: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = selectedFarm?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Farm") },
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select farm")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            isError = isError
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            farms.forEach { farm ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(farm.name, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                farm.location,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    onClick = {
                        onFarmSelected(farm)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ProductDropdown(
    products: List<ProductEntity>,
    selectedProduct: ProductEntity?,
    onProductSelected: (ProductEntity?) -> Unit,
    isError: Boolean
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = selectedProduct?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Product") },
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select product")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            isError = isError
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            products.forEach { product ->
                DropdownMenuItem(
                    text = {
                        Column {
                            Text(product.name, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                "${product.category} · SKU ${product.sku}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    onClick = {
                        onProductSelected(product)
                        expanded = false
                    }
                )
            }
        }
    }
}
