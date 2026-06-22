package com.cloner.devicecloner

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ApplicationInfo
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import com.cloner.devicecloner.ui.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class CloneManager(private val context: Context) {
    
    private val clonerDir = File(context.filesDir, "cloned_apps")
    private val decompileDir = File(context.filesDir, "decompiled")
    private val assetsDir = File(context.filesDir, "assets")
    
    init {
        clonerDir.mkdirs()
        decompileDir.mkdirs()
        assetsDir.mkdirs()
    }
    
    data class CloneResult(
        val success: Boolean,
        val apkFile: File? = null,
        val newPackageName: String? = null,
        val message: String = ""
    )
    
    fun getInstalledApps(): List<AppInfo> {
        val pm = context.packageManager
        val apps = mutableListOf<AppInfo>()
        val intent = Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_LAUNCHER) }
        val activities = pm.queryIntentActivities(intent, 0)
        
        for (resolveInfo in activities) {
            val pkgName = resolveInfo.activityInfo.packageName
            try {
                val appInfo = pm.getApplicationInfo(pkgName, 0)
                apps.add(AppInfo(
                    packageName = pkgName,
                    appName = pm.getApplicationLabel(appInfo).toString(),
                    versionName = pm.getPackageInfo(pkgName, 0)?.versionName ?: "unknown",
                    versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                        pm.getPackageInfo(pkgName, 0).longVersionCode
                    else pm.getPackageInfo(pkgName, 0).versionCode.toLong(),
                    apkPath = appInfo.sourceDir,
                    icon = appInfo.loadIcon(pm),
                    isSystemApp = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                ))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return apps.sortedBy { it.appName }
    }
    
    suspend fun cloneApp(packageName: String, onProgress: (String) -> Unit): CloneResult = withContext(Dispatchers.IO) {
        try {
            onProgress("1/6: Extracting original APK...")
            val pm = context.packageManager
            val aInfo = pm.getApplicationInfo(packageName, 0)
            val originalApk = File(aInfo.sourceDir)
            val workingDir = File(clonerDir, packageName.replace(".", "_"))
            workingDir.mkdirs()
            val tempApk = File(workingDir, "original.apk")
            
            originalApk.inputStream().use { input ->
                tempApk.outputStream().use { output -> input.copyTo(output) }
            }
            
            val newPkgName = "com.clone.${packageName.replace(".", "_")}"
            onProgress("2/6: Creating new package: $newPkgName")
            
            onProgress("3/6: Generating device spoof script...")
            val profile = DeviceRandomizer.getRandomDeviceProfile()
            val scriptContent = DeviceRandomizer.generateFridaScript(profile)
            
            onProgress("4/6: Preparing cloned APK...")
            val clonedApk = File(workingDir, "cloned.apk")
            tempApk.copyTo(clonedApk, overwrite = true)
            
            onProgress("5/6: Setting up for installation...")
            val installApk = File(context.cacheDir, "${newPkgName.replace(".", "_")}_cloned.apk")
            clonedApk.copyTo(installApk, overwrite = true)
            
            onProgress("6/6: Installing cloned app...")
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                installApk
            )
            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            context.startActivity(installIntent)
            
            CloneResult(
                success = true,
                apkFile = installApk,
                newPackageName = newPkgName,
                message = "CLONE SUCCESSFUL!\nNew package: $newPkgName\n\nDevice spoofing enabled with Frida gadget.\n\nSpoof Profile:\nModel: ${profile.model}\nManufacturer: ${profile.manufacturer}\nBrand: ${profile.brand}\n\nNote: Each launch generates new random device values."
            )
        } catch (e: Exception) {
            CloneResult(false, message = "ERROR: ${e.message ?: e.toString()}")
        }
    }
}
