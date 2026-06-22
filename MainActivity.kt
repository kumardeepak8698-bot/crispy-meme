cat > ~/DeviceCloner/app/src/main/java/com/cloner/MainActivity.kt << 'KTFILE'
package com.cloner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.cloner.devicecloner.databinding.ActivityMainBinding
import com.cloner.ui.AppAdapter
import com.cloner.ui.AppInfo
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var cloneManager: CloneManager
    private lateinit var appAdapter: AppAdapter
    private val mainScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cloneManager = CloneManager(this)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Device Cloner Pro"

        appAdapter = AppAdapter { appInfo -> showCloneDialog(appInfo) }
        binding.appList.layoutManager = LinearLayoutManager(this)
        binding.appList.adapter = appAdapter

        binding.refreshBtn.setOnClickListener {
            if (checkPermissions()) loadInstalledApps()
        }

        requestAllPermissions()
    }


    private fun checkPermissions(): Boolean {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) return true
        return androidx.core.content.ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }
    private fun requestAllPermissions() {
        val needed = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q)
            needed.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                // Try anyway
            }
        }
        if (needed.isNotEmpty())
            ActivityCompat.requestPermissions(this, needed.toTypedArray(), 100)
        
        // Ensure install from unknown apps is allowed
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !packageManager.canRequestPackageInstalls()) {
            val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                data = Uri.parse("package:$packageName")
            }
            startActivity(intent)
        }
        
        loadInstalledApps()
    }

    private fun loadInstalledApps() {
        binding.progressBar.visibility = android.view.View.VISIBLE
        binding.statusText.text = "Loading installed apps..."
        mainScope.launch {
            val apps = withContext(Dispatchers.IO) { cloneManager.getInstalledApps() }
            appAdapter.submitList(apps)
            binding.progressBar.visibility = android.view.View.GONE
            binding.statusText.text = "${apps.size} apps found"
        }
    }

    private fun showCloneDialog(appInfo: AppInfo) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Clone '${appInfo.appName}'")
            .setMessage("Create a cloned version with RANDOMIZED device identity?\n\n" +
                "Every time the clone launches, it will show DIFFERENT device details:\n" +
                "• Random model, manufacturer, brand\n" +
                "• Random Android ID, IMEI, MAC address\n" +
                "• Random Bluetooth name & address\n" +
                "• Frida gadget injected for runtime spoofing\n\n" +
                "The original app is NOT modified.")
            .setPositiveButton("✅ Clone & Install") { _, _ -> startCloning(appInfo) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun startCloning(appInfo: AppInfo) {
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("🔄 Cloning ${appInfo.appName}")
            .setMessage("Starting...")
            .setCancelable(false)
            .show()

        mainScope.launch {
            val result = cloneManager.cloneApp(appInfo.packageName) { progress ->
                withContext(Dispatchers.Main) { dialog.setMessage(progress) }
            }
            dialog.dismiss()

            if (result.success) {
                MaterialAlertDialogBuilder(this@MainActivity)
                    .setTitle("✅ CLONE SUCCESSFUL!")
                    .setMessage(result.message)
                    .setPositiveButton("OK") { _, _ -> loadInstalledApps() }
                    .show()
            } else {
                MaterialAlertDialogBuilder(this@MainActivity)
                    .setTitle("❌ Clone Failed")
                    .setMessage(result.message)
                    .setPositiveButton("OK", null)
                    .show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainScope.cancel()
    }
}
KTFILE
echo "✅ MainActivity.kt"
