package com.cloner.devicecloner

import kotlin.random.Random

data class DeviceProfile(
    val model: String,
    val manufacturer: String,
    val device: String,
    val brand: String,
    val fingerprint: String,
    val serial: String,
    val androidId: String,
    val imei: String,
    val bluetoothAddress: String,
    val macAddress: String,
    val bluetoothName: String
)

object DeviceRandomizer {
    
    private val models = listOf(
        "Pixel 8 Pro", "Pixel 8", "Galaxy S24 Ultra", "Galaxy S24",
        "OnePlus 12", "Xiaomi 14", "iPad Air", "Galaxy Tab S9",
        "Pixel Fold", "Galaxy Z Fold 5", "Galaxy Z Flip 5"
    )
    
    private val manufacturers = listOf(
        "Google", "Samsung", "OnePlus", "Xiaomi",
        "OPPO", "Vivo", "Realme", "Nothing", "Motorola",
        "Sony", "HTC", "LG", "Huawei", "ZTE"
    )
    
    private val brands = listOf(
        "google", "samsung", "oneplus", "xiaomi",
        "oppo", "vivo", "realme", "nothing", "motorola",
        "sony", "htc", "lg", "huawei", "zte"
    )
    
    private val devices = listOf(
        "bluejay", "cheetah", "oriole", "raven",
        "shiba", "husky", "lynx", "tangorpro",
        "caiman", "cloudripper", "marlin", "sailfish",
        "redfin", "barbet", "coral", "flame"
    )
    
    fun getRandomDeviceProfile(): DeviceProfile {
        val model = models[Random.nextInt(models.size)]
        val manufacturer = manufacturers[Random.nextInt(manufacturers.size)]
        val brand = brands[Random.nextInt(brands.size)]
        val device = devices[Random.nextInt(devices.size)]
        val serial = generateRandomSerial()
        val androidId = generateRandomAndroidId()
        val imei = generateRandomIMEI()
        val bluetoothAddress = generateRandomBluetoothAddress()
        val macAddress = generateRandomMacAddress()
        val bluetoothName = "$brand Device ${Random.nextInt(1000, 9999)}"
        val fingerprint = "$manufacturer/$device:${Random.nextInt(10, 15)}.${Random.nextInt(0, 3)}.${Random.nextInt(1, 30)}/build:user/release-keys"
        
        return DeviceProfile(
            model = model,
            manufacturer = manufacturer,
            device = device,
            brand = brand,
            fingerprint = fingerprint,
            serial = serial,
            androidId = androidId,
            imei = imei,
            bluetoothAddress = bluetoothAddress,
            macAddress = macAddress,
            bluetoothName = bluetoothName
        )
    }
    
    fun generateFridaScript(profile: DeviceProfile): String {
        return """
var Build = Java.use('android.os.Build');

// Random device value generators
var randomModel = function() {
    var models = ['Pixel 8 Pro', 'Galaxy S24', 'OnePlus 12', 'Xiaomi 14'];
    return models[Math.floor(Math.random() * models.length)];
};

var randomManufacturer = function() {
    var mfgs = ['Google', 'Samsung', 'OnePlus', 'Xiaomi', 'OPPO'];
    return mfgs[Math.floor(Math.random() * mfgs.length)];
};

var randomBrand = function() {
    var brands = ['google', 'samsung', 'oneplus', 'xiaomi', 'oppo'];
    return brands[Math.floor(Math.random() * brands.length)];
};

var randomSerial = function() {
    var chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
    var result = '';
    for (var i = 0; i < 16; i++) {
        result += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    return result;
};

var randomIMEI = function() {
    var result = '';
    for (var i = 0; i < 15; i++) {
        result += Math.floor(Math.random() * 10);
    }
    return result;
};

var randomAndroidId = function() {
    var hex = '0123456789abcdef';
    var result = '';
    for (var i = 0; i < 16; i++) {
        result += hex.charAt(Math.floor(Math.random() * hex.length));
    }
    return result;
};

// Hook Build properties
try {
    var modelField = Build.class.getField('MODEL');
    modelField.setAccessible(true);
    modelField.set(null, randomModel());
} catch(e) {}

try {
    var mfgField = Build.class.getField('MANUFACTURER');
    mfgField.setAccessible(true);
    mfgField.set(null, randomManufacturer());
} catch(e) {}

try {
    var brandField = Build.class.getField('BRAND');
    brandField.setAccessible(true);
    brandField.set(null, randomBrand());
} catch(e) {}

try {
    var serialField = Build.class.getField('SERIAL');
    serialField.setAccessible(true);
    serialField.set(null, randomSerial());
} catch(e) {}

console.log('[DeviceSpoof] Hooked at startup');
console.log('[DeviceSpoof] MODEL: ' + randomModel());
console.log('[DeviceSpoof] MANUFACTURER: ' + randomManufacturer());
console.log('[DeviceSpoof] BRAND: ' + randomBrand());
""".trimIndent()
    }
    
    private fun generateRandomSerial(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..16).map { chars[Random.nextInt(chars.length)] }.joinToString("")
    }
    
    private fun generateRandomAndroidId(): String {
        val hex = "0123456789abcdef"
        return (1..16).map { hex[Random.nextInt(hex.length)] }.joinToString("")
    }
    
    private fun generateRandomIMEI(): String {
        return (1..15).map { Random.nextInt(10) }.joinToString("")
    }
    
    private fun generateRandomMacAddress(): String {
        return (1..6).map { Random.nextInt(256) }
            .joinToString(":") { "%02X".format(it) }
    }
    
    private fun generateRandomBluetoothAddress(): String {
        return (1..6).map { Random.nextInt(256) }
            .joinToString(":") { "%02X".format(it) }
    }
}
