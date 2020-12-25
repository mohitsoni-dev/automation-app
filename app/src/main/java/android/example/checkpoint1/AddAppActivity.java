package android.example.checkpoint1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddAppActivity extends AppCompatActivity {
    public static PackageManager packageManager;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private List<ResolveInfo> pkgAppList;
    private static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_app);

        recyclerView = findViewById(R.id.recyclerViewAddActivity);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        packageManager = getPackageManager();

        getAppsList();

        recyclerViewAdapter = new RecyclerViewAdapter(this, pkgAppList);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void getAppsList() {

        Intent mainIntent = new Intent(Intent.ACTION_MAIN);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        pkgAppList = getPackageManager().queryIntentActivities(mainIntent, 0);
        /**pkgAppList.removeAll(MainActivity.selectedApps);
         *
         * TODO: remove all the apps which are already selected. above method is not working find another
         *
         */
        pkgAppList.sort((o1, o2) -> {

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