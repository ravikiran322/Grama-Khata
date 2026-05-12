package com.example.grama_khatamicro_finance.`data`

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Double
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppDao_Impl(
  __db: RoomDatabase,
) : AppDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfCustomer: EntityInsertAdapter<Customer>

  private val __insertAdapterOfTransaction: EntityInsertAdapter<Transaction>

  private val __deleteAdapterOfCustomer: EntityDeleteOrUpdateAdapter<Customer>

  private val __deleteAdapterOfTransaction: EntityDeleteOrUpdateAdapter<Transaction>

  private val __updateAdapterOfCustomer: EntityDeleteOrUpdateAdapter<Customer>
  init {
    this.__db = __db
    this.__insertAdapterOfCustomer = object : EntityInsertAdapter<Customer>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `customers` (`id`,`ownerId`,`name`,`phone`,`photoUri`,`nextPaymentDate`,`category`) VALUES (nullif(?, 0),?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Customer) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindText(2, entity.ownerId)
        statement.bindText(3, entity.name)
        statement.bindText(4, entity.phone)
        val _tmpPhotoUri: String? = entity.photoUri
        if (_tmpPhotoUri == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpPhotoUri)
        }
        val _tmpNextPaymentDate: Long? = entity.nextPaymentDate
        if (_tmpNextPaymentDate == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpNextPaymentDate)
        }
        statement.bindText(7, entity.category)
      }
    }
    this.__insertAdapterOfTransaction = object : EntityInsertAdapter<Transaction>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `transactions` (`id`,`ownerId`,`customerPhone`,`amount`,`date`,`note`) VALUES (nullif(?, 0),?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: Transaction) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindText(2, entity.ownerId)
        statement.bindText(3, entity.customerPhone)
        statement.bindDouble(4, entity.amount)
        statement.bindLong(5, entity.date)
        statement.bindText(6, entity.note)
      }
    }
    this.__deleteAdapterOfCustomer = object : EntityDeleteOrUpdateAdapter<Customer>() {
      protected override fun createQuery(): String = "DELETE FROM `customers` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: Customer) {
        statement.bindLong(1, entity.id.toLong())
      }
    }
    this.__deleteAdapterOfTransaction = object : EntityDeleteOrUpdateAdapter<Transaction>() {
      protected override fun createQuery(): String = "DELETE FROM `transactions` WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: Transaction) {
        statement.bindLong(1, entity.id.toLong())
      }
    }
    this.__updateAdapterOfCustomer = object : EntityDeleteOrUpdateAdapter<Customer>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `customers` SET `id` = ?,`ownerId` = ?,`name` = ?,`phone` = ?,`photoUri` = ?,`nextPaymentDate` = ?,`category` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: Customer) {
        statement.bindLong(1, entity.id.toLong())
        statement.bindText(2, entity.ownerId)
        statement.bindText(3, entity.name)
        statement.bindText(4, entity.phone)
        val _tmpPhotoUri: String? = entity.photoUri
        if (_tmpPhotoUri == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpPhotoUri)
        }
        val _tmpNextPaymentDate: Long? = entity.nextPaymentDate
        if (_tmpNextPaymentDate == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpNextPaymentDate)
        }
        statement.bindText(7, entity.category)
        statement.bindLong(8, entity.id.toLong())
      }
    }
  }

  public override suspend fun insertCustomer(customer: Customer): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfCustomer.insert(_connection, customer)
  }

  public override suspend fun insertTransaction(transaction: Transaction): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfTransaction.insert(_connection, transaction)
  }

  public override suspend fun deleteCustomer(customer: Customer): Unit = performSuspending(__db,
      false, true) { _connection ->
    __deleteAdapterOfCustomer.handle(_connection, customer)
  }

  public override suspend fun deleteTransaction(transaction: Transaction): Unit =
      performSuspending(__db, false, true) { _connection ->
    __deleteAdapterOfTransaction.handle(_connection, transaction)
  }

  public override suspend fun updateCustomer(customer: Customer): Unit = performSuspending(__db,
      false, true) { _connection ->
    __updateAdapterOfCustomer.handle(_connection, customer)
  }

  public override fun getAllCustomers(ownerId: String): Flow<List<Customer>> {
    val _sql: String = "SELECT * FROM customers WHERE ownerId = ?"
    return createFlow(__db, false, arrayOf("customers")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, ownerId)
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _cursorIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _cursorIndexOfPhone: Int = getColumnIndexOrThrow(_stmt, "phone")
        val _cursorIndexOfPhotoUri: Int = getColumnIndexOrThrow(_stmt, "photoUri")
        val _cursorIndexOfNextPaymentDate: Int = getColumnIndexOrThrow(_stmt, "nextPaymentDate")
        val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _result: MutableList<Customer> = mutableListOf()
        while (_stmt.step()) {
          val _item: Customer
          val _tmpId: Int
          _tmpId = _stmt.getLong(_cursorIndexOfId).toInt()
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_cursorIndexOfOwnerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_cursorIndexOfName)
          val _tmpPhone: String
          _tmpPhone = _stmt.getText(_cursorIndexOfPhone)
          val _tmpPhotoUri: String?
          if (_stmt.isNull(_cursorIndexOfPhotoUri)) {
            _tmpPhotoUri = null
          } else {
            _tmpPhotoUri = _stmt.getText(_cursorIndexOfPhotoUri)
          }
          val _tmpNextPaymentDate: Long?
          if (_stmt.isNull(_cursorIndexOfNextPaymentDate)) {
            _tmpNextPaymentDate = null
          } else {
            _tmpNextPaymentDate = _stmt.getLong(_cursorIndexOfNextPaymentDate)
          }
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_cursorIndexOfCategory)
          _item =
              Customer(_tmpId,_tmpOwnerId,_tmpName,_tmpPhone,_tmpPhotoUri,_tmpNextPaymentDate,_tmpCategory)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getCustomerById(id: Int): Customer? {
    val _sql: String = "SELECT * FROM customers WHERE id = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id.toLong())
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _cursorIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _cursorIndexOfPhone: Int = getColumnIndexOrThrow(_stmt, "phone")
        val _cursorIndexOfPhotoUri: Int = getColumnIndexOrThrow(_stmt, "photoUri")
        val _cursorIndexOfNextPaymentDate: Int = getColumnIndexOrThrow(_stmt, "nextPaymentDate")
        val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _result: Customer?
        if (_stmt.step()) {
          val _tmpId: Int
          _tmpId = _stmt.getLong(_cursorIndexOfId).toInt()
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_cursorIndexOfOwnerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_cursorIndexOfName)
          val _tmpPhone: String
          _tmpPhone = _stmt.getText(_cursorIndexOfPhone)
          val _tmpPhotoUri: String?
          if (_stmt.isNull(_cursorIndexOfPhotoUri)) {
            _tmpPhotoUri = null
          } else {
            _tmpPhotoUri = _stmt.getText(_cursorIndexOfPhotoUri)
          }
          val _tmpNextPaymentDate: Long?
          if (_stmt.isNull(_cursorIndexOfNextPaymentDate)) {
            _tmpNextPaymentDate = null
          } else {
            _tmpNextPaymentDate = _stmt.getLong(_cursorIndexOfNextPaymentDate)
          }
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_cursorIndexOfCategory)
          _result =
              Customer(_tmpId,_tmpOwnerId,_tmpName,_tmpPhone,_tmpPhotoUri,_tmpNextPaymentDate,_tmpCategory)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getCustomerByPhone(phone: String, ownerId: String): Customer? {
    val _sql: String = "SELECT * FROM customers WHERE phone = ? AND ownerId = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, phone)
        _argIndex = 2
        _stmt.bindText(_argIndex, ownerId)
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _cursorIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _cursorIndexOfPhone: Int = getColumnIndexOrThrow(_stmt, "phone")
        val _cursorIndexOfPhotoUri: Int = getColumnIndexOrThrow(_stmt, "photoUri")
        val _cursorIndexOfNextPaymentDate: Int = getColumnIndexOrThrow(_stmt, "nextPaymentDate")
        val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _result: Customer?
        if (_stmt.step()) {
          val _tmpId: Int
          _tmpId = _stmt.getLong(_cursorIndexOfId).toInt()
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_cursorIndexOfOwnerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_cursorIndexOfName)
          val _tmpPhone: String
          _tmpPhone = _stmt.getText(_cursorIndexOfPhone)
          val _tmpPhotoUri: String?
          if (_stmt.isNull(_cursorIndexOfPhotoUri)) {
            _tmpPhotoUri = null
          } else {
            _tmpPhotoUri = _stmt.getText(_cursorIndexOfPhotoUri)
          }
          val _tmpNextPaymentDate: Long?
          if (_stmt.isNull(_cursorIndexOfNextPaymentDate)) {
            _tmpNextPaymentDate = null
          } else {
            _tmpNextPaymentDate = _stmt.getLong(_cursorIndexOfNextPaymentDate)
          }
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_cursorIndexOfCategory)
          _result =
              Customer(_tmpId,_tmpOwnerId,_tmpName,_tmpPhone,_tmpPhotoUri,_tmpNextPaymentDate,_tmpCategory)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getTransactionsByPhone(phone: String, ownerId: String):
      Flow<List<Transaction>> {
    val _sql: String =
        "SELECT * FROM transactions WHERE customerPhone = ? AND ownerId = ? ORDER BY date DESC"
    return createFlow(__db, false, arrayOf("transactions")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, phone)
        _argIndex = 2
        _stmt.bindText(_argIndex, ownerId)
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _cursorIndexOfCustomerPhone: Int = getColumnIndexOrThrow(_stmt, "customerPhone")
        val _cursorIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _cursorIndexOfDate: Int = getColumnIndexOrThrow(_stmt, "date")
        val _cursorIndexOfNote: Int = getColumnIndexOrThrow(_stmt, "note")
        val _result: MutableList<Transaction> = mutableListOf()
        while (_stmt.step()) {
          val _item: Transaction
          val _tmpId: Int
          _tmpId = _stmt.getLong(_cursorIndexOfId).toInt()
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_cursorIndexOfOwnerId)
          val _tmpCustomerPhone: String
          _tmpCustomerPhone = _stmt.getText(_cursorIndexOfCustomerPhone)
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_cursorIndexOfAmount)
          val _tmpDate: Long
          _tmpDate = _stmt.getLong(_cursorIndexOfDate)
          val _tmpNote: String
          _tmpNote = _stmt.getText(_cursorIndexOfNote)
          _item = Transaction(_tmpId,_tmpOwnerId,_tmpCustomerPhone,_tmpAmount,_tmpDate,_tmpNote)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getCustomersWithBalances(ownerId: String): Flow<List<CustomerWithBalance>> {
    val _sql: String = """
        |
        |        SELECT customers.*, SUM(transactions.amount) as netBalance 
        |        FROM customers 
        |        LEFT JOIN transactions ON customers.phone = transactions.customerPhone AND customers.ownerId = transactions.ownerId
        |        WHERE customers.ownerId = ?
        |        GROUP BY customers.id 
        |        ORDER BY netBalance DESC
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("customers", "transactions")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, ownerId)
        val _cursorIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _cursorIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _cursorIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _cursorIndexOfPhone: Int = getColumnIndexOrThrow(_stmt, "phone")
        val _cursorIndexOfPhotoUri: Int = getColumnIndexOrThrow(_stmt, "photoUri")
        val _cursorIndexOfNextPaymentDate: Int = getColumnIndexOrThrow(_stmt, "nextPaymentDate")
        val _cursorIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _cursorIndexOfNetBalance: Int = getColumnIndexOrThrow(_stmt, "netBalance")
        val _result: MutableList<CustomerWithBalance> = mutableListOf()
        while (_stmt.step()) {
          val _item: CustomerWithBalance
          val _tmpNetBalance: Double?
          if (_stmt.isNull(_cursorIndexOfNetBalance)) {
            _tmpNetBalance = null
          } else {
            _tmpNetBalance = _stmt.getDouble(_cursorIndexOfNetBalance)
          }
          val _tmpCustomer: Customer
          val _tmpId: Int
          _tmpId = _stmt.getLong(_cursorIndexOfId).toInt()
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_cursorIndexOfOwnerId)
          val _tmpName: String
          _tmpName = _stmt.getText(_cursorIndexOfName)
          val _tmpPhone: String
          _tmpPhone = _stmt.getText(_cursorIndexOfPhone)
          val _tmpPhotoUri: String?
          if (_stmt.isNull(_cursorIndexOfPhotoUri)) {
            _tmpPhotoUri = null
          } else {
            _tmpPhotoUri = _stmt.getText(_cursorIndexOfPhotoUri)
          }
          val _tmpNextPaymentDate: Long?
          if (_stmt.isNull(_cursorIndexOfNextPaymentDate)) {
            _tmpNextPaymentDate = null
          } else {
            _tmpNextPaymentDate = _stmt.getLong(_cursorIndexOfNextPaymentDate)
          }
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_cursorIndexOfCategory)
          _tmpCustomer =
              Customer(_tmpId,_tmpOwnerId,_tmpName,_tmpPhone,_tmpPhotoUri,_tmpNextPaymentDate,_tmpCategory)
          _item = CustomerWithBalance(_tmpCustomer,_tmpNetBalance)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
