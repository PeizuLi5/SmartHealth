package edu.cmpe277.smarthealth.ui.intro;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
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
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import edu.cmpe277.smarthealth.R;
import edu.cmpe277.smarthealth.backend.PatientDbHelper;
import edu.cmpe277.smarthealth.databinding.FragmentIntroBinding;

public class IntroFragment extends Fragment {

    private FragmentIntroBinding binding;
    private IntroViewModel introViewModel;

    private static final String PREFS_NAME = "patient_prefs";
    private static final String KEY_PATIENT_ID = "patient_id";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        introViewModel = new ViewModelProvider(this).get(IntroViewModel.class);
        binding = FragmentIntroBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        introViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        EditText nameEditText = binding.editTextName;
        EditText dateOfBirthEditText = binding.editTextDateOfBirth;
        EditText weightEditText = binding.editTextWeight;
        EditText heightEditText = binding.editTextHeight;
        EditText idNumberEditText = binding.editTextIdNumber;
        EditText emailEditText = binding.editTextEmail;
        EditText phoneNumberEditText = binding.editTextPhoneNumber;
        Button submitButton = binding.buttonSubmit;

        dateOfBirthEditText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(10) });


        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Placeholder
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                introViewModel.setName(s.toString());
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
                introViewModel.setDateOfBirth(s.toString());
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
                    Log.w("HomeFragment", e.getMessage());
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
                introViewModel.setWeight(s.toString());
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
                introViewModel.setHeight(s.toString());
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
                introViewModel.setIdNumber(s.toString());
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
                introViewModel.setEmail(s.toString());
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
                introViewModel.setPhoneNumber(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Placeholder
            }
        });

        submitButton.setOnClickListener((v) -> {
            // Placeholder: Validation logic if needed

            String patientId = generatePatientId();
            savePatientId(patientId);
            saveInformation(patientId);

            Toast.makeText(getContext(), "Information submitted", Toast.LENGTH_SHORT).show();
        });

        return root;
    }

    private void navigateToHome() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
        navController.navigate(R.id.nav_home);
    }

    private void saveInformation(String patientId) {
        PatientDbHelper dbHelper = new PatientDbHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("_id", patientId);
        values.put("name", introViewModel.getName().getValue());
        values.put("date_of_birth", introViewModel.getDateOfBirth().getValue());
        values.put("weight", introViewModel.getWeight().getValue());
        values.put("height", introViewModel.getHeight().getValue());
        values.put("id_number", introViewModel.getIdNumber().getValue());
        values.put("email", introViewModel.getEmail().getValue());
        values.put("phone", introViewModel.getPhoneNumber().getValue());

        db.insert("patients", null, values);
        db.close();

        navigateToHome();
    }

    private String generatePatientId() {
        return UUID.randomUUID().toString();
    }

    private void savePatientId(String patientId) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_PATIENT_ID, patientId);
        editor.apply();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}