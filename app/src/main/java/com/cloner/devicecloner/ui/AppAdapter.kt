package com.cloner.devicecloner.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cloner.devicecloner.databinding.ItemAppBinding

class AppAdapter(private val onAppClick: (AppInfo) -> Unit) : 
    ListAdapter<AppInfo, AppAdapter.ViewHolder>(AppDiffCallback()) {
    
    inner class ViewHolder(private val binding: ItemAppBinding) : 
        RecyclerView.ViewHolder(binding.root) {
        
        fun bind(app: AppInfo) {
            binding.appIcon.setImageDrawable(app.icon)
            binding.appName.text = app.appName
            binding.appPackage.text = app.packageName
            binding.appVersion.text = "v${app.versionName} (${app.versionCode})"
            binding.appSystemTag.text = if (app.isSystemApp) "[SYSTEM]" else "[USER]"
            
            binding.root.setOnClickListener {
                onAppClick(app)
            }
        }
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAppBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class AppDiffCallback : DiffUtil.ItemCallback<AppInfo>() {
        override fun areItemsTheSame(oldItem: AppInfo, newItem: AppInfo) =
            oldItem.packageName == newItem.packageName
        
        override fun areContentsTheSame(oldItem: AppInfo, newItem: AppInfo) =
            oldItem == newItem
    }
}
