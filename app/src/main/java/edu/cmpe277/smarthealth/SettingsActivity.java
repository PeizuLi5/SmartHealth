package edu.cmpe277.smarthealth;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;

import edu.cmpe277.smarthealth.databinding.ActivitySettingsBinding;


public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;

    private EditText nameEditText, dateOfBirthEditText, weightEditText, heightEditText;
    private TextView setStepTextView;

    private Chip fiveKChip, eightKChip, tenKChip;

    private Button saveButton, backButton;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setTitle("Settings");

        sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        String name = sharedPreferences.getString("name", "User");
        String DoB = sharedPreferences.getString("DoB", "");
        int height = sharedPreferences.getInt("height", -1);
        int weight = sharedPreferences.getInt("weight", -1);

        fiveKChip = binding.settingFiveKChip;
        eightKChip = binding.settingEightKChip;
        tenKChip = binding.settingTenKChip;

        setStepTextView = binding.setStepTextView;

        fiveKChip.setOnClickListener((l) ->{
            setStepTextView.setText("Set your step goal: 5000 steps");
        });

        eightKChip.setOnClickListener((l) ->{
            setStepTextView.setText("Set your step goal: 8000 steps");
        });

        tenKChip.setOnClickListener((l) ->{
            setStepTextView.setText("Set your step goal: 10000 steps");
        });

        nameEditText = binding.settingsEditTextName;
        dateOfBirthEditText = binding.settingsEditTextDob;
        weightEditText = binding.settingsEditTextWeight;
        heightEditText = binding.settingsEditTextHeight;

        nameEditText.setText(name);
        dateOfBirthEditText.setText(DoB);
        weightEditText.setText(String.valueOf(weight));
        heightEditText.setText(String.valueOf(height));

        fiveKChip = binding.settingFiveKChip;
        eightKChip = binding.settingEightKChip;
        tenKChip = binding.settingTenKChip;

        backButton = binding.buttonBack;
        backButton.setOnClickListener(view -> finish());


        saveButton = binding.buttonSave;
        saveButton.setOnClickListener((view) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();

            if(!weightEditText.getText().toString().isEmpty()){
                editor.putInt("weight", Integer.parseInt(weightEditText.getText().toString()));
            }
            else
                editor.putInt("weight", -1);

            if(!heightEditText.getText().toString().isEmpty()){
                editor.putInt("height", Integer.parseInt(heightEditText.getText().toString()));
            }
            else
                editor.putInt("height", -1);

            if(!nameEditText.getText().toString().isEmpty()){
                editor.putString("name", nameEditText.getText().toString());
            }
            else{
                editor.putString("name", "User");
            }

            if(!dateOfBirthEditText.getText().toString().isEmpty()){
                editor.putString("DoB", dateOfBirthEditText.getText().toString());
            }

            if(fiveKChip.isChecked())
                editor.putInt("step", 5000);
            else if(eightKChip.isChecked())
                editor.putInt("step", 8000);
            else if(tenKChip.isChecked())
                editor.putInt("step", 10000);
            else
                editor.putInt("step", 7000);

            editor.apply();

            finish();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
