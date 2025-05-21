package com.example.listadecompras.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
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
            val intent = Intent(this, CadastroActivity::class.java)
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
                setTitle("Confirmation")
                setMessage("Tem certeza que deseja apagar este item?")
                setPositiveButton("Yes") { _, _ ->
                    // 1) deleta do banco
                    val deleted = ProductDatabaseHelper(this@MainActivity)
                        .deleteProductById(item.id)

                    if (deleted) {
                        // 2) atualiza lista em memória e tela
                        adapter.remove(item)
                        globalProduct.remove(item)
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

                    // 3) recarrega a lista (ou apenas adapter.notifyDataSetChanged())
                    updateProductList()
                }
                setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
                show()
            }

            true
        }
    }


    private fun updateTotalValue() {
        val total = globalProduct.sumOf { it.value * it.quantity }
        val currencyFormatter = NumberFormat.getCurrencyInstance(Locale("pt", "MZ"))
        findViewById<TextView>(R.id.total).text = "TOTAL: ${currencyFormatter.format(total)}"
    }
}
