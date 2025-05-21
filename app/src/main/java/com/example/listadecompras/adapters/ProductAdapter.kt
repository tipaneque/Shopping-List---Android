package com.example.listadecompras.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.listadecompras.models.Product
import com.example.listadecompras.R

class ProductAdapter(context: Context) : ArrayAdapter<Product>(context, 0) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val v: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_view_item, parent, false)

        val item = getItem(position)
        val imgPhoto = v.findViewById<ImageView>(R.id.img_item_foto)
        val txtProdName = v.findViewById<TextView>(R.id.txt_item_produto)
        val txtQuantity = v.findViewById<TextView>(R.id.txt_item_qty)
        val txtPrice = v.findViewById<TextView>(R.id.txt_item_valor)

        txtProdName.text = item?.name
        txtQuantity.text = item?.quantity.toString()
        txtPrice.text = item?.value.toString()
        if(item?.photo != null){
            imgPhoto.setImageBitmap(item.photo)
        }

        return v
    }
}