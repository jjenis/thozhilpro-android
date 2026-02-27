# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in the SDK tools.

# Retrofit
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.thozhilpro.app.data.model.** { *; }
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Gson
-keep class com.google.gson.** { *; }
-keepattributes EnclosingMethod

# Razorpay
-keep class com.razorpay.** { *; }
-keepclassmembers class * implements com.razorpay.PaymentResultListener { *; }
-keepclassmembers class * implements com.razorpay.PaymentResultWithDataListener { *; }

# Firebase
-keep class com.google.firebase.** { *; }
