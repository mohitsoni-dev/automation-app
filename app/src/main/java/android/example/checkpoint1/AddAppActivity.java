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
    public static Set<String> appSet;
    SharedPreferences preferences;
    public static PackageManager packageManager;
    private RecyclerView recyclerView;
    public static String APP_SET = "APP_SET";
    List<ResolveInfo> pkgAppList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_app);

        preferences = getSharedPreferences(APP_SET, MODE_PRIVATE);
        appSet = preferences.getStringSet(APP_SET, new HashSet<>());

        recyclerView = findViewById(R.id.recyclerViewAddActivity);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        packageManager = getPackageManager();

        Intent mainIntent = new Intent(Intent.ACTION_MAIN);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        pkgAppList = getPackageManager().queryIntentActivities(mainIntent, 0);
        pkgAppList.sort((o1, o2) -> {

            String app1 = o1.activityInfo.loadLabel(packageManager).toString();
            String app2 = o2.activityInfo.loadLabel(packageManager).toString();

            return app1.compareTo(app2);
        });
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(this, pkgAppList);
        recyclerView.setAdapter(recyclerViewAdapter);
    }
}