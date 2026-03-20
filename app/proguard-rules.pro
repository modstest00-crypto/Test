# Add project specific ProGuard rules here.
-keep class com.snapsort.app.model.** { *; }
-keep class com.snapsort.app.adapter.** { *; }
-keep class com.snapsort.app.util.** { *; }

# TensorFlow Lite
-dontwarn org.tensorflow.**
-keep class org.tensorflow.** { *; }

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule {
 <init>(...);
}
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class com.bumptech.glide.load.data.ParcelFileDescriptorRewinder$InternalRewinder {
  *** rewind();
}
