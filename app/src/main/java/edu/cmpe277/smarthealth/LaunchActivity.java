package edu.cmpe277.smarthealth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.ChipGroup;

import edu.cmpe277.smarthealth.databinding.ActivityLaunchBinding;

public class LaunchActivity extends AppCompatActivity {
    private ActivityLaunchBinding binding;

    private EditText editTextName, editTextDoB, editTextWeight, editTextHeight;
    private Button buttonSubmit;

    private ChipGroup chipGroup;

    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLaunchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);

        editTextName = binding.editTextName;
        editTextDoB = binding.editTextDateOfBirth;
        editTextWeight = binding.editTextWeight;
        editTextHeight = binding.editTextHeight;
        chipGroup = binding.chipGroup;

        if(chipGroup.getCheckedChipId() == R.id.fiveKChip)
            sharedPreferences.getInt("step", 5000);
        else if(chipGroup.getCheckedChipId() == R.id.eightKChip)
            sharedPreferences.getInt("step", 8000);
        else if(chipGroup.getCheckedChipId() == R.id.tenKChip)
            sharedPreferences.getInt("step", 10000);
        else
            sharedPreferences.getInt("step", 8000);

        buttonSubmit = binding.buttonSubmit;

        buttonSubmit.setOnClickListener((view) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if(!editTextWeight.getText().toString().isEmpty()){
                editor.putInt("weight", Integer.parseInt(editTextWeight.getText().toString()));
            }
            if(!editTextHeight.getText().toString().isEmpty()){
                editor.putInt("height", Integer.parseInt(editTextHeight.getText().toString()));
            }
            if(!editTextName.getText().toString().isEmpty()){
                editor.putString("name", editTextName.getText().toString());
            }
            else{
                editor.putString("name", "User");
            }

            if(!editTextDoB.getText().toString().isEmpty()){
                editor.putString("DoB", editTextDoB.getText().toString());
            }
            editor.putBoolean("isLaunched", false);
            editor.apply();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
