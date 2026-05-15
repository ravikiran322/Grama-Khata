package com.example.grama_khatamicro_finance.data

import kotlinx.coroutines.flow.Flow

class AppRepository(private val appDao: AppDao) {
    
    fun getAllCustomers(ownerId: String): Flow<List<Customer>> = appDao.getAllCustomers(ownerId)
    
    fun getCustomersWithBalances(ownerId: String): Flow<List<CustomerWithBalance>> = 
        appDao.getCustomersWithBalances(ownerId)

    suspend fun insertCustomer(customer: Customer) {
        appDao.insertCustomer(customer)
    }

    suspend fun updateCustomer(customer: Customer) {
        appDao.updateCustomer(customer)
    }

    suspend fun deleteCustomer(customer: Customer) {
        appDao.deleteCustomer(customer)
    }

    suspend fun getCustomerById(id: Int): Customer? {
        return appDao.getCustomerById(id)
    }

    suspend fun getCustomerByPhone(phone: String, ownerId: String): Customer? {
        return appDao.getCustomerByPhone(phone, ownerId)
    }

    fun getTransactionsByPhone(phone: String, ownerId: String): Flow<List<Transaction>> {
        return appDao.getTransactionsByPhone(phone, ownerId)
    }

    suspend fun insertTransaction(transaction: Transaction) {
        appDao.insertTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        appDao.deleteTransaction(transaction)
    }
}
