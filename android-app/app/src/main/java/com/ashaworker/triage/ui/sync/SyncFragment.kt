package com.ashaworker.triage.ui.sync

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ashaworker.triage.databinding.SyncFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SyncFragment : Fragment() {

    private var _binding: SyncFragmentBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SyncViewModel by viewModels()
    private val adapter = SyncResultAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = SyncFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvSyncResults.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSyncResults.adapter = adapter

        val online = isConnected()
        binding.btnSyncNow.isEnabled = online

        binding.btnSyncNow.setOnClickListener { viewModel.syncNow() }

        lifecycleScope.launch {
            viewModel.unsyncedCount.collect {
                binding.tvUnsyncedCount.text = "Unsynced visits: $it"
            }
        }
        lifecycleScope.launch {
            viewModel.lastSyncLabel.collect {
                binding.tvLastSync.text = "Last sync: $it"
            }
        }
        lifecycleScope.launch {
            viewModel.logs.collect { adapter.submitList(it) }
        }
        lifecycleScope.launch {
            viewModel.syncing.collect {
                binding.progressSync.visibility = if (it) View.VISIBLE else View.GONE
                binding.btnSyncNow.isEnabled = !it
            }
        }

        viewModel.refresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun isConnected(): Boolean {
        val cm = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
