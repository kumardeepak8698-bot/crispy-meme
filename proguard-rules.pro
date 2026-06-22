cat > ~/DeviceCloner/app/proguard-rules.pro << 'EOF'
-keep class com.cloner.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
EOF
echo "✅ proguard-rules.pro"
