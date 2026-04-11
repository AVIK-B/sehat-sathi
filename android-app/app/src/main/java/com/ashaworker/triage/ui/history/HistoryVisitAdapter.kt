package com.ashaworker.triage.ui.history

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ashaworker.triage.R
import com.ashaworker.triage.data.db.VisitEntity
import com.ashaworker.triage.databinding.ItemHistoryVisitBinding
import com.ashaworker.triage.util.DateUtils

class HistoryVisitAdapter : RecyclerView.Adapter<HistoryVisitAdapter.VisitViewHolder>() {

    private val items = mutableListOf<VisitEntity>()

    fun submitList(data: List<VisitEntity>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VisitViewHolder {
        val binding = ItemHistoryVisitBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return VisitViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VisitViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class VisitViewHolder(
        private val binding: ItemHistoryVisitBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: VisitEntity) {
            binding.tvDate.text = DateUtils.formatTimestamp(item.visitTimestamp)
            binding.tvPatientType.text = item.patientType
            binding.tvResultBadge.text = item.triageResult
            val color = when (item.triageResult) {
                "RED" -> binding.root.context.getColor(R.color.red_emergency)
                "YELLOW" -> binding.root.context.getColor(R.color.yellow_referral)
                else -> binding.root.context.getColor(R.color.green_home)
            }
            binding.tvResultBadge.setBackgroundColor(color)
            binding.tvLocation.text = "${item.latitude ?: 0.0}, ${item.longitude ?: 0.0}"
            if (item.synced) {
                binding.tvSyncState.text = binding.root.context.getString(R.string.status_synced)
                binding.tvSyncState.setTextColor(binding.root.context.getColor(R.color.green_home))
            } else {
                binding.tvSyncState.text = binding.root.context.getString(R.string.unsynced)
                binding.tvSyncState.setTextColor(binding.root.context.getColor(R.color.red_emergency))
            }
        }
    }
}
