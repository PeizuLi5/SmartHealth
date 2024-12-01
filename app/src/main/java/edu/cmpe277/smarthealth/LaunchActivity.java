package edu.cmpe277.smarthealth;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

import edu.cmpe277.smarthealth.databinding.ActivityLaunchBinding;

public class LaunchActivity extends AppCompatActivity {
    private static final int ACTIVITY_RECOGNITION_POST_NOTIFICATION_PERMISSION = 1;
    private ActivityLaunchBinding binding;

    private EditText editTextName, editTextDoB, editTextWeight, editTextHeight;
    private Button buttonSubmit;

    private TextView setStepTextView;

    private Chip fiveKChip, eightKChip, tenKChip;

    private SharedPreferences sharedPreferences;

    private List<String> permissions = new ArrayList<>();
    private int steps = -1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLaunchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if(ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED){
            permissions.add(android.Manifest.permission.ACTIVITY_RECOGNITION);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

        if(!permissions.isEmpty()){
            ActivityCompat.requestPermissions(this,
                    permissions.toArray(new String[0]),
                    ACTIVITY_RECOGNITION_POST_NOTIFICATION_PERMISSION);
        }

        sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);

        editTextName = binding.editTextName;
        editTextDoB = binding.editTextDateOfBirth;
        editTextWeight = binding.editTextWeight;
        editTextHeight = binding.editTextHeight;
        fiveKChip = binding.fiveKChip;
        eightKChip = binding.eightKChip;
        tenKChip = binding.tenKChip;

        setStepTextView = binding.setStepTextView;

        fiveKChip.setOnClickListener((l) ->{
            setStepTextView.setText("Set your step goal: 5000 steps");
            steps = 5000;
        });

        eightKChip.setOnClickListener((l) ->{
            setStepTextView.setText("Set your step goal: 8000 steps");
            steps = 8000;
        });

        tenKChip.setOnClickListener((l) ->{
            setStepTextView.setText("Set your step goal: 10000 steps");
            steps = 10000;
        });

        buttonSubmit = binding.buttonSubmit;

        buttonSubmit.setOnClickListener((view) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if(!editTextWeight.getText().toString().isEmpty()){
                editor.putInt("weight", Integer.parseInt(editTextWeight.getText().toString()));
            }
            else
                editor.putInt("weight", -1);

            if(!editTextHeight.getText().toString().isEmpty()){
                editor.putInt("height", Integer.parseInt(editTextHeight.getText().toString()));
            }
            else
                editor.putInt("height", -1);

            if(!editTextName.getText().toString().isEmpty()){
                editor.putString("name", editTextName.getText().toString());
            }
            else{
                editor.putString("name", "User");
            }

            if(!editTextDoB.getText().toString().isEmpty()){
                editor.putString("DoB", editTextDoB.getText().toString());
            }

            if(steps != -1)
                editor.putInt("step", steps);
            else
                editor.putInt("step", 7000);

            editor.putBoolean("isLaunched", false);
            editor.apply();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
