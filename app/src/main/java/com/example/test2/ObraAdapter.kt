package com.example.test2

import android.view.LayoutInflater

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.test2.databinding.ItemObraBinding

class ObraAdapter() : ListAdapter<ObraModel, ObraAdapter.ObraViewHolder>(Companion){
    private var onClickItem: ((ObraModel) -> Unit)? = null
    private var onClickFotoItem: ((ObraModel) -> Unit)? = null


    inner class ObraViewHolder(val binding: ItemObraBinding): RecyclerView.ViewHolder(binding.root)

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObraViewHolder {
        return ObraViewHolder(
            ItemObraBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ObraViewHolder, position: Int) {
        val std = currentList[position]
        holder.binding.apply {
            obraId.text = std.obra
            descripcionId.text = std.descripcion
        }
        holder.itemView.setOnClickListener {
            onClickItem?.invoke(std)
        }
        holder.binding.fotoButton.setOnClickListener {
            onClickFotoItem?.invoke(std)
        }

    }




    override fun getItemCount(): Int {
       return currentList.size
    }
}