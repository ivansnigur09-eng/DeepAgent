# Add project specific ProGuard rules here.
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception

# Keep JWT classes
-keep class com.auth0.jwt.** { *; }

# Keep security classes
-keep class androidx.security.crypto.** { *; }
