# Music_Player_with_Java_for_Android


EROR:

E/AndroidRuntime: FATAL EXCEPTION: main
    Process: com.example.musicplayer, PID: 2299
    java.lang.RuntimeException: Unable to start activity ComponentInfo{com.example.musicplayer/com.example.musicplayer.MainActivity}: java.lang.IllegalArgumentException: Bitmap is not valid
        at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:2646)
        at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:2707)
        at android.app.ActivityThread.-wrap12(ActivityThread.java)
        at android.app.ActivityThread$H.handleMessage(ActivityThread.java:1460)
        at android.os.Handler.dispatchMessage(Handler.java:102)
        at android.os.Looper.loop(Looper.java:154)
        at android.app.ActivityThread.main(ActivityThread.java:6077)
        at java.lang.reflect.Method.invoke(Native Method)
        at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:866)
        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:756)
     Caused by: java.lang.IllegalArgumentException: Bitmap is not valid
        at androidx.palette.graphics.Palette$Builder.<init>(Palette.java:618)
        at androidx.palette.graphics.Palette.from(Palette.java:103)
        at com.example.musicplayer.MainActivity.setVibrantColorOnViews(MainActivity.java:220)
        at com.example.musicplayer.MainActivity.initMusicPlayer(MainActivity.java:156)
        at com.example.musicplayer.MainActivity.onCreate(MainActivity.java:128)
        at android.app.Activity.performCreate(Activity.java:6662)
        at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1118)
        at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:2599)
