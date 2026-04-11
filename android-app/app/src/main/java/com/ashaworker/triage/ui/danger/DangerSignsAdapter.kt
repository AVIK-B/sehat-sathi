package com.ashaworker.triage.ui.danger

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ashaworker.triage.data.model.DangerSign
import com.ashaworker.triage.databinding.ItemDangerSignBinding

class DangerSignsAdapter(
    private val onCheckedChanged: (Int, Boolean) -> Unit
) : RecyclerView.Adapter<DangerSignsAdapter.DangerViewHolder>() {

    private val items = mutableListOf<DangerSign>()
    private val checked = mutableMapOf<Int, Boolean>()

    fun submitList(data: List<DangerSign>) {
        items.clear()
        items.addAll(data)
        checked.clear()
        notifyDataSetChanged()
    }

    fun hasAnyChecked(): Boolean = checked.values.any { it }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DangerViewHolder {
        val binding = ItemDangerSignBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DangerViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: DangerViewHolder, position: Int) {
        holder.bind(items[position], checked[position] == true)
    }

    inner class DangerViewHolder(
        private val binding: ItemDangerSignBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DangerSign, isChecked: Boolean) {
            binding.tvText.text = item.text
            binding.tvHindi.text = item.hindi
            binding.cbDanger.setOnCheckedChangeListener(null)
            binding.cbDanger.isChecked = isChecked
            binding.cbDanger.setOnCheckedChangeListener { _, value ->
                checked[bindingAdapterPosition] = value
                onCheckedChanged(bindingAdapterPosition, value)
            }
            binding.root.setOnClickListener {
                binding.cbDanger.isChecked = !binding.cbDanger.isChecked
            }
        }
    }
}
