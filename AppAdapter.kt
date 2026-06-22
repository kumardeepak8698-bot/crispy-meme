cat > ~/DeviceCloner/app/src/main/java/com/cloner/ui/AppAdapter.kt << 'KTFILE'
package com.cloner.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cloner.devicecloner.R

class AppAdapter(
    private val onCloneClick: (AppInfo) -> Unit
) : ListAdapter<AppInfo, AppAdapter.AppViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_app, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        holder.bind(getItem(position), onCloneClick)
    }

    class AppViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val appIcon: ImageView = itemView.findViewById(R.id.appIcon)
        private val appName: TextView = itemView.findViewById(R.id.appName)
        private val appPackage: TextView = itemView.findViewById(R.id.appPackage)
        private val appVersion: TextView = itemView.findViewById(R.id.appVersion)
        private val cloneBtn: com.google.android.material.button.MaterialButton = itemView.findViewById(R.id.cloneBtn)

        fun bind(appInfo: AppInfo, onCloneClick: (AppInfo) -> Unit) {
            appIcon.setImageDrawable(appInfo.icon)
            appName.text = appInfo.appName
            appPackage.text = appInfo.packageName
            appVersion.text = "v${appInfo.versionName}"
            cloneBtn.setOnClickListener { onCloneClick(appInfo) }
            itemView.setOnClickListener { onCloneClick(appInfo) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<AppInfo>() {
        override fun areItemsTheSame(o1: AppInfo, o2: AppInfo) = o1.packageName == o2.packageName
        override fun areContentsTheSame(o1: AppInfo, o2: AppInfo) = o1 == o2
    }
}
KTFILE
echo "✅ AppAdapter.kt"
