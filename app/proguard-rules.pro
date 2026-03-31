# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# ML Kit Document Scanner rules
-keep class com.google.mlkit.** { *; }
-keep class com.google.android.gms.internal.mlkit_vision_documentscanner.** { *; }

# General Google Play Services / ML Kit safety
-keep class com.google.android.gms.common.internal.safeparcel.SafeParcelable { *; }
-keepnames class com.google.android.gms.common.internal.ReflectedParcelable
-keepnames class com.google.android.gms.common.internal.safeparcel.SafeParcelable
-keepnames class * extends com.google.android.gms.common.internal.safeparcel.SafeParcelable

# Keep native methods and their classes
-keepclasseswithmembernames class * {
    native <methods>;
}