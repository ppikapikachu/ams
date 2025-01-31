-keepattributes Exceptions,InnerClasses,*Annotation*,Signature,EnclosingMethod

-dontshrink
-dontoptimize
-dontpreverify
-dontnote
-ignorewarnings

-keepclassmembers enum * {
    public static <methods>;
}

# 避免混淆Annotation、内部类、泛型、匿名类
-keepattributes *Annotation*,InnerClasses,Signature,EnclosingMethod

-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-keep class * extends android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}
-keep,allowshrinking class * extends dji.publics.DJIUI.** {
    public <methods>;
}
#加固后的AAR，其内容无法被混淆工具识别，所以MSDK外部依赖的类，必须被Keep。
-keep class net.sqlcipher.** { *; }
-keep class net.sqlcipher.database.* { *; }
-keep class dji.** { *; }
-keep class com.dji.** { *; }
-keep class djimrtc.** { *; }
-keep class com.google.** { *; }
-keep class org.bouncycastle.** { *; }
-keep class org.** { *; }
-keep class com.squareup.wire.** { *; }
-keep class sun.misc.Unsafe { *; }
-keep class com.secneo.** {*;}
-keep class org.greenrobot.eventbus.**{*;}
-keep class com.commonsware.**{*;}
-keep class io.reactivex.**{*;}
-keep class okhttp3.**{*;}
-keep class okio.**{*;}
-keep class org.bouncycastle.**{*;}
-keep class sun.**{*;}
-keep class java.**{*;}
-keep class com.amap.api.**{*;}
-keep class com.here.**{*;}
-keep class com.mapbox.**{*;}
-keep class retrofit2.**{*;}

-keepclasseswithmembers,allowshrinking class * {
    native <methods>;
}

-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

-keep class androidx.appcompat.widget.SearchView { *; }

-keepclassmembers class * extends android.app.Service
-keepclassmembers public class * extends android.view.View {
    void set*(***);
    *** get*();
}
-keepclassmembers class * extends android.app.Activity {
    public void *(android.view.View);
}
-keep class kotlin.** { *; }
-keep class androidx.** { *; }
-keep class android.** { *; }
-keep class com.android.** { *; }
-keep class android.media.** { *; }
-keep class okio.** { *; }
-keep class com.lmax.disruptor.** {
    *;
}

-dontwarn com.mapbox.services.android.location.LostLocationEngine
-dontwarn com.mapbox.services.android.location.MockLocationEngine
-keepclassmembers class * implements android.arch.lifecycle.LifecycleObserver {
    <init>(...);
}
# ViewModel's empty constructor is considered to be unused by proguard
-keepclassmembers class * extends android.arch.lifecycle.ViewModel {
    <init>(...);
}
# keep Lifecycle State and Event enums values
-keepclassmembers class android.arch.lifecycle.Lifecycle$State { *; }
-keepclassmembers class android.arch.lifecycle.Lifecycle$Event { *; }
# keep methods annotated with @OnLifecycleEvent even if they seem to be unused
# (Mostly for LiveData.LifecycleBoundObserver.onStateChange(), but who knows)
-keepclassmembers class * {
    @android.arch.lifecycle.OnLifecycleEvent *;
}

-keepclassmembers class * implements android.arch.lifecycle.LifecycleObserver {
    <init>(...);
}

-keep class * implements android.arch.lifecycle.LifecycleObserver {
    <init>(...);
}
-keepclassmembers class android.arch.** { *; }
-keep class android.arch.** { *; }
-dontwarn android.arch.**


#<------------ utmiss config start------------>
-keep class dji.sdk.utmiss.** { *; }
-keep class utmisslib.** { *; }
#<------------ utmiss config end------------>

# 地图相关
-keep class com.dji.mapkit.amap.provider.AMapProvider {*;}
-keep class com.dji.mapkit.maplibre.provider.MapLibreProvider {*;}
-keep class com.dji.mapkit.core.** {*;}
-keep class com.autonavi.** {*;}
-keep class com.alibaba.fastjson.**{*;}


## keep 千寻相关接口
-keep class com.qx.wz.dj.rtcm.** {*;}

# 在项目的proguard-rules.pro文件中添加以下规则

# 排除OpenCV的.so文件
-keep class org.opencv.** { *; }
-keep class org.opencv.engine.** { *; }
-keep class org.opencv.imgproc.** { *; }
-keep class org.opencv.core.** { *; }
-keep class org.opencv.features2d.** { *; }
-keep class org.opencv.ml.** { *; }
-keep class org.opencv.photo.** { *; }
-keep class org.opencv.video.** { *; }
-keep class org.opencv.calib3d.** { *; }
-keep class org.opencv.utils.** { *; }
-keep class org.opencv.flann.** { *; }
-keep class org.opencv.objdetect.** { *; }
-keep class org.opencv.xfeatures2d.** { *; }
-keep class org.opencv.ximgproc.** { *; }
-keep class org.opencv.xphoto.** { *; }

# 如果你有自定义的OpenCV类，确保也要保证这些类不被混淆
-keep class com.example.opencv.** { *; }