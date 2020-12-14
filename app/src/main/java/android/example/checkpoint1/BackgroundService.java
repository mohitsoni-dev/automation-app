package android.example.checkpoint1;
import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class BackgroundService extends Service {

    Handler h;
    int i = 0;
    public static List<String> names = new ArrayList<>();
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        h = new Handler(Looper.myLooper());
        helper();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
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
