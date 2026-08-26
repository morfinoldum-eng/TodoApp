# ProGuard rules for TodoApp
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep model classes
-keep class com.todoapp.model.** { *; }
-keep class com.todoapp.data.** { *; }

# Keep Compose classes
-keep class androidx.compose.** { *; }
-keep interface androidx.compose.** { *; }

# Keep Room classes
-keep class androidx.room.** { *; }
-keepclassmembers class * extends androidx.room.RoomDatabase { *; }

# Keep ViewModels
-keep class androidx.lifecycle.** { *; }
-keepclassmembers class * extends androidx.lifecycle.ViewModel { *; }

# Keep Kotlin
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }

# Keep annotations
-keepattributes *Annotation*
-keepattributes Signature

# Remove logging
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
}
