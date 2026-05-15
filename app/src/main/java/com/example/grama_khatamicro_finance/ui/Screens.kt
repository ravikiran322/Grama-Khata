package com.example.grama_khatamicro_finance.ui

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.grama_khatamicro_finance.R
import com.example.grama_khatamicro_finance.data.CustomerWithBalance
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isRegistering by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "App Logo",
            modifier = Modifier.size(120.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Grama-Khata",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = if (isRegistering) "Create your account" else "Secure Cloud Login",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    isLoading = true
                    if (isRegistering) {
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                isLoading = false
                                if (task.isSuccessful) onLoginSuccess()
                                else Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                            }
                    } else {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                isLoading = false
                                if (task.isSuccessful) onLoginSuccess()
                                else Toast.makeText(context, "Login Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                            }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(if (isRegistering) "Register" else "Login", fontSize = 18.sp)
            }
            
            TextButton(onClick = { isRegistering = !isRegistering }) {
                Text(if (isRegistering) "Already have an account? Login" else "New user? Create Account")
            }
        }
    }
}

@Composable
fun PinLockScreen(correctPin: String, onUnlock: () -> Unit) {
    var pinAttempt by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(16.dp))
        Text("Ledger Locked", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Enter PIN to continue", color = Color.Gray)
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(
            value = pinAttempt,
            onValueChange = { 
                if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                    pinAttempt = it
                    if (it == correctPin) onUnlock()
                    else if (it.length == 4) error = true
                    else error = false
                }
            },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.width(180.dp),
            isError = error,
            singleLine = true,
            label = { Text("4-Digit PIN") },
            textStyle = LocalTextStyle.current.copy(fontSize = 24.sp, fontWeight = FontWeight.Bold)
        )
        if (error) {
            Text("Incorrect PIN. Try again.", color = Color.Red, modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onCustomerClick: (Int) -> Unit,
    onAddCustomerClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val customers by viewModel.filteredCustomers.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val shopName by viewModel.shopName.collectAsState()
    val totalOutstanding by viewModel.totalOutstanding.collectAsState()
    val customersOwedCount by viewModel.customersOwedCount.collectAsState()
    var showSortMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp).padding(end = 8.dp)
                        )
                        Text(shopName, fontWeight = FontWeight.Bold) 
                    }
                },
                actions = {
                    IconButton(onClick = { showSortMenu = true }) {
                        Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Sort")
                    }
                    DropdownMenu(expanded = showSortMenu, onDismissRequest = { showSortMenu = false }) {
                        SortOrder.entries.forEach { order ->
                            DropdownMenuItem(
                                text = { Text(order.name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    viewModel.setSortOrder(order)
                                    showSortMenu = false
                                }
                            )
                        }
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddCustomerClick, containerColor = MaterialTheme.colorScheme.primary) {
                Icon(Icons.Default.PersonAdd, contentDescription = "Add Customer", tint = Color.White)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Stats Row
            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFBE9E7))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Due to collect", fontSize = 12.sp, color = Color.DarkGray)
                        Text("₹${String.format(Locale.getDefault(), "%.0f", totalOutstanding)}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F))
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Active Customers", fontSize = 12.sp, color = Color.DarkGray)
                        Text("$customersOwedCount", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF388E3C))
                    }
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                placeholder = { Text("Search by name or mobile") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(customers) { item ->
                    CustomerItem(item) { onCustomerClick(item.customer.id) }
                }
            }
        }
    }
}

@Composable
fun CustomerItem(item: CustomerWithBalance, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp).clickable { onClick() },
        elevation = CardDefaults.cardElevation(1.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (item.customer.photoUri != null) {
                AsyncImage(
                    model = item.customer.photoUri,
                    contentDescription = null,
                    modifier = Modifier.size(54.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Surface(
                    modifier = Modifier.size(54.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(item.customer.name.take(1).uppercase(), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(text = item.customer.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(text = item.customer.phone, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                if (item.customer.nextPaymentDate != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color(0xFFD32F2F))
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "Promise: ${SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(item.customer.nextPaymentDate))}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFD32F2F)
                        )
                    }
                }
            }
            val balance = item.netBalance ?: 0.0
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₹${String.format(Locale.getDefault(), "%.0f", Math.abs(balance))}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (balance > 0) Color(0xFFD32F2F) else if (balance < 0) Color(0xFF388E3C) else Color.Gray
                )
                Text(
                    text = if (balance > 0) "Owes You" else if (balance < 0) "You Owe" else "Settled",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (balance > 0) Color(0xFFD32F2F) else if (balance < 0) Color(0xFF388E3C) else Color.Gray
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailScreen(
    customerId: Int,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val customer by viewModel.getCustomer(customerId).collectAsState(initial = null)
    val transactions by viewModel.getTransactions(customerId).collectAsState(initial = emptyList())
    val context = LocalContext.current
    
    var showAddDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var isCredit by remember { mutableStateOf(true) }
    var amountText by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(customer?.name ?: "Customer Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Customer", tint = Color.Red)
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            val balance = transactions.sumOf { it.amount }
            
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = if (balance > 0) Color(0xFFFFEBEE) else Color(0xFFE8F5E9))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (customer?.photoUri != null) {
                            AsyncImage(
                                model = customer?.photoUri,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Surface(modifier = Modifier.size(80.dp), shape = CircleShape, color = Color.White) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(customer?.name?.take(1)?.uppercase() ?: "?", fontSize = 32.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(if (balance >= 0) "Current Debt" else "Advance Paid", style = MaterialTheme.typography.titleMedium)
                            Text(
                                "₹${String.format(Locale.getDefault(), "%.2f", Math.abs(balance))}",
                                style = MaterialTheme.typography.headlineLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (balance > 0) Color(0xFFD32F2F) else Color(0xFF388E3C)
                            )
                        }
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { 
                            customer?.let {
                                sendWhatsAppReminder(context, it.phone, it.name, balance)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Send WhatsApp Reminder")
                    }
                }
            }

            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = { isCredit = true; showAddDialog = true },
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Give (+)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { isCredit = false; showAddDialog = true },
                    modifier = Modifier.weight(1f).height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF388E3C)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Take (-)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Text("Transaction Log", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            
            LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
                items(transactions) { tx ->
                    var showTxDelete by remember { mutableStateOf(false) }
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(0.5.dp)
                    ) {
                        ListItem(
                            headlineContent = { Text(if (tx.amount > 0) "Item Given" else "Money Received", fontWeight = FontWeight.Medium) },
                            supportingContent = { 
                                Column {
                                    Text(SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(tx.date))
                                    if (tx.note.isNotBlank()) Text("Note: ${tx.note}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                }
                            },
                            trailingContent = { 
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        "₹${String.format(Locale.getDefault(), "%.2f", Math.abs(tx.amount))}", 
                                        color = if (tx.amount > 0) Color(0xFFD32F2F) else Color(0xFF388E3C),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    IconButton(onClick = { showTxDelete = true }) {
                                        Icon(Icons.Default.Close, contentDescription = "Delete", modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        )
                        if (showTxDelete) {
                            AlertDialog(
                                onDismissRequest = { showTxDelete = false },
                                title = { Text("Delete this entry?") },
                                confirmButton = {
                                    TextButton(onClick = { viewModel.deleteTransaction(tx); showTxDelete = false }) { Text("Delete", color = Color.Red) }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showTxDelete = false }) { Text("Cancel") }
                                }
                            )
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text(if (isCredit) "Give Item / Credit" else "Take Money / Payment") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = amountText,
                            onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) amountText = it },
                            label = { Text("Amount (₹)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = noteText,
                            onValueChange = { noteText = it },
                            label = { Text("Note (e.g. Sugar, 2kg Rice)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        val amount = amountText.toDoubleOrNull() ?: 0.0
                        if (amount > 0) {
                            viewModel.addTransaction(customerId, if (isCredit) amount else -amount, noteText)
                        }
                        showAddDialog = false
                        amountText = ""
                        noteText = ""
                    }) { Text("Confirm") }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
                }
            )
        }
        
        if (showDeleteConfirm) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = false },
                title = { Text("Delete this Customer?") },
                text = { Text("This will permanently remove the customer and all their records.") },
                confirmButton = {
                    TextButton(onClick = { 
                        customer?.let { viewModel.deleteCustomer(it) }
                        onBack()
                    }) { Text("Yes, Delete", color = Color.Red) }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirm = false }) { Text("No, Keep") }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCustomerScreen(viewModel: MainViewModel, onFinish: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var nextPaymentDate by remember { mutableStateOf<Long?>(null) }
    
    val context = LocalContext.current
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        photoUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Customer") },
                navigationIcon = {
                    IconButton(onClick = onFinish) { Icon(Icons.Default.Close, contentDescription = "Close") }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .clickable { photoPickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (photoUri != null) {
                    AsyncImage(
                        model = photoUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.AddAPhoto, contentDescription = null, modifier = Modifier.size(40.dp))
                }
            }
            Text("Add Profile Picture", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 8.dp))
            
            Spacer(modifier = Modifier.height(32.dp))
            
            OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Customer Name") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Mobile Number") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
            
            Spacer(modifier = Modifier.height(20.dp))
            
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(12.dp).fillMaxWidth()) {
                    Icon(Icons.Default.Event, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(12.dp))
                    Text("Promise to Pay Date:", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                    TextButton(onClick = {
                        val calendar = Calendar.getInstance()
                        DatePickerDialog(context, { _, year, month, day ->
                            calendar.set(year, month, day)
                            nextPaymentDate = calendar.timeInMillis
                        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
                    }) {
                        Text(if (nextPaymentDate == null) "Set Date" else SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(nextPaymentDate!!)))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank()) {
                        viewModel.addCustomer(name, phone, photoUri?.toString(), nextPaymentDate)
                        onFinish()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Create Customer Profile", fontSize = 18.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: MainViewModel, onBack: () -> Unit, onLogout: () -> Unit) {
    val shopName by viewModel.shopName.collectAsState()
    val securityPin by viewModel.securityPin.collectAsState()
    val totalCustomers by viewModel.totalCustomersCount.collectAsState()
    val filteredCustomers by viewModel.filteredCustomers.collectAsState()
    val context = LocalContext.current
    
    var newShopName by remember { mutableStateOf(shopName) }
    var newPin by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shop Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout", tint = Color.Red)
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Business Info", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = newShopName,
                onValueChange = { newShopName = it },
                label = { Text("Shop Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = { viewModel.saveShopName(newShopName) },
                modifier = Modifier.padding(top = 8.dp).fillMaxWidth()
            ) {
                Text("Save Changes")
            }
            
            Spacer(Modifier.height(24.dp))
            
            Text("Security PIN Lock", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            
            if (securityPin == null) {
                Text("Secure your data with a 4-digit PIN", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = newPin,
                    onValueChange = { if (it.length <= 4 && it.all { char -> char.isDigit() }) newPin = it },
                    label = { Text("New 4-Digit PIN") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation()
                )
                Button(
                    onClick = { if (newPin.length == 4) viewModel.savePin(newPin) },
                    modifier = Modifier.padding(top = 8.dp).fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Enable Security Lock")
                }
            } else {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)), border = null) {
                    Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFF2E7D32))
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Ledger Protection Active", modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
                        TextButton(onClick = { viewModel.clearPin() }) {
                            Text("Turn Off", color = Color.Red)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            
            Text("Export Records", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { 
                    val csvData = viewModel.generateCSVExport(filteredCustomers)
                    shareText(context, csvData)
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF455A64))
            ) {
                Icon(Icons.Default.FileDownload, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Export all to CSV (Excel)")
            }

            Spacer(modifier = Modifier.weight(1f))
            Text("Total Customers Managed: $totalCustomers", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            Text("Grama-Khata v1.5 | Cloud Edition", modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 8.dp), color = Color.Gray, fontSize = 12.sp)
        }
    }
}

private fun sendWhatsAppReminder(context: Context, phone: String, name: String, amount: Double) {
    val message = "Hello $name, your current balance at Shop is ₹${String.format(Locale.getDefault(), "%.2f", amount)}. Please clear it by your promised date. Thank you!"
    val intent = Intent(Intent.ACTION_VIEW)
    val url = "https://api.whatsapp.com/send?phone=$phone&text=${Uri.encode(message)}"
    intent.data = Uri.parse(url)
    context.startActivity(intent)
}

private fun shareText(context: Context, text: String) {
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}

private fun String.capitalize() = this.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
