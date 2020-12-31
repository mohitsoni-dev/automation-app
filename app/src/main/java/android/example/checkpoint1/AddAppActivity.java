package android.example.checkpoint1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddAppActivity extends AppCompatActivity {
    public static PackageManager packageManager;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private List<ResolveInfo> pkgAppList;
    private List<ResolveInfo> pkgAppListFiltered;
    private static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_app);

        recyclerView = findViewById(R.id.recyclerViewAddActivity);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        packageManager = getPackageManager();

        getAppsList();

        recyclerViewAdapter = new RecyclerViewAdapter(this, pkgAppListFiltered);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void getAppsList() {

        Intent mainIntent = new Intent(Intent.ACTION_MAIN);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        pkgAppList = getPackageManager().queryIntentActivities(mainIntent, 0);
        pkgAppListFiltered = new ArrayList<>();
        for (ResolveInfo app : pkgAppList) {
            if (!MainActivity.selectedAppsSet.contains(app.activityInfo.packageName)) {
                pkgAppListFiltered.add(app);
            }
        }
        /**pkgAppList.removeAll(MainActivity.selectedApps);
         *
         * TODO: remove all the apps which are already selected. above method is not working find another
         *
         */
//        for (int i = 0; i < pkgAppList.size(); i++) {
//            for (int j = 0; j < MainActivity.selectedApps.size(); j++) {
//                String appA = pkgAppList.get(i).activityInfo.packageName;
//                String appB = MainActivity.selectedApps.get(j).activityInfo.packageName;
//                if (appA.equals(appB)) {
//                    pkgAppList.remove(i);
//                    break;
//                }
//            }
//        }

        pkgAppListFiltered.sort((o1, o2) -> {

            String app1 = o1.activityInfo.loadLabel(packageManager).toString();
            String app2 = o2.activityInfo.loadLabel(packageManager).toString();

            return app1.compareTo(app2);
        });

    }


    @Override
    protected void onStop() {
        save();
        super.onStop();
    }

    public static void save() {

        editor = MainActivity.preferences.edit();

        Set<String> selectedApps = new HashSet<>();

        for(ResolveInfo s : MainActivity.selectedApps){
            String app = MainActivity.gson.toJson(s);
            selectedApps.add(app);
        }

        editor.putStringSet(MainActivity.APP_SET, selectedApps);
        editor.apply();
    }
}