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
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity{

    public static String APP_SET = "APP_SET";
    public static PackageManager packageManager;
    public static Gson gson;
    public static Set<String> appSet;
    public static Set<String> selectedAppsSet;
    public static SharedPreferences preferences;
    public static List<ResolveInfo> selectedApps;

    private static boolean hasStarted = false;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private Button startStopButton;

    public void onOffButton(View view) {
        if (!hasStarted) {
            onStartClicked();
            startStopButton.setText("Stop Service");
        } else {
            onStopClicked();
            startStopButton.setText("Start Service");
        }
        hasStarted = !hasStarted;
    }

    public void onStartClicked(){
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
        Intent intent = new Intent(this, AddAppActivity.class);
        startActivity(intent);



    }

    private void minimizeApp() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    public void onStopClicked(){
        this.stopService(new Intent(this, BackgroundService.class));
        this.stopService(new Intent(MainActivity.this, FloatingWindow.class));
        Toast.makeText(this, "Service Stopped!", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startStopButton = findViewById(R.id.buttonStart);
        preferences = getSharedPreferences(APP_SET, MODE_PRIVATE);

        gson = new Gson();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.HORIZONTAL, false));

        packageManager = getPackageManager();

        recyclerViewAdapter = new RecyclerViewAdapter(this, (selectedApps = new ArrayList<>()));
        recyclerView.setAdapter(recyclerViewAdapter);

        getSelectedApps();

    }

    private void getSelectedApps() {

        appSet = preferences.getStringSet(APP_SET, new HashSet<>());
        selectedApps.clear();
        selectedAppsSet = new HashSet<>();
        for(String s : appSet){
            ResolveInfo app = gson.fromJson(s, ResolveInfo.class);
            selectedApps.add(app);
            selectedAppsSet.add(app.activityInfo.packageName);
        }

        selectedApps.sort((o1, o2) -> {

            String app1 = o1.activityInfo.loadLabel(packageManager).toString();
            String app2 = o2.activityInfo.loadLabel(packageManager).toString();

            return app1.compareTo(app2);
        });

        recyclerViewAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        AddAppActivity.save();
        super.onStop();
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

    /**
     *
     * TODO:    1) add pop up menu
     *          2) have its first option as add task which will record the triggers until the icon is clicked again
     *          3) store the saved trigger sequence in some class(make it)
     *           Itna kar le fir or likh dunga
     *
     */

}