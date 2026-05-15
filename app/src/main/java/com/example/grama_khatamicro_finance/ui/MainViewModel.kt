package com.example.grama_khatamicro_finance.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.grama_khatamicro_finance.data.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class SortOrder {
    NAME, HIGHEST_DUE, RECENT
}

data class MonthlyInsight(
    val monthName: String,
    val totalCredit: Double,
    val totalPayment: Double,
    val netChange: Double
)

class MainViewModel(
    private val repository: AppRepository,
    private val preferenceManager: PreferenceManager
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    // Reactive flow of current user ID
    private val userId = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.uid ?: "anonymous")
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), auth.currentUser?.uid ?: "anonymous")

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _sortOrder = MutableStateFlow(SortOrder.HIGHEST_DUE)
    val sortOrder: StateFlow<SortOrder> = _sortOrder

    @OptIn(ExperimentalCoroutinesApi::class)
    val shopName: StateFlow<String> = userId.flatMapLatest { id ->
        preferenceManager.getShopName(id)
    }.map { it ?: "Grama-Khata" }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Grama-Khata")

    @OptIn(ExperimentalCoroutinesApi::class)
    val securityPin: StateFlow<String?> = userId.flatMapLatest { id ->
        preferenceManager.getSecurityPin(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val isDarkMode: StateFlow<Boolean> = preferenceManager.isDarkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val filteredCustomers: StateFlow<List<CustomerWithBalance>> = combine(
        userId.flatMapLatest { id -> repository.getCustomersWithBalances(id) },
        _searchQuery,
        _sortOrder
    ) { customers, query, order ->
        val filtered = if (query.isBlank()) customers 
        else customers.filter { it.customer.name.contains(query, ignoreCase = true) || it.customer.phone.contains(query) }

        when (order) {
            SortOrder.NAME -> filtered.sortedBy { it.customer.name }
            SortOrder.HIGHEST_DUE -> filtered.sortedByDescending { it.netBalance ?: 0.0 }
            SortOrder.RECENT -> filtered 
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalOutstanding: StateFlow<Double> = filteredCustomers
        .map { list -> list.sumOf { it.netBalance ?: 0.0 } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val customersOwedCount: StateFlow<Int> = filteredCustomers
        .map { list -> list.count { (it.netBalance ?: 0.0) > 0 } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    @OptIn(ExperimentalCoroutinesApi::class)
    val totalCustomersCount: StateFlow<Int> = userId.flatMapLatest { id -> 
        repository.getAllCustomers(id) 
    }.map { it.size }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Monthly Analytics restored and updated
    @OptIn(ExperimentalCoroutinesApi::class)
    val monthlyInsights: Flow<MonthlyInsight> = userId.flatMapLatest { uid ->
        repository.getAllCustomers(uid).flatMapLatest { customers ->
            if (customers.isEmpty()) {
                return@flatMapLatest flowOf(MonthlyInsight(SimpleDateFormat("MMMM", Locale.getDefault()).format(Date()), 0.0, 0.0, 0.0))
            }
            val currentMonth = Calendar.getInstance()
            currentMonth.set(Calendar.DAY_OF_MONTH, 1)
            currentMonth.set(Calendar.HOUR_OF_DAY, 0)
            
            val allTransactionsFlows = customers.map { repository.getTransactionsByPhone(it.phone, uid) }
            combine(allTransactionsFlows) { arrays ->
                val flatList = arrays.flatMap { it.toList() }
                val thisMonthTxs = flatList.filter { it.date >= currentMonth.timeInMillis }
                
                val credit = thisMonthTxs.filter { it.amount > 0 }.sumOf { it.amount }
                val payment = thisMonthTxs.filter { it.amount < 0 }.sumOf { it.amount }
                
                MonthlyInsight(
                    monthName = SimpleDateFormat("MMMM", Locale.getDefault()).format(Date()),
                    totalCredit = credit,
                    totalPayment = Math.abs(payment),
                    netChange = credit + payment
                )
            }
        }
    }

    init {
        viewModelScope.launch {
            userId.collect { id ->
                if (id != "anonymous") syncFromCloud()
            }
        }
    }

    fun syncFromCloud() {
        val uid = auth.currentUser?.uid ?: return
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                doc.getString("shopName")?.let { name -> 
                    viewModelScope.launch { preferenceManager.saveShopName(uid, name) }
                }
            }

        db.collection("users").document(uid).collection("customers")
            .get()
            .addOnSuccessListener { result ->
                viewModelScope.launch {
                    for (doc in result) {
                        val customer = doc.toObject(Customer::class.java).copy(ownerId = uid)
                        repository.insertCustomer(customer)
                    }
                }
            }
            
        db.collection("users").document(uid).collection("transactions")
            .get()
            .addOnSuccessListener { result ->
                viewModelScope.launch {
                    for (doc in result) {
                        val tx = doc.toObject(Transaction::class.java).copy(ownerId = uid)
                        repository.insertTransaction(tx)
                    }
                }
            }
    }

    fun saveShopName(name: String) {
        val uid = userId.value
        viewModelScope.launch {
            preferenceManager.saveShopName(uid, name)
            db.collection("users").document(uid).set(mapOf("shopName" to name), SetOptions.merge())
        }
    }

    fun addCustomer(name: String, phone: String, photoUri: String? = null, nextPaymentDate: Long? = null) {
        val uid = userId.value
        if (uid == "anonymous") return
        
        viewModelScope.launch {
            val customer = Customer(ownerId = uid, name = name, phone = phone, photoUri = photoUri, nextPaymentDate = nextPaymentDate)
            repository.insertCustomer(customer)
            db.collection("users").document(uid).collection("customers").document(phone).set(customer)
        }
    }

    fun addTransaction(customerId: Int, amount: Double, note: String = "") {
        val uid = userId.value
        if (uid == "anonymous") return
        
        viewModelScope.launch {
            val customer = repository.getCustomerById(customerId)
            if (customer != null) {
                val transaction = Transaction(ownerId = uid, customerPhone = customer.phone, amount = amount, note = note)
                repository.insertTransaction(transaction)
                db.collection("users").document(uid).collection("transactions").add(transaction)
            }
        }
    }

    fun savePin(pin: String) = viewModelScope.launch { preferenceManager.savePin(userId.value, pin) }
    fun clearPin() = viewModelScope.launch { preferenceManager.clearPin(userId.value) }
    fun toggleDarkMode() = viewModelScope.launch { preferenceManager.setDarkMode(!isDarkMode.value) }
    fun deleteCustomer(customer: Customer) = viewModelScope.launch { repository.deleteCustomer(customer) }
    fun deleteTransaction(transaction: Transaction) = viewModelScope.launch { repository.deleteTransaction(transaction) }
    
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getTransactions(customerId: Int): Flow<List<Transaction>> {
        return userId.flatMapLatest { uid ->
            flow {
                val customer = repository.getCustomerById(customerId)
                if (customer != null) {
                    emitAll(repository.getTransactionsByPhone(customer.phone, uid))
                } else {
                    emit(emptyList<Transaction>())
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getCustomer(id: Int) = userId.flatMapLatest { uid -> 
        flow {
            emit(repository.getCustomerById(id))
        }
    }

    fun setSearchQuery(query: String) { _searchQuery.value = query }
    fun setSortOrder(order: SortOrder) { _sortOrder.value = order }
    
    fun generateCSVExport(customers: List<CustomerWithBalance>): String {
        val sb = StringBuilder("Customer Name,Phone,Net Balance\n")
        customers.forEach { sb.append("${it.customer.name},${it.customer.phone},${it.netBalance ?: 0.0}\n") }
        return sb.toString()
    }
}

class MainViewModelFactory(
    private val repository: AppRepository,
    private val preferenceManager: PreferenceManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository, preferenceManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
