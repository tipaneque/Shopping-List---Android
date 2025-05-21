package com.example.listadecompras.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.example.listadecompras.adapters.ProductAdapter
import com.example.listadecompras.data.ProductDatabaseHelper
import com.example.listadecompras.R
import com.example.listadecompras.utils.globalProduct
import java.text.NumberFormat
import java.util.Locale

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupAddButton()
    }

    private fun setupAddButton() {
        val addButton = findViewById<Button>(R.id.btn_adicionar)
        addButton.setOnClickListener {
            val intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        updateProductList()
        updateTotalValue()
    }

    private fun updateProductList() {
        val listView = findViewById<ListView>(R.id.list_prod)
        val dbHelper = ProductDatabaseHelper(this)
        val products = dbHelper.getAllProducts()

        globalProduct.clear()
        globalProduct.addAll(products)

        val adapter = ProductAdapter(this).apply {
            addAll(globalProduct)
        }

        listView.adapter = adapter
        setupLongClickListener(listView, adapter)
    }


    private fun setupLongClickListener(listView: ListView, adapter: ProductAdapter) {
        listView.setOnItemLongClickListener { _, _, position, _ ->
            val item = adapter.getItem(position) ?: return@setOnItemLongClickListener true

            AlertDialog.Builder(this).apply {
                setTitle("Confirmação")
                setMessage("Tem certeza que deseja apagar este item?")
                setPositiveButton("Sim") { _, _ ->
                    // 1) Delete the item from the database
                    val deleted = ProductDatabaseHelper(this@MainActivity)
                        .deleteProductById(item.id)

                    if (deleted) {
                        // 2) Update list in memory and screen
                        adapter.remove(item)
                        globalProduct.remove(item)
                        updateTotalValue()
                        Toast.makeText(
                            this@MainActivity,
                            "Item ${item.name} apagado!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this@MainActivity,
                            "Erro ao apagar do banco.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    // 3) Reload the list (or just adapter.notifyDataSetChanged())
                    updateProductList()
                }
                setNegativeButton("Não") { dialog, _ -> dialog.dismiss() }
                show()
            }

            true
        }
    }


    private fun updateTotalValue() {
        val total = globalProduct.sumOf { it.value * it.quantity }
        val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("pt", "MZ"))
        "TOTAL: ${currencyFormatter.format(total)}".also { findViewById<TextView>(R.id.total).text = it }
    }
}
