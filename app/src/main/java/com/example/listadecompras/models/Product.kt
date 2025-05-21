package com.example.listadecompras.models

import android.graphics.Bitmap

data class Product(val id: Int = 0, val name: String, val quantity: Int, val value: Double, val photo: Bitmap? = null)