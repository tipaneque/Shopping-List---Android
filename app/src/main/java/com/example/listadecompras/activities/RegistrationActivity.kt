package com.example.listadecompras.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.example.listadecompras.models.Product
import com.example.listadecompras.data.ProductDatabaseHelper
import com.example.listadecompras.R
import com.example.listadecompras.utils.globalProduct
import java.io.ByteArrayOutputStream

class RegistrationActivity : Activity() {
    private val REQUEST_CODE_IMAGE = 101
    private val dbHelper by lazy { ProductDatabaseHelper(this) }
    private var productImageBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        val imageProduct = findViewById<ImageView>(R.id.igm_photo_product)
        val inputProductName = findViewById<EditText>(R.id.txt_produto)
        val inputQuantity = findViewById<EditText>(R.id.txt_qtde)
        val inputPrice = findViewById<EditText>(R.id.txt_valor)
        val buttonInsert = findViewById<Button>(R.id.btn_insert)

        imageProduct.setOnClickListener {
            openGallery()
        }

        buttonInsert.setOnClickListener {
            val productName = inputProductName.text.toString().trim()
            val quantity = inputQuantity.text.toString().trim()
            val price = inputPrice.text.toString().trim()

            if (productName.isNotEmpty() && quantity.isNotEmpty() && price.isNotEmpty()) {
                try {
                    val imageBytes = productImageBitmap?.let { bitmap ->
                        val stream = ByteArrayOutputStream()
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                        stream.toByteArray()
                    }

                    val product = Product(
                        id = 0, // será autogerado no banco
                        name = productName,
                        quantity = quantity.toInt(),
                        value = price.toDouble(),
                        //photo = imageBytes
                    )

                    val insertedId = dbHelper.insertProduct(product)
                    if (insertedId != -1L) {
                        globalProduct.add(product.copy(id = insertedId.toInt()))
                        inputProductName.text.clear()
                        inputQuantity.text.clear()
                        inputPrice.text.clear()
                        productImageBitmap = null
                        imageProduct.setImageResource(android.R.drawable.ic_menu_camera)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                val inputStream = contentResolver.openInputStream(uri)
                productImageBitmap = BitmapFactory.decodeStream(inputStream)
                findViewById<ImageView>(R.id.igm_photo_product).setImageBitmap(productImageBitmap)
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(Intent.createChooser(intent, "Select an image"), REQUEST_CODE_IMAGE)
    }
}
