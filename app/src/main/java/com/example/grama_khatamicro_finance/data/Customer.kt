package com.example.grama_khatamicro_finance.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ownerId: String = "",
    val name: String = "",
    val phone: String = "",
    val photoUri: String? = null,
    val nextPaymentDate: Long? = null,
    val category: String = "Regular"
)
