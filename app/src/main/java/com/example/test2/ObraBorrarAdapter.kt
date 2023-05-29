package com.example.test2

import android.view.LayoutInflater

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.test2.databinding.ItemObraBorrarBinding

class ObraBorrarAdapter() : ListAdapter<ObraModel, ObraBorrarAdapter.ObraBorrarViewHolder>(Companion){
    private var onClickItem: ((ObraModel) -> Unit)? = null
    private var onClickFotoItem: ((ObraModel) -> Unit)? = null


    inner class ObraBorrarViewHolder(val binding: ItemObraBorrarBinding): RecyclerView.ViewHolder(binding.root)

    companion object : DiffUtil.ItemCallback<ObraModel>() {
        override fun areItemsTheSame(oldItem: ObraModel, newItem: ObraModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ObraModel, newItem: ObraModel): Boolean {
            return oldItem == newItem
        }
    }

    fun setOnClickItem(callback: (ObraModel) -> Unit) {
        this.onClickItem = callback
    }

    fun setOnClickFotoItem(callback: (ObraModel) -> Unit) {
        this.onClickFotoItem = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObraBorrarViewHolder {
        return ObraBorrarViewHolder(
            ItemObraBorrarBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ObraBorrarViewHolder, position: Int) {
        val std = currentList[position]
        holder.binding.apply {
            obraNumero.text = std.obra
        }
        holder.itemView.setOnClickListener {
            onClickItem?.invoke(std)
        }
        holder.binding.borrarButton.setOnClickListener {
            onClickFotoItem?.invoke(std)
        }

    }




    override fun getItemCount(): Int {
        return currentList.size
    }

}