


-keep public class com.google.android.gms.* { public *; }
-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}
-dontwarn com.googlecode.mp4parser.**
-keep class com.coremedia.iso.**
-keepclassmembers class com.coremedia.iso.** {*;}
-keep class com.coremedia.iso.** { *; }
-dontwarn com.google.android.gms.**
-dontwarn com.google.common.cache.**
-dontwarn com.google.common.primitives.**

-keepattributes *Annotation*,Signature
#Dependencies
-keep public interface mougram.api$WebService {*;}
-dontwarn com.googlecode.mp4parser.**
-keep class com.android.support.** { *; }
-keep class org.apache.**
-keepclassmembers class org.apache.** {*;}
-keep class org.apache.http.client.methods.HttpPost.** { *; }
-dontwarn org.apache.**
-keep interface com.android.support.** { *; }
-keep class com.github.rey5137.** { *; }
-keep interface com.github.rey5137.** { *; }
-keep class com.wdullaer.** { *; }
-keep interface com.wdullaer.** { *; }
-keep class net.hockeyapp.android.**
-keep interface net.hockeyapp.android.**
#-keep class !android.support.v7.view.menu.**,android.support.v7.** {*;}
#Messanger
-keep class org.zgram.messenger.** { *; }
-keep interface org.zgram.messenger.** { *; }
-keep class org.zgram.PhoneFormat.** { *; }
-keep interface org.zgram.PhoneFormat.** { *; }
-keep class org.zgram.SQLite.** { *; }
-keep interface org.zgram.SQLite.** { *; }
-keep class org.zgram.tgnet.** { *; }
-keep interface org.zgram.tgnet.** { *; }
#UI Activity
-keep class org.zgram.ui.IntroActivity { *; }
-keep class org.zgram.ui.LaunchActivity { *; }
-keep class org.zgram.ui.PhotoViewer { *; }
-keep class org.zgram.ui.ProfileActivity { *; }
-keep class org.zgram.ui.VideoEditorActivity { *; }
-keep class org.zgram.ui.PopupNotificationActivity { *; }
-keep class org.zgram.ui.ManageSpaceActivity { *; }
-keep class org.zgram.ui.StickersActivity { *; }
-keep class org.zgram.ui.StickersActivity$ListAdapter { *; }  #SubClass
#UI ActionBar
-keep class org.zgram.ui.ActionBar.** { *; }
#UI Component
-keep class org.zgram.ui.Components.ChatActivityEnterView { *; }
-keep class org.zgram.ui.Components.ChatActivityEnterView$RecordCircle { *; }
-keep class org.zgram.ui.Components.AnimatedFileDrawable { *; }
-keep class org.zgram.ui.Components.BackupImageView { *; }
-keep class org.zgram.ui.Components.ChatAttachAlert { *; }
-keep class org.zgram.ui.Components.CheckBoxImage { *; }
-keep class org.zgram.ui.Components.PlayerView { *; }
-keep class org.zgram.ui.Components.RecyclerListView { *; }
-keep class org.zgram.ui.Components.URLSpanBotCommand { *; }
-keep class org.zgram.ui.Components.URLSpanNoUnderline { *; }
-keep class org.zgram.ui.Components.URLSpanNoUnderlineBold { *; }
-keep class org.zgram.ui.Components.URLSpanReplacement { *; }
-keep class org.zgram.ui.Components.URLSpanUserMention { *; }
-keep class org.zgram.ui.Components.VideoSeekBarView { *; }
-keep class org.zgram.ui.Components.VideoTimelineView { *; }
-ignorewarnings
-ignorewarnings