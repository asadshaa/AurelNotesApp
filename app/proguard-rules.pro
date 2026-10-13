# Aurel Proguard Rules
-keepattributes *Annotation*
-dontwarn javax.annotation.**

# DataStore & Protobuf Rules
-keepclassmembers class * extends androidx.datastore.preferences.core.Preferences {
    *;
}

# Coroutines Debug Metadata
-keepclassmembers class kotlinx.coroutines.** {
    *;
}
