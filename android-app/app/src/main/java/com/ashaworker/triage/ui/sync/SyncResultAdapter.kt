package com.ashaworker.triage.ui.sync

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ashaworker.triage.databinding.ItemSyncResultBinding

class SyncResultAdapter : RecyclerView.Adapter<SyncResultAdapter.SyncViewHolder>() {

    private val items = mutableListOf<String>()

    fun submitList(data: List<String>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SyncViewHolder {
        val binding = ItemSyncResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SyncViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: SyncViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class SyncViewHolder(private val binding: ItemSyncResultBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(text: String) {
            binding.tvSyncResult.text = text
        }
    }
}
