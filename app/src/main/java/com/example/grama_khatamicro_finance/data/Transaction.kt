package com.example.grama_khatamicro_finance.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ownerId: String = "",       // Linked to the shop owner
    val customerPhone: String = "", // Linked to the specific customer
    val amount: Double = 0.0,
    val date: Long = System.currentTimeMillis(),
    val note: String = ""
)
