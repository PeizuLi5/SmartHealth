package edu.cmpe277.smarthealth.ui.settings;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import edu.cmpe277.smarthealth.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SettingsViewModel settingsViewModel =
                new ViewModelProvider(this).get(SettingsViewModel.class);
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textSettings;
        settingsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        EditText nameEditText = binding.settingsEditTextName;
        EditText dateOfBirthEditText = binding.settingsEditTextDob;
        EditText weightEditText = binding.settingsEditTextWeight;
        EditText heightEditText = binding.settingsEditTextHeight;
        EditText idNumberEditText = binding.settingsEditTextIdNumber;
        EditText emailEditText = binding.settingsEditTextEmail;
        EditText phoneNumberEditText = binding.settingsEditTextPhoneNumber;
        Button saveButton = binding.buttonSave;


        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Placeholder
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                settingsViewModel.setName(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Placeholder
            }
        });

        dateOfBirthEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Placeholder
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                settingsViewModel.setDateOfBirth(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Placeholder
            }
        });

        weightEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Placeholder
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                settingsViewModel.setWeight(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Placeholder
            }
        });

        heightEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Placeholder
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                settingsViewModel.setHeight(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Placeholder
            }
        });

        idNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Placeholder
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                settingsViewModel.setIdNumber(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Placeholder
            }
        });

        emailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Placeholder
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                settingsViewModel.setEmail(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Placeholder
            }
        });

        phoneNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Placeholder
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                settingsViewModel.setPhoneNumber(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Placeholder
            }
        });

        saveButton.setOnClickListener((v) -> {
            // Placeholder: Validation logic if needed

            saveChanges();

            Toast.makeText(getContext(), "Settings saved", Toast.LENGTH_SHORT).show();
        });

        return root;
    }

    private void saveChanges() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
