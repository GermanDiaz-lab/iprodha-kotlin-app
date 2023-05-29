package com.example.test2

import android.view.LayoutInflater

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.test2.databinding.ItemInformeBinding

class InformeAdapter() : ListAdapter<InformeModel, InformeAdapter.InformeViewHolder>(Companion){
    private var onClickItem: ((InformeModel) -> Unit)? = null
    private var onClickFotoItem: ((InformeModel) -> Unit)? = null


    inner class InformeViewHolder(val binding: ItemInformeBinding): RecyclerView.ViewHolder(binding.root)

    companion object : DiffUtil.ItemCallback<InformeModel>() {
        override fun areItemsTheSame(oldItem: InformeModel, newItem: InformeModel): Boolean {
            return oldItem.titulardni == newItem.titulardni
        }

        override fun areContentsTheSame(oldItem: InformeModel, newItem: InformeModel): Boolean {
            return oldItem == newItem
        }
    }

    fun setOnClickItem(callback: (InformeModel) -> Unit) {
        this.onClickItem = callback
    }

    fun setOnClickFotoItem(callback: (InformeModel) -> Unit) {
        this.onClickFotoItem = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InformeViewHolder {
        return InformeViewHolder(
            ItemInformeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: InformeViewHolder, position: Int) {
        val std = currentList[position]
        holder.binding.apply {
            dniNumero.text = std.titulardni
        }

        holder.binding.guardarInformeButton.setOnClickListener {
            onClickFotoItem?.invoke(std)
        }

    }




    override fun getItemCount(): Int {
        return currentList.size
    }
}