package android.example.checkpoint1;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class BackgroundService extends Service {

    Handler h;
    int i = 0;

    WindowManager wm;
    ImageView iconImageView;

    public static List<String> names = new ArrayList<>();
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        h = new Handler(Looper.myLooper());
        //helper();

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();


        wm = (WindowManager) getBaseContext().getSystemService(WINDOW_SERVICE);
        iconImageView =  new ImageView(getApplicationContext());
        iconImageView.setImageResource(android.R.drawable.star_big_on);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                100,
                100,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY|
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM |
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|

                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT
        );
        layoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        wm.addView(iconImageView, layoutParams);

    }

    @Override
    public void onDestroy() {
        i = 10000;
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void helper () {
        i++;
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        String name = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) getSystemService(USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                    time - 1000 * 1000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(),
                            usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    name = mySortedMap.get(
                            mySortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            am = (ActivityManager) getBaseContext().getSystemService(ACTIVITY_SERVICE);
            name = am.getRunningTasks(1).get(0).topActivity .getPackageName();
        }
        if(!names.contains(name))
            names.add(name);

        MainActivity.adapter.notifyDataSetChanged();

        if(i<10000)
            h.postDelayed(()-> helper(), 1000);
    }
}
