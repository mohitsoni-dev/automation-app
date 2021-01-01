package android.example.checkpoint1;
import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class BackgroundService extends Service {

    Handler h;
    int i = 0;
    public static String prevApp = "";

    public static List<String> names = new ArrayList<>();
    public static String currentApp = "";
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        h = new Handler(Looper.myLooper());
        helper();

        return START_STICKY;
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

        if (!names.contains(name)){
            names.add(name);
        }

        prevApp = currentApp;

        // For fixing issues in android < 10
        if (!name.equals("android"))
            currentApp = name;

        Intent intent = new Intent(this, FloatingWindow.class);
        if (MainActivity.selectedAppsSet.contains(currentApp)) {
            if (!prevApp.equals(currentApp)) {
                intent.putExtra("Called", true);
                this.startService(intent);
            }
        } else {
            this.stopService(intent);
        }

        /**
         * TODO:    - remove youtube specificity and generalize this by checking if the name exist in MainActivity.selectedApps
         *          - baaki tujhe jese acha lage isme aage flow m wese kar lena ni apn kuch kar hi lenge
         */

        if(i<10000)
            h.postDelayed(()-> helper(), 1000);
    }

    private boolean checkIfSelected(String currentApp) {
        for (ResolveInfo app : MainActivity.selectedApps) {
            if (app.activityInfo.packageName.equals(currentApp)) return true;
        }
        return false;
    }
}
