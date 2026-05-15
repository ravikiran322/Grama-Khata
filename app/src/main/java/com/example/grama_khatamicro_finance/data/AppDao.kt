package com.example.grama_khatamicro_finance.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: Customer)

    @Update
    suspend fun updateCustomer(customer: Customer)

    @Delete
    suspend fun deleteCustomer(customer: Customer)

    @Query("SELECT * FROM customers WHERE ownerId = :ownerId")
    fun getAllCustomers(ownerId: String): Flow<List<Customer>>

    @Query("SELECT * FROM customers WHERE id = :id LIMIT 1")
    suspend fun getCustomerById(id: Int): Customer?

    @Query("SELECT * FROM customers WHERE phone = :phone AND ownerId = :ownerId LIMIT 1")
    suspend fun getCustomerByPhone(phone: String, ownerId: String): Customer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("SELECT * FROM transactions WHERE customerPhone = :phone AND ownerId = :ownerId ORDER BY date DESC")
    fun getTransactionsByPhone(phone: String, ownerId: String): Flow<List<Transaction>>

    @Query("""
        SELECT customers.*, SUM(transactions.amount) as netBalance 
        FROM customers 
        LEFT JOIN transactions ON customers.phone = transactions.customerPhone AND customers.ownerId = transactions.ownerId
        WHERE customers.ownerId = :ownerId
        GROUP BY customers.id 
        ORDER BY netBalance DESC
    """)
    fun getCustomersWithBalances(ownerId: String): Flow<List<CustomerWithBalance>>
}

data class CustomerWithBalance(
    @Embedded val customer: Customer,
    val netBalance: Double?
)
