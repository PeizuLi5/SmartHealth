package edu.cmpe277.smarthealth.ui.settings;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import edu.cmpe277.smarthealth.backend.PatientDbHelper;
import edu.cmpe277.smarthealth.databinding.FragmentSettingsBinding;

public class SettingsFragment extends Fragment {

    private FragmentSettingsBinding binding;
    private SettingsViewModel settingsViewModel;

    private static final String PREFS_NAME = "patient_prefs";
    private static final String KEY_PATIENT_ID = "patient_id";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        binding.setSettingsViewModel(settingsViewModel);
        View root = binding.getRoot();

        final TextView textView = binding.textSettings;
        settingsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        loadPatient();

        EditText nameEditText = binding.settingsEditTextName;
        EditText dateOfBirthEditText = binding.settingsEditTextDateOfBirth;
        EditText weightEditText = binding.settingsEditTextWeight;
        EditText heightEditText = binding.settingsEditTextHeight;
        EditText idNumberEditText = binding.settingsEditTextIdNumber;
        EditText emailEditText = binding.settingsEditTextEmail;
        EditText phoneNumberEditText = binding.settingsEditTextPhoneNumber;
        Button saveButton = binding.buttonSave;

        dateOfBirthEditText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(10) });


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
                try {
                    SimpleDateFormat inputFormat = new SimpleDateFormat("MMddyyyy", Locale.getDefault());
                    Date date = inputFormat.parse(s.toString());

                    SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                    String formattedDate = outputFormat.format(date);

                    dateOfBirthEditText.removeTextChangedListener(this);
                    dateOfBirthEditText.setText(formattedDate);
                    dateOfBirthEditText.setSelection(formattedDate.length());
                    dateOfBirthEditText.addTextChangedListener(this);

                } catch (ParseException e) {
                    Log.w("SettingsFragment", e.getMessage());
                }
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
        PatientDbHelper dbHelper = new PatientDbHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", settingsViewModel.getName().getValue());
        values.put("date_of_birth", settingsViewModel.getDateOfBirth().getValue());
        values.put("weight", settingsViewModel.getWeight().getValue());
        values.put("height", settingsViewModel.getHeight().getValue());
        values.put("id_number", settingsViewModel.getIdNumber().getValue());
        values.put("email", settingsViewModel.getEmail().getValue());
        values.put("phone", settingsViewModel.getPhoneNumber().getValue());

        String patientId = getPatientId();
        db.update("patients", values, "_id = ?", new String[]{patientId});
        db.close();
    }

    private void loadPatient() {
        PatientDbHelper dbHelper = new PatientDbHelper(getContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query("patients", null, null, null, null, null, null, "1"); // Limit to 1 row
        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            String date_of_birth = cursor.getString(cursor.getColumnIndexOrThrow("date_of_birth"));
            double weight = cursor.getDouble(cursor.getColumnIndexOrThrow("weight"));
            double height = cursor.getDouble(cursor.getColumnIndexOrThrow("height"));
            String idNumber = cursor.getString(cursor.getColumnIndexOrThrow("id_number"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));

            settingsViewModel.getName().setValue(name);
            settingsViewModel.getDateOfBirth().setValue(date_of_birth);
            settingsViewModel.getWeight().setValue(String.valueOf(weight));
            settingsViewModel.getHeight().setValue(String.valueOf(height));
            settingsViewModel.getIdNumber().setValue(idNumber);
            settingsViewModel.getEmail().setValue(email);
            settingsViewModel.getPhoneNumber().setValue(phone);

            Log.d("SettingsFragment", "Loaded patient: " + name + ", " + date_of_birth + ", " + weight + ", ...");
        } else {
            Log.w("SettingsFragment", "No patient data found in the database.");
        }
        cursor.close();
        db.close();
    }

    private String getPatientId() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_PATIENT_ID, null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
