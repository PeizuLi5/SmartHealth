package edu.cmpe277.smarthealth;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import edu.cmpe277.smarthealth.databinding.ActivitySettingsBinding;


public class SettingsFragment extends AppCompatActivity {

    private ActivitySettingsBinding binding;

    private TextView nameEditText, dateOfBirthEditText, weightEditText, heightEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final TextView textView = binding.textSettings;

        nameEditText = binding.settingsEditTextName;
        dateOfBirthEditText = binding.settingsEditTextDob;
        weightEditText = binding.settingsEditTextWeight;
        heightEditText = binding.settingsEditTextHeight;
        EditText idNumberEditText = binding.settingsEditTextIdNumber;
        EditText emailEditText = binding.settingsEditTextEmail;
        EditText phoneNumberEditText = binding.settingsEditTextPhoneNumber;
        Button saveButton = binding.buttonSave;
    }

    private void saveChanges() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
