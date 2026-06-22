cat > ~/DeviceCloner/app/src/main/java/com/cloner/DeviceRandomizer.kt << 'KTFILE'
package com.cloner

import kotlin.random.Random

data class DeviceProfile(
        val model: String, val manufacturer: String, val brand: String,
            val device: String, val product: String, val fingerprint: String,
                val serial: String, val androidId: String, val macAddress: String,
                    val bluetoothMac: String, val imei1: String, val imei2: String,
                        val bluetoothName: String, val host: String, val displayId: String
)

object DeviceRandomizer {
        
            private val MODELS = listOf("SM-S928B","Pixel 9 Pro XL","Pixel 8 Pro","Galaxy S25 Ultra",
                    "OnePlus 13","Xiaomi 14 Pro","Nothing Phone 3","Oppo Find X8 Pro","CPH2499","RMX3700","V2029")
                        private val MFR = listOf("samsung","google","oneplus","xiaomi","oppo","nothing","realme","vivo")
                            private val BRANDS = listOf("google","samsung","oneplus","xiaomi","oppo")
                                
                                    fun getRandomDeviceProfile(): DeviceProfile {
                                                val m = MODELS[Random.nextInt(MODELS.size)]
                                                        val mfr = MFR[Random.nextInt(MFR.size)]
                                                                val br = BRANDS[Random.nextInt(BRANDS.size)]
                                                                        val dev = randomAlpha(8).lowercase()
                                                                                val prod = randomAlpha(8).lowercase()
                                                                                        return DeviceProfile(
                                                                                                        model = m, manufacturer = mfr, brand = br,
                                                                                                                    device = dev, product = prod,
                                                                                                                                fingerprint = "$br/$prod/$dev:15/${randomAlpha(6)}/${randomHex(8)}:user/release-keys",
                                                                                                                                            serial = randomHex(8).uppercase(),
                                                                                                                                                        androidId = randomHex(16),
                                                                                                                                                                    macAddress = randomMac(),
                                                                                                                                                                                bluetoothMac = randomMac().uppercase(),
                                                                                                                                                                                            imei1 = "35" + (1..13).map { Random.nextInt(10) }.joinToString(""),
                                                                                                                                                                                                        imei2 = "35" + (1..13).map { Random.nextInt(10) }.joinToString(""),
                                                                                                                                                                                                                    bluetoothName = "Galaxy ${randomAlpha(4)}",
                                                                                                                                                                                                                                host = randomAlpha(8).lowercase() + "-build-server",
                                                                                                                                                                                                                                            displayId = "AP${randomAlpha(3)}.${randomHex(6)}"
                                                                                        )
                                    }
                                        
                                            private fun randomHex(len: Int) = (1..len).map { "0123456789abcdef"[Random.nextInt(16)] }.joinToString("")
                                                private fun randomAlpha(len: Int) = (1..len).map { ('a'..'z').random() }.joinToString("")
                                                    private fun randomMac() = (1..6).joinToString(":") { String.format("%02x", Random.nextInt(256)) }
                                                        
                                                            fun generateFridaScript(profile: DeviceProfile): String {
                                                                        return """Java.perform(function() {
                                                                                console.log('[DeviceCloner] Spoofing device identity...');
                                                                                    function rh(l){return Array.from({length:l},()=>'0123456789abcdef'[Math.floor(Math.random()*16)]).join('')}
                                                                                        function rm(){return Array.from({length:6},()=>Math.floor(Math.random()*256).toString(16).padStart(2,'0')).join(':')}
                                                                                            function ra(l){const c='ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';return Array.from({length:l},()=>c[Math.floor(Math.random()*c.length)]).join('')}
                                                                                                const MODELS=['SM-S928B','Pixel 9 Pro XL','Pixel 8 Pro','Galaxy S25 Ultra','OnePlus 13','Xiaomi 14 Pro','Nothing Phone 3','Oppo Find X8 Pro','CPH2499','RMX3700','V2029'];
                                                                                                    const MFR=['samsung','google','oneplus','xiaomi','oppo','nothing','realme','vivo'];
                                                                                                        const BRANDS=['google','samsung','oneplus','xiaomi','oppo'];
                                                                                                            try{var B=Java.use('android.os.Build');B.MODEL.value=MODELS[Math.floor(Math.random()*MODELS.length)];B.MANUFACTURER.value=MFR[Math.floor(Math.random()*MFR.length)];B.BRAND.value=BRANDS[Math.floor(Math.random()*BRANDS.length)];B.DEVICE.value=ra(8).toLowerCase();B.PRODUCT.value=ra(8).toLowerCase();B.FINGERPRINT.value=BRANDS[Math.floor(Math.random()*BRANDS.length)]+'/'+ra(8)+'/'+ra(8)+':15/'+ra(6)+'/'+rh(8)+':user/release-keys';B.SERIAL.value=rh(8).toUpperCase();B.DISPLAY.value='AP'+ra(3)+'.'+rh(6);B.HOST.value=ra(8).toLowerCase()+'-build';B.TAGS.value='release-keys';B.TYPE.value='user';console.log('[DeviceCloner] Build spoofed: '+B.MODEL.value)}catch(e){}
                                                                                                                try{var S=Java.use('android.provider.Settings$Secure');S.getString.overload('android.content.ContentResolver','java.lang.String').implementation=function(cr,n){if(n==='android_id')return rh(16);if(n==='bluetooth_address'||n==='wlan_mac')return rm();return this.getString(cr,n)}}catch(e){}
                                                                                                                    try{var T=Java.use('android.telephony.TelephonyManager');T.getDeviceId.overload().implementation=function(){return '35'+Array(13).fill(0).map(()=>Math.floor(Math.random()*10)).join('')};T.getDeviceId.overload('int').implementation=function(s){return s===0?'35'+Array(13).fill(0).map(()=>Math.floor(Math.random()*10)).join(''):'35'+Array(13).fill(0).map(()=>Math.floor(Math.random()*10)).join('')};T.getImei.overload().implementation=function(){return '35'+Array(13).fill(0).map(()=>Math.floor(Math.random()*10)).join('')};T.getImei.overload('int').implementation=function(s){return '35'+Array(13).fill(0).map(()=>Math.floor(Math.random()*10)).join('')};T.getSubscriberId.overload().implementation=function(){return '310'+Array(12).fill(0).map(()=>Math.floor(Math.random()*10)).join('')};T.getSimSerialNumber.overload().implementation=function(){return '8901'+Array(16).fill(0).map(()=>Math.floor(Math.random()*10)).join('')};T.getNetworkOperator.overload().implementation=function(){var n=['310410','310260','310150'];return n[Math.floor(Math.random()*n.length)]};T.getNetworkOperatorName.overload().implementation=function(){var c=['T-Mobile','Verizon','AT&T'];return c[Math.floor(Math.random()*c.length)]};T.getPhoneType.overload().implementation=function(){return 1};T.getNetworkType.overload().implementation=function(){return 13};console.log('[DeviceCloner] Telephony spoofed')}catch(e){}
                                                                                                                        try{var W=Java.use('android.net.wifi.WifiInfo');W.getMacAddress.implementation=function(){return rm()};W.getBSSID.implementation=function(){return rm().toUpperCase()};W.getSSID.implementation=function(){var s=['"HOME_WIFI_'+rh(4)+'"','"AndroidAP_'+rh(4)+'"'];return s[Math.floor(Math.random()*s.length)]};W.getRssi.implementation=function(){return -30-Math.floor(Math.random()*50)};W.getLinkSpeed.implementation=function(){return 433+Math.floor(Math.random()*400)};W.getFrequency.implementation=function(){return Math.random()>0.5?5000:2400};console.log('[DeviceCloner] WiFi spoofed')}catch(e){}
                                                                                                                            try{var BT=Java.use('android.bluetooth.BluetoothAdapter');BT.getAddress.implementation=function(){return rm().toUpperCase()};BT.getName.implementation=function(){return 'Galaxy '+ra(4)};console.log('[DeviceCloner] Bluetooth spoofed')}catch(e){}
                                                                                                                                try{var A=Java.use('android.app.Activity');A.onResume.implementation=function(){this.onResume();console.log('[DeviceCloner] Activity resumed')} }catch(e){}
                                                                                                                                    console.log('[DeviceCloner] ALL HOOKS ACTIVE - device identity randomized');
                                                                        });"""
                                                            }
}
KTFILE
echo "✅ DeviceRandomizer.kt"
                                                                        })
                                                            }
                                                                                        )
                                    }
}
)