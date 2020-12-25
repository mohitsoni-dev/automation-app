package android.example.checkpoint1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity{
    public static ArrayAdapter<String> adapter;
    public static String APP_SET = "APP_SET";
    public static PackageManager packageManager;
    private RecyclerView recyclerView;
    public static Set<String> appSet;
    SharedPreferences preferences;
    List<ResolveInfo> pkgAppList;

    public void onStartClicked(View view){
        if (!Settings.canDrawOverlays(MainActivity.this)) {
            getPermissionForOverlay();
        }else if (!isAccessGranted()) {
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }else {
            this.startService(new Intent(this, BackgroundService.class));
            minimizeApp();
        }
    }

    public void onAddAppsClicked(View view){


    }

    private void minimizeApp() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    public void onStopClicked(View view){

        this.stopService(new Intent(this, BackgroundService.class));
        this.stopService(new Intent(MainActivity.this, FloatingWindow.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences(APP_SET, MODE_PRIVATE);
        appSet = preferences.getStringSet(APP_SET, new HashSet<>());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL, false));

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

    private boolean isAccessGranted() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            return mode == AppOpsManager.MODE_ALLOWED;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public void getPermissionForOverlay () {
        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:"+getPackageName()));
            startActivityForResult(intent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (!Settings.canDrawOverlays(MainActivity.this)) {
                Toast.makeText(this, "Permission denied by the user.", Toast.LENGTH_LONG).show();
            }
        }
    }

}