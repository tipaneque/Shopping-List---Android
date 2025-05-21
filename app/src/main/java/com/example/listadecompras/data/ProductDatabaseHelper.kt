package com.example.listadecompras.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import com.example.listadecompras.models.Product

class ProductDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "product_db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE products (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL,
                quantity INTEGER NOT NULL,
                value REAL NOT NULL
            )
        """
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS products")
        onCreate(db)
    }

    fun insertProduct(product: Product): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("name", product.name)
            put("quantity", product.quantity)
            put("value", product.value)
        }
        val id = db.insert("products", null, values)
        db.close()
        return id // retorna o ID gerado, pode ser usado se necessário
    }


    fun deleteProductById(id: Int): Boolean {
        val db = writableDatabase
        val rowsAffected = db.delete(
            "products",
            "id = ?",
            arrayOf(id.toString())
        )
        db.close()
        return rowsAffected > 0
    }

    fun getAllProducts(): List<Product> {
        val products = mutableListOf<Product>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM products", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"))
                val value = cursor.getDouble(cursor.getColumnIndexOrThrow("value"))

                products.add(Product(id, name, quantity, value))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return products
    }

}
