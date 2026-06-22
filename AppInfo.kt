cat > ~/DeviceCloner/app/src/main/java/com/cloner/ui/AppInfo.kt << 'KTFILE'
package com.cloner.ui

import android.graphics.drawable.Drawable

data class AppInfo(
        val packageName: String,
            val appName: String,
                val versionName: String,
                    val versionCode: Long,
                        val apkPath: String,
                            val icon: Drawable? = null,
                                val isSystemApp: Boolean = false
)
KTFILE
echo "✅ AppInfo.kt"
)