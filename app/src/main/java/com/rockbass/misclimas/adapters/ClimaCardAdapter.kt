package com.rockbass.misclimas.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.rockbass.misclimas.R
import com.rockbass.misclimas.databinding.ClimaCardBinding
import com.rockbass.misclimas.db.entities.Data

class ClimaCardAdapter(private val data: List<Data>) : RecyclerView.Adapter<ClimaCardAdapter.CardViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view: ClimaCardBinding = DataBindingUtil.inflate(layoutInflater,
            R.layout.clima_card, parent, false)
        return CardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val clima = data[position]
        holder.binding(clima)
    }

    class CardViewHolder(private val bindingAdapter: ClimaCardBinding) :
        RecyclerView.ViewHolder(bindingAdapter.root){

        fun binding(data: Data){
            bindingAdapter.clima = data
            bindingAdapter.executePendingBindings()
        }
    }
}