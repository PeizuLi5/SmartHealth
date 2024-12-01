package edu.cmpe277.smarthealth;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.widget.Toast;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import edu.cmpe277.smarthealth.databinding.ActivityMainBinding;
import edu.cmpe277.smarthealth.databinding.AppBarMainBinding;
import edu.cmpe277.smarthealth.services.SleepService;
import edu.cmpe277.smarthealth.services.StepCounterService;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private SharedPreferences sharedPreferences;
    private ActivityMainBinding binding;
    private static final int ACTIVITY_RECOGNITION_POST_NOTIFICATION_PERMISSION = 1;

    private TextView userTextView;

    private List<String> permissions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        boolean isFirstRun = sharedPreferences.getBoolean("isLaunched", true);

        if(isFirstRun){
            Intent intent = new Intent(this, LaunchActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        userTextView = navigationView.getHeaderView(0).findViewById(R.id.userTextView);
        userTextView.setText(sharedPreferences.getString("name", "User"));
      
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        if(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED){
            permissions.add(Manifest.permission.ACTIVITY_RECOGNITION);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

        if(!permissions.isEmpty()){
            ActivityCompat.requestPermissions(this,
                    permissions.toArray(new String[0]),
                    ACTIVITY_RECOGNITION_POST_NOTIFICATION_PERMISSION);
        }
        else{
            startStepCounterService();
            startSleepService();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String output = sharedPreferences.getString("name", "User") + sharedPreferences.getInt("step", -1);
        userTextView.setText(output);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        userTextView.setText(sharedPreferences.getString("name", "User"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean allPermissionGranted = true;
        if (requestCode == ACTIVITY_RECOGNITION_POST_NOTIFICATION_PERMISSION) {
            for(int i = 0; i < permissions.length; i++) {
                int result = grantResults[i];

                if(result != PackageManager.PERMISSION_GRANTED){
                    allPermissionGranted = false;
                    break;
                }
            }

            if (allPermissionGranted) {
                startStepCounterService();
                startSleepService();
            }
        }
    }

    private void startStepCounterService() {
        Intent intent = new Intent(this, StepCounterService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    private void startSleepService() {
        Intent intent = new Intent(this, SleepService.class);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            startForegroundService(intent);
        }
        else{
            startService(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        menu.getItem(0).setOnMenuItemClickListener((item) -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return false;
        });
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}